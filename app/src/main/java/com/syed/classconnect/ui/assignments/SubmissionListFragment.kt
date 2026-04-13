package com.syed.classconnect.ui.assignments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
<<<<<<< HEAD
import com.google.android.material.chip.Chip
=======
>>>>>>> final
import com.syed.classconnect.data.model.Submission
import com.syed.classconnect.databinding.FragmentSubmissionListBinding
import com.syed.classconnect.databinding.ItemSubmissionBinding
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.DateUtils.toDisplayDateTime
import com.syed.classconnect.util.NetworkResult
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubmissionListFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentSubmissionListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AssignmentsViewModel by viewModels()
    private lateinit var adapter: SubmissionsAdapter
    private lateinit var classId: String
    private lateinit var assignmentId: String
    private var allSubmissions: List<Submission> = emptyList()

    companion object {
        fun newInstance(classId: String, assignmentId: String) = SubmissionListFragment().apply {
            arguments = Bundle().apply {
                putString(Constants.EXTRA_CLASS_ID, classId)
                putString(Constants.EXTRA_ASSIGNMENT_ID, assignmentId)
            }
        }
    }

<<<<<<< HEAD
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
=======
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
>>>>>>> final
        _binding = FragmentSubmissionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classId = arguments?.getString(Constants.EXTRA_CLASS_ID) ?: return
        assignmentId = arguments?.getString(Constants.EXTRA_ASSIGNMENT_ID) ?: return

        adapter = SubmissionsAdapter { submission ->
            GradeSubmissionFragment.newInstance(classId, assignmentId, submission.studentId)
                .show(parentFragmentManager, "grade_submission")
        }
        binding.rvSubmissions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSubmissions.adapter = adapter

        viewModel.loadAllSubmissions(classId, assignmentId)

        viewModel.allSubmissions.observe(viewLifecycleOwner) { result ->
            when (result) {
<<<<<<< HEAD
                is NetworkResult.Loading -> { binding.progressBar.show(); binding.layoutEmpty.hide() }
=======
                is NetworkResult.Loading -> {
                    binding.progressBar.show(); binding.layoutEmpty.hide()
                }

>>>>>>> final
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    allSubmissions = result.data
                    if (result.data.isEmpty()) {
                        binding.layoutEmpty.show(); binding.rvSubmissions.hide()
                    } else {
                        binding.layoutEmpty.hide(); binding.rvSubmissions.show()
                        adapter.submitList(result.data)
                    }
                }
<<<<<<< HEAD
=======

>>>>>>> final
                is NetworkResult.Error -> binding.progressBar.hide()
            }
        }

        // Filter chips
        setupFilterChips()
    }

    private fun setupFilterChips() {
        val chips = listOf(
            binding.chipAll to null,
            binding.chipSubmitted to "submitted",
            binding.chipGraded to "graded",
            binding.chipNotSubmitted to "not_submitted"
        )
        chips.forEach { (chip, filter) ->
            chip.setOnClickListener {
                chips.forEach { (c, _) -> c.isChecked = false }
                chip.isChecked = true
                when (filter) {
                    null -> adapter.submitList(allSubmissions)
                    "not_submitted" -> adapter.submitList(allSubmissions.filter { it.status != "submitted" && it.status != "graded" })
                    else -> adapter.submitList(allSubmissions.filter { it.status == filter })
                }
            }
        }
        binding.chipAll.isChecked = true
    }

<<<<<<< HEAD
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
=======
    override fun onDestroyView() {
        super.onDestroyView(); _binding = null
    }
>>>>>>> final
}

class SubmissionsAdapter(private val onGrade: (Submission) -> Unit) :
    ListAdapter<Submission, SubmissionsAdapter.VH>(object : DiffUtil.ItemCallback<Submission>() {
        override fun areItemsTheSame(a: Submission, b: Submission) = a.studentId == b.studentId
        override fun areContentsTheSame(a: Submission, b: Submission) = a == b
    }) {

    inner class VH(private val b: ItemSubmissionBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(sub: Submission) {
            b.tvStudentName.text = sub.studentName.ifEmpty { sub.studentId }
            b.tvSubmittedAt.text = sub.submittedAt.toDisplayDateTime()
            b.chipStatus.text = sub.status.replaceFirstChar { it.uppercase() }
<<<<<<< HEAD
            b.chipStatus.visibility = android.view.View.VISIBLE
            b.chipStatus.setChipBackgroundColorResource(
                when (sub.status) {
                    "graded"    -> com.syed.classconnect.R.color.semantic_success
                    "submitted" -> com.syed.classconnect.R.color.semantic_info
                    else        -> com.syed.classconnect.R.color.semantic_warning
=======
            b.chipStatus.visibility = View.VISIBLE
            b.chipStatus.setChipBackgroundColorResource(
                when (sub.status) {
                    "graded" -> com.syed.classconnect.R.color.semantic_success
                    "submitted" -> com.syed.classconnect.R.color.semantic_info
                    else -> com.syed.classconnect.R.color.semantic_warning
>>>>>>> final
                }
            )
            if (sub.grade >= 0) {
                b.tvGrade.text = "Grade: ${sub.grade}"
                b.tvGrade.show()
            } else {
                b.tvGrade.hide()
            }
            b.btnGrade.setOnClickListener { onGrade(sub) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemSubmissionBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))
}

