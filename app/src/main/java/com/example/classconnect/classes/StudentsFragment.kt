package com.syed.classconnect.ui.classes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.syed.classconnect.R
import com.syed.classconnect.databinding.FragmentStudentsBinding
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.NetworkResult
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentsFragment : Fragment() {

    private var _binding: FragmentStudentsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudentsViewModel by viewModels()
    private lateinit var adapter: StudentsAdapter

    companion object {
        fun newInstance(classId: String) = StudentsFragment().apply {
            arguments = Bundle().apply { putString(Constants.EXTRA_CLASS_ID, classId) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val classId = arguments?.getString(Constants.EXTRA_CLASS_ID) ?: return

        adapter = StudentsAdapter()
        binding.rvStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStudents.adapter = adapter

        viewModel.loadStudents(classId)

        viewModel.students.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    binding.progressBar.show()
                    binding.layoutEmpty.hide()
                }
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    if (result.data.isEmpty()) {
                        binding.layoutEmpty.show()
                        binding.rvStudents.hide()
                    } else {
                        binding.layoutEmpty.hide()
                        binding.rvStudents.show()
                        adapter.submitList(result.data)
                    }
                    binding.tvStudentCount.text = getString(R.string.student_count_label, result.data.size)
                }
                is NetworkResult.Error -> {
                    binding.progressBar.hide()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

