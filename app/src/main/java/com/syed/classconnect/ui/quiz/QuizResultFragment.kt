package com.syed.classconnect.ui.quiz

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.syed.classconnect.databinding.FragmentQuizResultBinding
import com.syed.classconnect.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizResultFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentQuizResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuizViewModel by viewModels()
    private lateinit var classId: String
    private lateinit var quizId: String
    private lateinit var studentId: String

    companion object {
        private const val ARG_STUDENT_ID = "student_id"
        private const val ARG_SCORE = "score"
        private const val ARG_TOTAL = "total"
        fun newInstance(
            classId: String,
            quizId: String,
            studentId: String,
            score: Int,
            total: Int
        ) =
            QuizResultFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.EXTRA_CLASS_ID, classId)
                    putString(Constants.EXTRA_QUIZ_ID, quizId)
                    putString(ARG_STUDENT_ID, studentId)
                    putInt(ARG_SCORE, score)
                    putInt(ARG_TOTAL, total)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classId = arguments?.getString(Constants.EXTRA_CLASS_ID) ?: return
        quizId = arguments?.getString(Constants.EXTRA_QUIZ_ID) ?: return
        studentId = arguments?.getString(ARG_STUDENT_ID) ?: return
        val score = arguments?.getInt(ARG_SCORE, 0) ?: 0
        val total = arguments?.getInt(ARG_TOTAL, 0) ?: 0

        // Animated score counter
        animateScore(0, score)

        val percentage = if (total > 0) (score * 100 / total) else 0
        binding.progressCircular.max = 100
        binding.progressCircular.setProgress(percentage, true)
        binding.tvPercentage.text = "$percentage%"
        binding.tvScoreDetail.text = "$score / $total"

        val feedback = when {
            percentage >= 90 -> "Excellent! Outstanding performance! 🏆"
            percentage >= 75 -> "Great job! Keep it up! 🎯"
            percentage >= 60 -> "Good work! Room for improvement. 📈"
            percentage >= 40 -> "Fair attempt. Study more for better results. 📚"
            else -> "Keep practicing! You can do better. 💪"
        }
        binding.tvFeedback.text = feedback

        // Entrance animations for result elements
        listOf(binding.tvScoreDetail, binding.tvFeedback, binding.btnDone).forEachIndexed { i, v ->
            v.alpha = 0f; v.translationY = 30f
            v.animate().alpha(1f).translationY(0f)
                .setDuration(400).setStartDelay(800L + i * 100L)
                .setInterpolator(DecelerateInterpolator(2f)).start()
        }
        // Score card pop-in
        binding.root.findViewById<View>(com.syed.classconnect.R.id.tv_score)?.let { sv ->
            sv.scaleX = 0f; sv.scaleY = 0f
            sv.animate().scaleX(1f).scaleY(1f)
                .setDuration(500).setStartDelay(300)
                .setInterpolator(OvershootInterpolator(2f)).start()
        }

        binding.btnDone.setOnClickListener {
            parentFragmentManager.setFragmentResult("quiz_result_dismissed", Bundle())
            dismiss()
        }

        // Load attempt details for answer review
        viewModel.loadAttempt(classId, quizId, studentId)
        viewModel.loadQuizDetail(classId, quizId)
    }

    private fun animateScore(from: Int, to: Int) {
        ValueAnimator.ofInt(from, to).apply {
            duration = 1200
            interpolator = DecelerateInterpolator(2f)
            addUpdateListener { binding.tvScore.text = it.animatedValue.toString() }
            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView(); _binding = null
    }
}

