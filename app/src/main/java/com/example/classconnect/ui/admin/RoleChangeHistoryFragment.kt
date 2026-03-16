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
import com.syed.classconnect.databinding.FragmentRoleChangeHistoryBinding
import com.syed.classconnect.util.NetworkResult
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RoleChangeHistoryFragment : Fragment() {

    private var _binding: FragmentRoleChangeHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RoleChangeHistoryViewModel by viewModels()
    private lateinit var adapter: RoleChangeLogAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoleChangeHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = RoleChangeLogAdapter()
        binding.rvLogs.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLogs.adapter = adapter

        binding.toolbar.setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.logs.collectLatest { result ->
                when (result) {
                    is NetworkResult.Loading -> { binding.progressBar.show(); binding.tvEmpty.isVisible = false }
                    is NetworkResult.Success -> {
                        binding.progressBar.hide()
                        adapter.submitList(result.data)
                        binding.tvEmpty.isVisible = result.data.isEmpty()
                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        binding.tvEmpty.isVisible = true
                        binding.tvEmpty.text = result.message
                    }
                }
            }
        }

        viewModel.loadLogs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

