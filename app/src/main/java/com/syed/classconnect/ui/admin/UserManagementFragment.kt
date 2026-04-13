package com.syed.classconnect.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
<<<<<<< HEAD
=======
import androidx.core.widget.doAfterTextChanged
>>>>>>> final
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.R
<<<<<<< HEAD
=======
import com.syed.classconnect.data.model.User
>>>>>>> final
import com.syed.classconnect.databinding.FragmentUserManagementBinding
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserManagementFragment : Fragment() {

    private var _binding: FragmentUserManagementBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminViewModel by viewModels()
<<<<<<< HEAD
    @Inject lateinit var auth: FirebaseAuth
    private lateinit var adapter: UserManagementAdapter
    private var currentTab = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
=======

    @Inject
    lateinit var auth: FirebaseAuth

    private lateinit var adapter: UserManagementAdapter
    private var currentTab = 0
    private var currentQuery = ""
    private var pendingUsersSource: List<User> = emptyList()
    private var usersSource: List<User> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
>>>>>>> final
        _binding = FragmentUserManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uid = auth.currentUser?.uid ?: return

        adapter = UserManagementAdapter(
            onApprove = { user ->
                removeFromPendingList(user.uid)
                viewModel.approveUser(user.uid, true)
            },
<<<<<<< HEAD
            onReject  = { user ->
=======
            onReject = { user ->
>>>>>>> final
                removeFromPendingList(user.uid)
                viewModel.approveUser(user.uid, false)
            },
            onItemClick = { user ->
                findNavController().navigate(
                    R.id.action_userManagement_to_userDetail,
                    bundleOf("userId" to user.uid)
                )
            }
        )
        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsers.adapter = adapter

<<<<<<< HEAD
=======
        binding.etSearch.doAfterTextChanged { text ->
            currentQuery = text?.toString().orEmpty()
            applyFilterForCurrentTab()
        }

>>>>>>> final
        viewModel.loadAdminStats(uid)

        // ── Pending tab observer ──────────────────────────────────────────
        viewModel.pendingUsers.observe(viewLifecycleOwner) { result ->
            if (currentTab != 0) return@observe
            adapter.showApproveActions = true
            when (result) {
                is NetworkResult.Success -> {
<<<<<<< HEAD
                    adapter.submitList(result.data.toMutableList())
                    binding.progressBar.visibility = View.GONE
                    binding.emptyState.visibility =
                        if (result.data.isEmpty()) View.VISIBLE else View.GONE
                }
=======
                    pendingUsersSource = result.data
                    binding.progressBar.visibility = View.GONE
                    applyFilterForCurrentTab()
                }

>>>>>>> final
                is NetworkResult.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.emptyState.visibility = View.GONE
                }
<<<<<<< HEAD
=======

>>>>>>> final
                is NetworkResult.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showSnackbarAboveNav("Error: ${result.message}")
                }
            }
        }

        // ── Teachers / Students / Admins tab observer ─────────────────────────────
        viewModel.users.observe(viewLifecycleOwner) { result ->
            if (currentTab == 0) return@observe
            adapter.showApproveActions = false
            when (result) {
                is NetworkResult.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.emptyState.visibility = View.GONE
                }
<<<<<<< HEAD
                is NetworkResult.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(result.data.toMutableList())
                    binding.emptyState.visibility =
                        if (result.data.isEmpty()) View.VISIBLE else View.GONE
                }
=======

                is NetworkResult.Success -> {
                    binding.progressBar.visibility = View.GONE
                    usersSource = result.data
                    applyFilterForCurrentTab()
                }

>>>>>>> final
                is NetworkResult.Error -> binding.progressBar.visibility = View.GONE
            }
        }

        // ── Approve / reject result ───────────────────────────────────────
        viewModel.approveResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ApproveResult.Success -> {
                    val msg = if (result.approved) "✅ User approved" else "❌ User rejected"
                    showSnackbarAboveNav(msg)
<<<<<<< HEAD
                    // _pendingUsers is already updated inside ViewModel.approveUser()
                    // The pendingUsers observer will re-render the list automatically.
                }
                is ApproveResult.Error -> {
                    showSnackbarAboveNav("❗ Failed: ${result.message}")
                    // Force reload to restore the row
=======
                }

                is ApproveResult.Error -> {
                    showSnackbarAboveNav("❗ Failed: ${result.message}")
>>>>>>> final
                    viewModel.forceReload(auth.currentUser?.uid ?: return@observe)
                }
            }
        }

        // ── Tab switching ─────────────────────────────────────────────────
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                currentTab = tab.position
                onTabChanged(tab.position)
            }
<<<<<<< HEAD
            override fun onTabUnselected(tab: TabLayout.Tab?) { /* no-op */ }
            override fun onTabReselected(tab: TabLayout.Tab?) { /* no-op */ }
=======

            override fun onTabUnselected(tab: TabLayout.Tab?) { /* no-op */
            }

            override fun onTabReselected(tab: TabLayout.Tab?) { /* no-op */
            }
>>>>>>> final
        })
    }

    /** Remove user row immediately before Firestore confirms, for instant UI feedback. */
    private fun removeFromPendingList(uid: String) {
<<<<<<< HEAD
        val updated = adapter.currentList.filter { it.uid != uid }
        adapter.submitList(updated.toMutableList())
        binding.emptyState.visibility = if (updated.isEmpty()) View.VISIBLE else View.GONE
=======
        pendingUsersSource = pendingUsersSource.filter { it.uid != uid }
        if (currentTab == 0) {
            applyFilterForCurrentTab()
        }
>>>>>>> final
    }

    private fun onTabChanged(position: Int) {
        val instId = viewModel.institutionId
        adapter.showApproveActions = (position == 0)
        when (position) {
<<<<<<< HEAD
            0 -> {
                val result = viewModel.pendingUsers.value
                if (result is NetworkResult.Success) {
                    adapter.submitList(result.data.toMutableList())
                    binding.emptyState.visibility =
                        if (result.data.isEmpty()) View.VISIBLE else View.GONE
                }
            }
=======
            0 -> applyFilterForCurrentTab()
>>>>>>> final
            1 -> if (instId.isNotEmpty()) viewModel.loadUsers(instId, Constants.ROLE_ADMIN)
            2 -> if (instId.isNotEmpty()) viewModel.loadUsers(instId, Constants.ROLE_TEACHER)
            3 -> if (instId.isNotEmpty()) viewModel.loadUsers(instId, Constants.ROLE_STUDENT)
        }
    }

<<<<<<< HEAD
=======
    private fun applyFilterForCurrentTab() {
        val source = if (currentTab == 0) pendingUsersSource else usersSource
        val query = currentQuery.trim()
        val filtered = if (query.isBlank()) {
            source
        } else {
            source.filter { user ->
                user.name.contains(query, ignoreCase = true) ||
                    user.email.contains(query, ignoreCase = true)
            }
        }
        adapter.submitList(filtered.toMutableList())
        binding.emptyState.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

>>>>>>> final
    private fun showSnackbarAboveNav(message: String) {
        val root = requireActivity().window.decorView.rootView
        val snackbar = Snackbar.make(root, message, Snackbar.LENGTH_SHORT)
        requireActivity().findViewById<View>(R.id.bottom_nav_container)?.let {
            snackbar.anchorView = it
        }
        snackbar.show()
    }

<<<<<<< HEAD
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
=======
    override fun onDestroyView() {
        super.onDestroyView(); _binding = null
    }
>>>>>>> final
}
