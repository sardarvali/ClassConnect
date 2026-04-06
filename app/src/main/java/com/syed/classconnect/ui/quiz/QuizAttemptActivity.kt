package com.syed.classconnect.ui.quiz

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.syed.classconnect.data.model.QuizAttempt
import com.syed.classconnect.databinding.ActivityQuizAttemptBinding
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.DateUtils.formatDuration
import com.syed.classconnect.util.NetworkResult
import com.syed.classconnect.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QuizAttemptActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizAttemptBinding
    private lateinit var viewModel: QuizViewModel
    @Inject
    lateinit var auth: FirebaseAuth

    private var classId = ""
    private var quizId = ""
    private val answers = mutableMapOf<Int, Int>()
    private var currentQuestion = 0
    private var timer: CountDownTimer? = null
    private val startedAt = Timestamp.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizAttemptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[QuizViewModel::class.java]
        classId = intent.getStringExtra(Constants.EXTRA_CLASS_ID) ?: run { finish(); return }
        quizId = intent.getStringExtra(Constants.EXTRA_QUIZ_ID) ?: run { finish(); return }
        viewModel.loadQuizDetail(classId, quizId)

        viewModel.quizDetail.observe(this) { quiz ->
            quiz ?: return@observe
            if (!quiz.published) {
                showToast("This quiz is not available")
                finish()
                return@observe
            }

            // Validate time limits
            val now = Timestamp.now()
            if (quiz.startTime != null && now.seconds < quiz.startTime.seconds) {
                showToast("This quiz is not open yet")
                finish()
                return@observe
            }
            if (quiz.endTime != null && now.seconds > quiz.endTime.seconds) {
                showToast("This quiz is already closed")
                finish()
                return@observe
            }
            startTimer(quiz.durationMinutes * 60L)
            loadDraftIfExists()   // Restore saved answers if any
            showQuestion(quiz, currentQuestion)

            binding.btnPrev.setOnClickListener {
                if (currentQuestion > 0) {
                    currentQuestion--; showQuestion(quiz, currentQuestion)
                }
            }
            binding.btnNext.setOnClickListener {
                if (currentQuestion < quiz.questions.size - 1) {
                    currentQuestion++; showQuestion(quiz, currentQuestion)
                }
            }
            binding.btnSubmit.setOnClickListener { confirmSubmit(quiz.questions.size) }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialog.Builder(this@QuizAttemptActivity)
                    .setTitle("Exit Quiz")
                    .setMessage("Your answers are saved. You can resume from where you left off.")
                    .setPositiveButton("Exit") { _, _ ->
                        saveDraftToFirestore()
                        finish()
                    }
                    .setNegativeButton("Continue Quiz", null).show()
            }
        })
    }

    private fun showQuestion(quiz: com.syed.classconnect.data.model.Quiz, index: Int) {
        val q = quiz.questions[index]
        binding.tvQuestion.text = q.question
        binding.tvProgress.text =
            getString(com.syed.classconnect.R.string.question_of, index + 1, quiz.questions.size)
        binding.progressBar.max = quiz.questions.size
        binding.progressBar.progress = index + 1

        val optionViews =
            listOf(binding.rbOption0, binding.rbOption1, binding.rbOption2, binding.rbOption3)
        binding.rgOptions.clearCheck()
        optionViews.forEachIndexed { i, rb ->
            rb.text = q.options.getOrElse(i) { "" }
            rb.isChecked = answers[index] == i
        }
        binding.rgOptions.setOnCheckedChangeListener { _, checkedId ->
            val selected = optionViews.indexOfFirst { it.id == checkedId }
            if (selected >= 0) answers[index] = selected
        }

        binding.btnPrev.isEnabled = index > 0
        binding.btnSubmit.visibility =
            if (index == quiz.questions.size - 1) View.VISIBLE else View.GONE
    }

    private fun startTimer(totalSeconds: Long) {
        timer = object : CountDownTimer(totalSeconds * 1000, 1000) {
            override fun onTick(ms: Long) {
                val secs = ms / 1000
                binding.tvTimer.text =
                    getString(com.syed.classconnect.R.string.time_remaining, formatDuration(secs))
                if (secs <= 30) binding.tvTimer.setTextColor(getColor(com.syed.classconnect.R.color.error))
            }

            override fun onFinish() {
                submitQuiz()
            }
        }.start()
    }

    private fun confirmSubmit(total: Int) {
        AlertDialog.Builder(this)
            .setMessage(getString(com.syed.classconnect.R.string.confirm_submit_quiz))
            .setPositiveButton("Submit") { _, _ -> submitQuiz() }
            .setNegativeButton("Cancel", null).show()
    }

    private fun submitQuiz() {
        timer?.cancel()
        val uid = auth.currentUser?.uid ?: return
        val quiz = viewModel.quizDetail.value ?: return
        var score = 0
        quiz.questions.forEachIndexed { i, q ->
            if (answers[i] == q.correctIndex) score += q.marks
        }

        // Fetch student name before submitting
        val firestore = FirebaseFirestore.getInstance()
        val attempt = QuizAttempt(
            studentId = uid, startedAt = startedAt,
            submittedAt = Timestamp.now(),
            answers = answers.mapKeys { it.key.toString() },
            score = score, totalMarks = quiz.totalMarks
        )

        // Try to get student name for results display
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { userDoc ->
                val studentName = userDoc.getString("name") ?: ""
                val enrichedAttempt = attempt.copy(studentName = studentName)
                viewModel.submitAttempt(classId, quizId, enrichedAttempt)
            }
            .addOnFailureListener {
                viewModel.submitAttempt(classId, quizId, attempt)
            }

        viewModel.submitResult.observe(this) { result ->
            if (result is NetworkResult.Success) {
                val fragment =
                    QuizResultFragment.newInstance(classId, quizId, uid, score, quiz.totalMarks)
                fragment.show(supportFragmentManager, "quiz_result")
                supportFragmentManager.setFragmentResultListener(
                    "quiz_result_dismissed",
                    this
                ) { _, _ ->
                    finish()
                }
            }
        }
    }

    /** Save current answers as a draft to Firestore so student can resume later */
    private fun saveDraftToFirestore() {
        val uid = auth.currentUser?.uid ?: return
        if (answers.isEmpty()) return
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("classes/$classId/quizzes/$quizId/attempts")
            .document(uid)
            .set(
                mapOf(
                    "studentId" to uid,
                    "answers" to answers.mapKeys { it.key.toString() },
                    "isDraft" to true,
                    "startedAt" to startedAt
                ), SetOptions.merge()
            )
    }

    /** Load a previously saved draft and restore answers */
    private fun loadDraftIfExists() {
        val uid = auth.currentUser?.uid ?: return
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("classes/$classId/quizzes/$quizId/attempts")
            .document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists() && doc.getBoolean("isDraft") == true) {
                    @Suppress("UNCHECKED_CAST")
                    val saved =
                        doc.get("answers") as? Map<String, Long> ?: return@addOnSuccessListener
                    saved.forEach { (key, value) ->
                        val index = key.toIntOrNull() ?: return@forEach
                        answers[index] = value.toInt()
                    }
                    // Refresh current question to show restored answer
                    viewModel.quizDetail.value?.let { quiz ->
                        showQuestion(quiz, currentQuestion)
                    }
                    com.google.android.material.snackbar.Snackbar.make(
                        binding.root,
                        "Resuming saved progress",
                        com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onDestroy() {
        timer?.cancel(); super.onDestroy()
    }
}
