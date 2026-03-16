package com.syed.classconnect.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.syed.classconnect.R
import com.syed.classconnect.databinding.FragmentAdminClassesBinding
import com.syed.classconnect.util.NetworkResult
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.show
import com.syed.classconnect.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminClassesFragment : Fragment() {

    private var _binding: FragmentAdminClassesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminClassesViewModel by viewModels()
    private lateinit var adapter: AdminClassAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminClassesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AdminClassAdapter(
            onAssignTeacher = { classRoom ->
                AssignTeacherBottomSheet.newInstance(classRoom.id, classRoom.name)
                    .show(childFragmentManager, "assign_teacher")
            }
        )
        binding.rvClasses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvClasses.adapter = adapter

        // Filter chips
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            viewModel.setFilter(checkedIds.contains(R.id.chip_unassigned))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.classes.collectLatest { result ->
                when (result) {
                    is NetworkResult.Loading -> { binding.progressBar.show(); binding.tvEmpty.isVisible = false }
                    is NetworkResult.Success -> {
                        binding.progressBar.hide()
                        adapter.submitList(result.data)
                        binding.tvEmpty.isVisible = result.data.isEmpty()
                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        showSnackbar(result.message)
                    }
                }
            }
        }

        viewModel.loadData()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
