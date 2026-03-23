package com.syed.classconnect.ui.assignments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.databinding.FragmentAssignmentsBinding
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.NetworkResult
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.show
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AssignmentsFragment : Fragment() {

    private var _binding: FragmentAssignmentsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AssignmentsViewModel by viewModels()
    @Inject lateinit var auth: FirebaseAuth
    private lateinit var adapter: AssignmentsAdapter
    private lateinit var classId: String

    companion object {
        fun newInstance(classId: String) = AssignmentsFragment().apply {
            arguments = Bundle().apply { putString(Constants.EXTRA_CLASS_ID, classId) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAssignmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classId = arguments?.getString(Constants.EXTRA_CLASS_ID) ?: return
        val uid = auth.currentUser?.uid ?: return

        viewModel.loadUserRole(uid)
        viewModel.loadAssignments(classId)

        binding.rvAssignments.layoutManager = LinearLayoutManager(requireContext())

        viewModel.userRole.observe(viewLifecycleOwner) { role ->
            val isTeacherOrAdmin = role == Constants.ROLE_TEACHER || role == Constants.ROLE_ADMIN
            adapter = AssignmentsAdapter(
                isTeacherMode = isTeacherOrAdmin,
                onItemClick = { assignment ->
                    if (isTeacherOrAdmin) {
                        SubmissionListFragment.newInstance(classId, assignment.id)
                            .show(parentFragmentManager, "submission_list")
                    } else {
                        AssignmentDetailFragment.newInstance(classId, assignment.id)
                            .show(parentFragmentManager, "assignment_detail")
                    }
                },
                onEdit = if (isTeacherOrAdmin) { { assignment ->
                    CreateAssignmentFragment.newInstanceForEdit(classId, assignment)
                        .show(parentFragmentManager, "edit_assignment")
                } } else null,
                onDelete = if (isTeacherOrAdmin) { { assignment ->
                    showDeleteAssignmentDialog(assignment)
                } } else null
            )
            binding.rvAssignments.adapter = adapter
            val current = viewModel.assignments.value
            if (current is NetworkResult.Success) adapter.submitList(current.data)
        }

        // default adapter until role loads
        adapter = AssignmentsAdapter(
            isTeacherMode = false,
            onItemClick = { assignment ->
                AssignmentDetailFragment.newInstance(classId, assignment.id)
                    .show(parentFragmentManager, "assignment_detail")
            }
        )
        binding.rvAssignments.adapter = adapter

        viewModel.assignments.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> { binding.progressBar.show(); binding.layoutEmpty.hide() }
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    if (result.data.isEmpty()) { binding.layoutEmpty.show(); binding.rvAssignments.hide() }
                    else { binding.layoutEmpty.hide(); binding.rvAssignments.show(); adapter.submitList(result.data) }
                }
                is NetworkResult.Error -> binding.progressBar.hide()
            }
        }

        viewModel.userRole.observe(viewLifecycleOwner) { role ->
            if (role == Constants.ROLE_TEACHER || role == Constants.ROLE_ADMIN) {
                binding.fab.show()
                binding.fab.setOnClickListener {
                    CreateAssignmentFragment.newInstance(classId)
                        .show(parentFragmentManager, "create_assignment")
                }
            } else {
                binding.fab.hide()
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadAssignments(classId)
            binding.swipeRefresh.isRefreshing = false
        }

        viewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Success -> {
                    Snackbar.make(binding.root, "Assignment deleted", Snackbar.LENGTH_SHORT).show()
                }
                is NetworkResult.Error -> {
                    Snackbar.make(binding.root, "Delete failed: ${result.message}", Snackbar.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading -> { /* no-op */ }
            }
        }
    }


    private fun showDeleteAssignmentDialog(assignment: com.syed.classconnect.data.model.Assignment) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Assignment")
            .setMessage("Delete \"${assignment.title}\"? This will also delete all student submissions. This cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteAssignment(classId, assignment.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

