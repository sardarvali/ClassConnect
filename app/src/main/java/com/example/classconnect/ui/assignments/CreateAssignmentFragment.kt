package com.syed.classconnect.ui.assignments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Timestamp
import com.syed.classconnect.R
import com.syed.classconnect.data.model.Assignment
import com.syed.classconnect.databinding.FragmentCreateAssignmentBinding
import com.syed.classconnect.ui.ai.AISuggestionsSheet
import com.syed.classconnect.ui.ai.AIViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CreateAssignmentFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentCreateAssignmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AssignmentsViewModel by viewModels()
    private val aiViewModel: AIViewModel by activityViewModels()

    private lateinit var classId: String
    private var dueDate: Calendar = Calendar.getInstance().also { it.add(Calendar.DAY_OF_MONTH, 7) }
    private var existingAssignment: Assignment? = null

    companion object {
        private const val ARG_CLASS_ID   = "classId"
        private const val ARG_ASSIGNMENT = "assignment"

        fun newInstance(classId: String) = CreateAssignmentFragment().apply {
            arguments = Bundle().apply { putString(ARG_CLASS_ID, classId) }
        }

        fun newInstanceForEdit(classId: String, assignment: Assignment) =
            CreateAssignmentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CLASS_ID, classId)
                    putString(ARG_ASSIGNMENT, com.google.gson.Gson().toJson(assignment))
                }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateAssignmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classId = arguments?.getString(ARG_CLASS_ID) ?: return

        arguments?.getString(ARG_ASSIGNMENT)?.let { json ->
            existingAssignment = com.google.gson.Gson().fromJson(json, Assignment::class.java)
            existingAssignment?.let { a ->
                binding.etTitle.setText(a.title)
                binding.etDescription.setText(a.description)
                binding.etMarks.setText(a.totalMarks.toString())
                dueDate.time = a.dueDate.toDate()
            }
        }

        updateDueDateText()

        binding.btnPickDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, y, m, d ->
                dueDate.set(y, m, d)
                TimePickerDialog(requireContext(), { _, h, min ->
                    dueDate.set(Calendar.HOUR_OF_DAY, h)
                    dueDate.set(Calendar.MINUTE, min)
                    updateDueDateText()
                }, dueDate.get(Calendar.HOUR_OF_DAY), dueDate.get(Calendar.MINUTE), false).show()
            }, dueDate.get(Calendar.YEAR), dueDate.get(Calendar.MONTH), dueDate.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.btnAiSuggest.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val desc  = binding.etDescription.text.toString().trim()
            val ctx   = if (title.isNotBlank() || desc.isNotBlank())
                getString(R.string.ai_suggestion_context_format, title, desc) else getString(R.string.a_new_assignment)
            AISuggestionsSheet.newInstance("assignment", ctx).apply {
                onPick = { t, d -> binding.etTitle.setText(t); binding.etDescription.setText(d) }
            }.show(childFragmentManager, "ai_assign_suggest")
        }

        binding.btnCreate.text = getString(if (existingAssignment != null) R.string.update_assignment else R.string.create_assignment_btn)

        binding.btnCreate.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val desc  = binding.etDescription.text.toString().trim()
            val marks = binding.etMarks.text.toString().toIntOrNull() ?: 100

            if (title.isEmpty()) { binding.tilTitle.error = getString(R.string.required); return@setOnClickListener }

            val existing = existingAssignment
            if (existing != null) {
                viewModel.updateAssignment(classId, existing.copy(
                    title = title, description = desc,
                    dueDate = Timestamp(dueDate.time), totalMarks = marks))
            } else {
                viewModel.createAssignment(classId, Assignment(
                    title = title, description = desc,
                    dueDate = Timestamp(dueDate.time), totalMarks = marks, createdAt = Timestamp.now()))
            }
            dismiss()
        }
    }

    private fun updateDueDateText() {
        binding.btnPickDate.text = android.text.format.DateFormat.format(getString(R.string.date_format_assignment), dueDate).toString()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
