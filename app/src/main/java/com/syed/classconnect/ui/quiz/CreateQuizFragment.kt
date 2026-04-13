package com.syed.classconnect.ui.quiz

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.syed.classconnect.data.model.Quiz
import com.syed.classconnect.data.model.QuizQuestion
import com.syed.classconnect.databinding.FragmentCreateQuizBinding
import com.syed.classconnect.ui.ai.AISuggestionsSheet
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable
import java.util.Calendar

@AndroidEntryPoint
class CreateQuizFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentCreateQuizBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuizViewModel by viewModels()
    private lateinit var classId: String
    private val questions = mutableListOf<QuizQuestion>()
    private var existingQuiz: Quiz? = null
    private lateinit var questionAdapter: EditableQuestionsAdapter
    private var startCalendar: Calendar? = null
    private var endCalendar: Calendar? = null

    companion object {
        private const val ARG_CLASS_ID = "classId"
        private const val ARG_EXISTING_QUIZ = "existing_quiz"

        fun newInstance(classId: String) = CreateQuizFragment().apply {
            arguments = Bundle().apply { putString(ARG_CLASS_ID, classId) }
        }

        fun newInstanceForEdit(classId: String, quiz: Quiz) = CreateQuizFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_CLASS_ID, classId)
                putSerializable(ARG_EXISTING_QUIZ, quiz as Serializable)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classId = arguments?.getString(ARG_CLASS_ID) ?: return

        questionAdapter = EditableQuestionsAdapter(
            onEdit = { index, question -> showEditQuestionDialog(index, question) },
            onDelete = { index, _ ->
                questions.removeAt(index)
                refreshQuestionsUi()
            }
        )
        binding.rvQuestions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvQuestions.adapter = questionAdapter

        @Suppress("DEPRECATION")
        existingQuiz = arguments?.getSerializable(ARG_EXISTING_QUIZ) as? Quiz
        existingQuiz?.let { quiz ->
            binding.etTitle.setText(quiz.title)
            binding.etDescription.setText(quiz.description)
            binding.etDuration.setText(quiz.durationMinutes.toString())
            questions.addAll(quiz.questions)
            startCalendar = quiz.startTime?.toDate()?.let {
                Calendar.getInstance().apply { time = it }
            }
            endCalendar = quiz.endTime?.toDate()?.let {
                Calendar.getInstance().apply { time = it }
            }
        }

        refreshQuestionsUi()
        updateScheduleUi()

        binding.btnAddQuestion.setOnClickListener { showAddQuestionDialog() }
        binding.btnPublish.setOnClickListener { saveQuiz(published = true) }
        binding.btnSaveDraft.setOnClickListener { saveQuiz(published = false) }
        binding.btnPickStart.setOnClickListener { pickDateTime(true) }
        binding.btnPickEnd.setOnClickListener { pickDateTime(false) }
        binding.btnClearSchedule.setOnClickListener {
            startCalendar = null
            endCalendar = null
            updateScheduleUi()
        }

        binding.btnAiSuggest.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val desc = binding.etDescription.text.toString().trim()
            val ctx = buildString {
                if (title.isNotBlank()) append("Title: $title\n")
                if (desc.isNotBlank()) append("Description: $desc")
                if (isEmpty()) append("A new quiz")
            }
            val sheet = AISuggestionsSheet.newInstance("quiz", ctx)
            sheet.onPickQuiz = { suggestion ->
                binding.etTitle.setText(suggestion.title)
                binding.etDescription.setText(suggestion.description)
                questions.clear()
                questions.addAll(suggestion.questions)
                refreshQuestionsUi()
                Snackbar.make(
                    binding.root,
                    "AI quiz loaded with ${suggestion.questions.size} questions",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            sheet.show(childFragmentManager, "ai_quiz_suggest")
        }
    }

    private fun showAddQuestionDialog() {
        AddQuestionDialog { q ->
            questions.add(q)
            refreshQuestionsUi()
        }.show(parentFragmentManager, "add_question")
    }

    private fun showEditQuestionDialog(index: Int, question: QuizQuestion) {
        AddQuestionDialog(question) { updatedQuestion ->
            questions[index] = updatedQuestion
            refreshQuestionsUi()
        }.show(parentFragmentManager, "edit_question_$index")
    }

    private fun refreshQuestionsUi() {
        binding.tvQuestionCount.text = when (questions.size) {
            0 -> "0 question(s) added"
            1 -> "1 question ready"
            else -> "${questions.size} questions ready"
        }
        questionAdapter.submitList(questions.toList())
    }

    private fun pickDateTime(isStart: Boolean) {
        val calendar = (if (isStart) startCalendar else endCalendar)?.clone() as? Calendar
            ?: Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar[Calendar.YEAR] = year
                calendar[Calendar.MONTH] = month
                calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                TimePickerDialog(
                    requireContext(),
                    { _, hour, minute ->
                        calendar[Calendar.HOUR_OF_DAY] = hour
                        calendar[Calendar.MINUTE] = minute
                        calendar[Calendar.SECOND] = 0
                        calendar[Calendar.MILLISECOND] = 0
                        if (isStart) startCalendar = calendar else endCalendar = calendar
                        updateScheduleUi()
                    },
                    calendar[Calendar.HOUR_OF_DAY],
                    calendar[Calendar.MINUTE],
                    false
                ).show()
            },
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        ).show()
    }

    private fun updateScheduleUi() {
        binding.btnPickStart.text = startCalendar?.let { formatCalendar(it) } ?: "Start time"
        binding.btnPickEnd.text = endCalendar?.let { formatCalendar(it) } ?: "End time"
        binding.tvScheduleSummary.text = when {
            startCalendar != null && endCalendar != null ->
                "Scheduled from ${formatCalendar(startCalendar!!)} to ${formatCalendar(endCalendar!!)}"

            startCalendar != null -> "Starts at ${formatCalendar(startCalendar!!)}"
            endCalendar != null -> "Ends at ${formatCalendar(endCalendar!!)}"
            else -> "No schedule set. Students can open it after publishing."
        }
    }

    private fun formatCalendar(calendar: Calendar): String =
        android.text.format.DateFormat.format("MMM dd, yyyy hh:mm a", calendar).toString()

    private fun saveQuiz(published: Boolean) {
        val title = binding.etTitle.text.toString().trim()
        val desc = binding.etDescription.text.toString().trim()
        val duration = binding.etDuration.text.toString().toIntOrNull() ?: 30
        if (title.isEmpty()) {
            binding.etTitle.error = "Required"
            return
        }
        if (questions.isEmpty()) {
            Snackbar.make(binding.root, "Add at least one question", Snackbar.LENGTH_SHORT).show()
            return
        }
        if (startCalendar != null && endCalendar != null && startCalendar!!.after(endCalendar)) {
            Snackbar.make(
                binding.root,
                "End time must be after the start time",
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }
        val marks = questions.sumOf { it.marks }
        val existing = existingQuiz
        if (existing != null) {
            viewModel.updateQuiz(
                classId, existing.copy(
                    title = title, description = desc,
                    durationMinutes = duration, totalMarks = marks,
                    startTime = startCalendar?.let { Timestamp(it.time) },
                    endTime = endCalendar?.let { Timestamp(it.time) },
                    published = published, questions = questions.toList()
                )
            )
        } else {
            viewModel.createQuiz(
                classId, Quiz(
                    title = title, description = desc,
                    durationMinutes = duration, totalMarks = marks,
                    startTime = startCalendar?.let { Timestamp(it.time) },
                    endTime = endCalendar?.let { Timestamp(it.time) },
                    published = published, questions = questions.toList(),
                    createdAt = Timestamp.now()
                )
            )
        }
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView(); _binding = null
    }
}
