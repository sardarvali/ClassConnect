package com.syed.classconnect.ui.ai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.syed.classconnect.R
import com.syed.classconnect.databinding.BottomSheetAiActionsBinding
import com.syed.classconnect.databinding.DialogAiQuizConfigBinding
import com.syed.classconnect.databinding.DialogAiAssignmentConfigBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AIActionsSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAiActionsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AIViewModel by activityViewModels()

    companion object {
        private const val ARG_CONTENT = "content"
        private const val ARG_TOPIC   = "topic"
        private const val ARG_MODE    = "mode"

        const val MODE_GENERIC = "generic"
        const val MODE_LESSON_PLAN = "lesson_plan"

        fun newInstance(content: String, topic: String = "", mode: String = MODE_GENERIC) =
            AIActionsSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_CONTENT, content)
                    putString(ARG_TOPIC, topic)
                    putString(ARG_MODE, mode)
                }
            }
    }

    private val content get() = arguments?.getString(ARG_CONTENT) ?: ""
    private val topic   get() = arguments?.getString(ARG_TOPIC)   ?: ""
    private val mode    get() = arguments?.getString(ARG_MODE) ?: MODE_GENERIC

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAiActionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadClassesForCurrentUser()
        bindLabels()

        binding.cardShareChat.setOnClickListener     { showShareToChatDialog()    }
        binding.cardMakeAssignment.setOnClickListener { showMakeAssignmentDialog() }
        binding.cardMakeQuiz.setOnClickListener      { showMakeQuizDialog()       }

        viewModel.actionResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AIActionResult.Loading -> showLoading(true)
                is AIActionResult.Success -> {
                    showLoading(false)
                    Snackbar.make(requireView(), result.message, Snackbar.LENGTH_LONG).show()
                    viewModel.clearActionResult()
                    dismiss()
                }
                is AIActionResult.Error -> {
                    showLoading(false)
                    Snackbar.make(requireView(), result.message, Snackbar.LENGTH_LONG).show()
                    viewModel.clearActionResult()
                }
                null -> showLoading(false)
            }
        }
    }

    private fun bindLabels() {
        if (mode == MODE_LESSON_PLAN) {
            binding.tvHeaderTitle.setText(R.string.ai_actions_header_lesson_plan)
            binding.tvShareTitle.setText(R.string.ai_actions_share_lesson_plan)
            binding.tvShareSubtitle.setText(R.string.ai_actions_share_lesson_plan_subtitle)
            binding.tvAssignmentTitle.setText(R.string.ai_actions_assignment_lesson_plan)
            binding.tvAssignmentSubtitle.setText(R.string.ai_actions_assignment_lesson_plan_subtitle)
            binding.tvQuizTitle.setText(R.string.ai_actions_quiz_lesson_plan)
            binding.tvQuizSubtitle.setText(R.string.ai_actions_quiz_lesson_plan_subtitle)
        } else {
            binding.tvHeaderTitle.setText(R.string.ai_actions_header_generic)
            binding.tvShareTitle.setText(R.string.ai_actions_share_generic)
            binding.tvShareSubtitle.setText(R.string.ai_actions_share_generic_subtitle)
            binding.tvAssignmentTitle.setText(R.string.ai_actions_assignment_generic)
            binding.tvAssignmentSubtitle.setText(R.string.ai_actions_assignment_generic_subtitle)
            binding.tvQuizTitle.setText(R.string.ai_actions_quiz_generic)
            binding.tvQuizSubtitle.setText(R.string.ai_actions_quiz_generic_subtitle)
        }
    }

    // ── Share to Chat ─────────────────────────────────────────────────────────
    private fun showShareToChatDialog() {
        val classes = viewModel.availableClasses.value ?: emptyList()
        if (classes.isEmpty()) { showNoClassesWarning(); return }

        val names = classes.map { "${it.name} (${it.subject})" }.toTypedArray()
        var selectedIndex = 0
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (mode == MODE_LESSON_PLAN) "Share Lesson Plan" else "Share to Class Chat")
            .setSingleChoiceItems(names, 0) { _, which -> selectedIndex = which }
            .setPositiveButton("Share") { _, _ ->
                viewModel.shareToChat(classes[selectedIndex].id, content)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ── Make Quiz ─────────────────────────────────────────────────────────────
    private fun showMakeQuizDialog() {
        val classes = viewModel.availableClasses.value ?: emptyList()
        if (classes.isEmpty()) { showNoClassesWarning(); return }

        val b = DialogAiQuizConfigBinding.inflate(layoutInflater)
        b.etQuizTitle.setText(if (topic.isNotBlank()) "Quiz: $topic" else "AI Quiz")
        b.etQuizDuration.setText(30.toString())
        b.spinnerClass.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            classes.map { "${it.name} — ${it.subject}" }
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (mode == MODE_LESSON_PLAN) "Create Quiz from Lesson Plan" else "Generate AI Quiz")
            .setView(b.root)
            .setPositiveButton("Create") { _, _ ->
                val title    = b.etQuizTitle.text.toString().trim().ifEmpty { "AI Quiz" }
                val duration = b.etQuizDuration.text.toString().toIntOrNull() ?: 30
                val cls      = classes[b.spinnerClass.selectedItemPosition]
                viewModel.createQuizFromAI(
                    classId = cls.id,
                    topic = topic.ifBlank { title },
                    quizTitle = title,
                    durationMinutes = duration,
                    sourceContent = content
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ── Make Assignment ───────────────────────────────────────────────────────
    private fun showMakeAssignmentDialog() {
        val classes = viewModel.availableClasses.value ?: emptyList()
        if (classes.isEmpty()) { showNoClassesWarning(); return }

        val b = DialogAiAssignmentConfigBinding.inflate(layoutInflater)
        b.etAssignmentTitle.setText(if (topic.isNotBlank()) "Assignment: $topic" else "AI Assignment")
        b.etAssignmentMarks.setText(100.toString())
        b.spinnerClass.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            classes.map { "${it.name} — ${it.subject}" }
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (mode == MODE_LESSON_PLAN) "Create Assignment from Plan" else "Create Assignment and Post")
            .setView(b.root)
            .setPositiveButton("Create & Post") { _, _ ->
                val title = b.etAssignmentTitle.text.toString().trim().ifEmpty { "AI Assignment" }
                val marks = b.etAssignmentMarks.text.toString().toIntOrNull() ?: 100
                val cls   = classes[b.spinnerClass.selectedItemPosition]
                viewModel.createAssignmentAndPostFromAI(cls.id, title, content, marks)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private fun showNoClassesWarning() {
        Snackbar.make(requireView(), "No classes found. Join or create a class first.", Snackbar.LENGTH_SHORT).show()
    }

    private fun showLoading(show: Boolean) {
        binding.layoutLoading.visibility = if (show) View.VISIBLE else View.GONE
        binding.layoutActions.visibility = if (show) View.GONE   else View.VISIBLE
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
