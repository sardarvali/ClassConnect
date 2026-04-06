package com.syed.classconnect.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.R
import com.syed.classconnect.databinding.FragmentAdminDashboardBinding
import com.syed.classconnect.util.NetworkResult
import com.syed.classconnect.util.animateEntrance
import com.syed.classconnect.util.animateStatValue
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminViewModel by viewModels()
    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uid = auth.currentUser?.uid ?: return

        // Staggered entrance for stat cards
        listOf<View>(
            binding.tvTotalUsers,
            binding.tvTeachers,
            binding.tvStudents,
            binding.tvClasses
        )
            .map { it.parent.parent as View } // get the MaterialCardView grandparent
            .animateEntrance(startDelay = 100L, stepDelay = 100L)

        viewModel.loadAdminStats(uid)

        viewModel.stats.observe(viewLifecycleOwner) { stats ->
            animateStatValue(binding.tvTotalUsers, stats.totalUsers)
            animateStatValue(binding.tvTeachers, stats.teachers)
            animateStatValue(binding.tvStudents, stats.students)
            animateStatValue(binding.tvClasses, stats.classes)
        }

        viewModel.pendingUsers.observe(viewLifecycleOwner) { result ->
            if (result is NetworkResult.Success) {
                binding.tvPendingCount.text = "${result.data.size} pending"
            }
        }

        binding.btnRoleChangeHistory.setOnClickListener {
            findNavController().navigate(R.id.action_adminDashboard_to_roleChangeHistory)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView(); _binding = null
    }
}

