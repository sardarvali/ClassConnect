package com.syed.classconnect.ui.assignments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.syed.classconnect.databinding.FragmentGradeSubmissionBinding
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.NetworkResult
import com.syed.classconnect.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GradeSubmissionFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentGradeSubmissionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AssignmentsViewModel by viewModels()
    private lateinit var classId: String
    private lateinit var assignmentId: String
    private lateinit var studentId: String

    companion object {
        private const val ARG_STUDENT_ID = "student_id"
        fun newInstance(classId: String, assignmentId: String, studentId: String) =
            GradeSubmissionFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.EXTRA_CLASS_ID, classId)
                    putString(Constants.EXTRA_ASSIGNMENT_ID, assignmentId)
                    putString(ARG_STUDENT_ID, studentId)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGradeSubmissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classId = arguments?.getString(Constants.EXTRA_CLASS_ID) ?: return
        assignmentId = arguments?.getString(Constants.EXTRA_ASSIGNMENT_ID) ?: return
        studentId = arguments?.getString(ARG_STUDENT_ID) ?: return

        viewModel.loadSubmission(classId, assignmentId, studentId)
        viewModel.loadAssignmentDetail(classId, assignmentId)

        viewModel.submission.observe(viewLifecycleOwner) { submission ->
            submission ?: return@observe
            binding.tvStudentId.text =
                "Student: ${submission.studentName.ifEmpty { submission.studentId }}"
            binding.tvAnswer.text = submission.textAnswer.ifEmpty { "No text answer submitted" }
            if (submission.grade >= 0) {
                binding.etGrade.setText(submission.grade.toString())
                binding.etFeedback.setText(submission.feedback)
            }
        }

        viewModel.assignmentDetail.observe(viewLifecycleOwner) { assignment ->
            assignment ?: return@observe
            binding.tvMaxMarks.text = "/ ${assignment.totalMarks}"
        }

        binding.btnSaveGrade.setOnClickListener {
            val gradeText = binding.etGrade.text.toString().trim()
            if (gradeText.isEmpty()) {
                binding.tilGrade.error = "Grade is required"
                return@setOnClickListener
            }
            val grade = gradeText.toIntOrNull()
            if (grade == null) {
                binding.tilGrade.error = "Enter a valid number"
                return@setOnClickListener
            }
            val maxMarks = viewModel.assignmentDetail.value?.totalMarks ?: Int.MAX_VALUE
            if (grade < 0 || grade > maxMarks) {
                binding.tilGrade.error = "Grade must be between 0 and $maxMarks"
                return@setOnClickListener
            }
            binding.tilGrade.error = null
            val feedback = binding.etFeedback.text.toString().trim()
            viewModel.gradeSubmission(classId, assignmentId, studentId, grade, feedback)
        }

        viewModel.gradeResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Success -> {
                    showSnackbar("Grade saved successfully!")
                    dismiss()
                }

                is NetworkResult.Error -> showSnackbar(result.message)
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView(); _binding = null
    }
}

