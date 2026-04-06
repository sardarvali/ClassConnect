package com.syed.classconnect.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.syed.classconnect.R
import com.syed.classconnect.data.model.User
import com.syed.classconnect.databinding.BottomSheetAssignTeacherBinding
import com.syed.classconnect.util.NetworkResult
import com.syed.classconnect.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AssignTeacherBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAssignTeacherBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminClassesViewModel by viewModels({ requireParentFragment() })
    private lateinit var adapter: AssignTeacherAdapter

    private var classId: String = ""
    private var className: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAssignTeacherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        classId = arguments?.getString("classId") ?: ""
        className = arguments?.getString("className") ?: ""
        binding.tvHeading.text = getString(R.string.assign_teacher) + " — $className"

        adapter = AssignTeacherAdapter { teacher -> confirmAndAssign(teacher) }
        binding.rvTeachers.adapter = adapter

        // Load teachers
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.teachers.collectLatest { result ->
                when (result) {
                    is NetworkResult.Loading -> binding.progressBar.show()
                    is NetworkResult.Success -> {
                        binding.progressBar.hide()
                        adapter.submitList(result.data)
                    }

                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        showSnackbar(result.message)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.assignState.collectLatest { result ->
                when (result) {
                    is NetworkResult.Loading -> binding.progressBar.show()
                    is NetworkResult.Success -> {
                        binding.progressBar.hide()
                        showSnackbar(getString(R.string.assign_teacher) + " successful")
                        dismiss()
                    }

                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        showSnackbar(result.message)
                    }

                    null -> Unit
                }
            }
        }

        viewModel.loadTeachersForInstitution()
    }

    private fun confirmAndAssign(teacher: User) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.assign_teacher)
            .setMessage("Assign ${teacher.name} as the teacher for $className?")
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.assignTeacher(classId, className, teacher)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView(); _binding = null
    }

    companion object {
        fun newInstance(classId: String, className: String) = AssignTeacherBottomSheet().apply {
            arguments = Bundle().apply {
                putString("classId", classId)
                putString("className", className)
            }
        }
    }
}

