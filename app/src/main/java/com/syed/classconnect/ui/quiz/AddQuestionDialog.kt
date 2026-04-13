package com.syed.classconnect.ui.quiz

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.syed.classconnect.data.model.QuizQuestion
import com.syed.classconnect.databinding.DialogAddQuestionBinding

class AddQuestionDialog(private val onAdd: (QuizQuestion) -> Unit) : DialogFragment() {

    private var _binding: DialogAddQuestionBinding? = null
    private val binding get() = _binding!!

    private var initialQuestion: QuizQuestion? = null

    constructor(
        initialQuestion: QuizQuestion,
        onSave: (QuizQuestion) -> Unit
    ) : this(onSave) {
        this.initialQuestion = initialQuestion
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddQuestionBinding.inflate(LayoutInflater.from(context))
        val currentQuestion = initialQuestion
        currentQuestion?.let { question ->
            binding.etQuestion.setText(question.question)
            binding.etOptionA.setText(question.options.getOrNull(0).orEmpty())
            binding.etOptionB.setText(question.options.getOrNull(1).orEmpty())
            binding.etOptionC.setText(question.options.getOrNull(2).orEmpty())
            binding.etOptionD.setText(question.options.getOrNull(3).orEmpty())
            binding.etMarks.setText(question.marks.toString())
            when (question.correctIndex) {
                0 -> binding.rbA.isChecked = true
                1 -> binding.rbB.isChecked = true
                2 -> binding.rbC.isChecked = true
                else -> binding.rbD.isChecked = true
            }
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(if (currentQuestion == null) "Add Question" else "Edit Question")
            .setView(binding.root)
            .setPositiveButton(
                if (currentQuestion == null) "Add" else "Save",
                null
            ) // Set null here
            .setNegativeButton("Cancel", null)
            .create()
    }

    override fun onResume() {
        super.onResume()
        val dialog = dialog as? AlertDialog
        dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            val question = binding.etQuestion.text.toString().trim()
            val opts = listOf(
                binding.etOptionA.text.toString().trim(),
                binding.etOptionB.text.toString().trim(),
                binding.etOptionC.text.toString().trim(),
                binding.etOptionD.text.toString().trim()
            )
            // Filter out empty options
            val validOptions = opts.filter { it.isNotEmpty() }
            val marks = binding.etMarks.text.toString().toIntOrNull()?.coerceAtLeast(1) ?: 1

            // Re-map correct index based on non-empty options if needed, but let's just keep the raw index
            val rawCorrect = when (binding.rgCorrect.checkedRadioButtonId) {
                binding.rbA.id -> 0
                binding.rbB.id -> 1
                binding.rbC.id -> 2
                else -> 3
            }
            // Ensure correct index is within valid bounds
            val finalCorrect =
                if (validOptions.isNotEmpty()) rawCorrect.coerceAtMost(validOptions.lastIndex) else 0

            if (question.isEmpty()) {
                binding.etQuestion.error = "Question required"
                return@setOnClickListener
            }
            if (validOptions.size < 2) {
                com.google.android.material.snackbar.Snackbar.make(
                    binding.root,
                    "At least 2 options required",
                    com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            onAdd(
                QuizQuestion(
                    question = question,
                    options = validOptions,
                    correctIndex = finalCorrect,
                    marks = marks
                )
            )
            dialog.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView(); _binding = null
    }
}
