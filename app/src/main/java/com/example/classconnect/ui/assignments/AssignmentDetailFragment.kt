package com.syed.classconnect.ui.assignments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.data.model.Submission
import com.syed.classconnect.databinding.FragmentAssignmentDetailBinding
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.DateUtils.toDisplayDateTime
import com.syed.classconnect.util.NetworkResult
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.show
import com.syed.classconnect.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AssignmentDetailFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAssignmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AssignmentsViewModel by viewModels()
    @Inject lateinit var auth: FirebaseAuth
    private lateinit var classId: String
    private lateinit var assignmentId: String

    companion object {
        fun newInstance(classId: String, assignmentId: String) = AssignmentDetailFragment().apply {
            arguments = Bundle().apply {
                putString(Constants.EXTRA_CLASS_ID, classId)
                putString(Constants.EXTRA_ASSIGNMENT_ID, assignmentId)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAssignmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classId = arguments?.getString(Constants.EXTRA_CLASS_ID) ?: return
        assignmentId = arguments?.getString(Constants.EXTRA_ASSIGNMENT_ID) ?: return
        val uid = auth.currentUser?.uid ?: return

        viewModel.loadAssignmentDetail(classId, assignmentId)
        viewModel.loadUserRole(uid)
        viewModel.loadSubmission(classId, assignmentId, uid)

        viewModel.assignmentDetail.observe(viewLifecycleOwner) { assignment ->
            assignment ?: return@observe
            binding.tvTitle.text = assignment.title
            binding.tvDescription.text = assignment.description
            binding.tvDueDate.text = "Due: ${assignment.dueDate.toDisplayDateTime()}"
            binding.tvMarks.text = "Total Marks: ${assignment.totalMarks}"
        }

        viewModel.submission.observe(viewLifecycleOwner) { submission ->
            if (submission != null) {
                binding.layoutSubmit.hide()
                binding.layoutSubmitted.show()
                binding.tvSubmittedAt.text = "Submitted: ${submission.submittedAt.toDisplayDateTime()}"
                if (submission.grade >= 0) {
                    binding.tvGrade.text = "Grade: ${submission.grade} / ${viewModel.assignmentDetail.value?.totalMarks}"
                    binding.tvFeedback.text = submission.feedback
                    binding.layoutGrade.show()
                }
            } else {
                binding.layoutSubmit.show()
                binding.layoutSubmitted.hide()
            }
        }

        binding.btnSubmit.setOnClickListener {
            val text = binding.etAnswer.text.toString().trim()
            val submission = Submission(
                studentId = uid, studentName = "",
                submittedAt = Timestamp.now(), textAnswer = text, status = "submitted"
            )
            viewModel.submitAssignment(classId, assignmentId, submission)
        }

        viewModel.submitResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Success -> { showSnackbar("Submitted successfully!"); dismiss() }
                is NetworkResult.Error -> showSnackbar(result.message)
                else -> {}
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

