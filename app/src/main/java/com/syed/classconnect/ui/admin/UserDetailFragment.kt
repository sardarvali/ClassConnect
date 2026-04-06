package com.syed.classconnect.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.R
import com.syed.classconnect.data.model.User
import com.syed.classconnect.databinding.FragmentUserDetailBinding
import com.syed.classconnect.util.NetworkResult
import com.syed.classconnect.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserDetailFragment : Fragment() {

    private var _binding: FragmentUserDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserDetailViewModel by viewModels()
    @Inject
    lateinit var auth: FirebaseAuth

    private var targetUser: User? = null
    private var adminUser: User? = null
    private var selectedNewRole: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val targetUid = arguments?.getString("userId") ?: return
        val adminUid = auth.currentUser?.uid ?: return

        viewModel.loadUser(targetUid)


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.user.collectLatest { result ->
                when (result) {
                    is NetworkResult.Loading -> binding.progressBar.show()
                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        showSnackbar(result.message)
                    }

                    is NetworkResult.Success -> {
                        binding.progressBar.hide()
                        targetUser = result.data
                        bindUserData(result.data, adminUid)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.roleChangeState.collectLatest { result ->
                result ?: return@collectLatest
                when (result) {
                    is NetworkResult.Loading -> binding.progressBar.show()
                    is NetworkResult.Success -> {
                        binding.progressBar.hide()
                        showSnackbar(getString(R.string.role_changed_success, result.data))
                        // Real-time listener will automatically update the UI
                    }

                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        showSnackbar(result.message)
                    }
                }
            }
        }

        setupRoleChips()
    }

    private fun bindUserData(user: User, adminUid: String) {
        binding.tvName.text = user.name
        binding.tvEmail.text = user.email
        binding.tvCurrentRole.text =
            getString(R.string.current_role, user.role.replaceFirstChar { it.uppercase() })

        Glide.with(this)
            .load(user.photoUrl.ifEmpty { null })
            .placeholder(R.drawable.ic_profile)
            .circleCrop()
            .into(binding.ivAvatar)

        // Institution / independent badge
        if (user.accountType == "independent") {
            binding.chipAccountType.text = getString(R.string.badge_independent)
            binding.chipAccountType.setChipBackgroundColorResource(R.color.success)
        } else {
            binding.chipAccountType.text = getString(R.string.badge_institution, user.institutionId)
            binding.chipAccountType.setChipBackgroundColorResource(R.color.info)
        }

        // Admin chip visibility
        binding.chipAdmin.isVisible = user.accountType == "institution"
                && user.uid != adminUid

        // Clear listener temporarily to avoid triggering change events
        binding.roleChipGroup.setOnCheckedStateChangeListener(null)

        // Pre-select current role chip
        binding.roleChipGroup.clearCheck()
        when (user.role) {
            "student" -> binding.chipStudent.isChecked = true
            "teacher" -> binding.chipTeacher.isChecked = true
            "admin" -> binding.chipAdmin.isChecked = true
        }

        // Re-attach the listener
        setupRoleChips()

        selectedNewRole = null
        binding.btnApplyRoleChange.isEnabled = false
        binding.tvRoleChangeWarning.isVisible = false
        binding.tilReason.isVisible = false
    }

    private fun setupRoleChips() {
        binding.roleChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val user = targetUser ?: return@setOnCheckedStateChangeListener
            val newRole = when {
                checkedIds.contains(R.id.chip_student) -> "student"
                checkedIds.contains(R.id.chip_teacher) -> "teacher"
                checkedIds.contains(R.id.chip_admin) -> "admin"
                else -> null
            }
            selectedNewRole = newRole
            val changed = newRole != null && newRole != user.role
            binding.btnApplyRoleChange.isEnabled = changed
            binding.tilReason.isVisible = changed
            if (changed && newRole != null) {
                val warning = buildWarning(user.role, newRole, user.name)
                if (warning.isNotEmpty()) {
                    binding.tvRoleChangeWarning.text = warning
                    binding.tvRoleChangeWarning.isVisible = true
                } else {
                    binding.tvRoleChangeWarning.isVisible = false
                }
            } else {
                binding.tvRoleChangeWarning.isVisible = false
            }
        }

        binding.btnApplyRoleChange.setOnClickListener {
            val user = targetUser ?: return@setOnClickListener
            val newRole = selectedNewRole ?: return@setOnClickListener
            showConfirmDialog(user, newRole)
        }
    }

    private fun showConfirmDialog(user: User, newRole: String) {
        val message = buildConfirmMessage(user.role, newRole, user.name)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.role_change_confirm_title))
            .setMessage(message)
            .setPositiveButton(getString(R.string.apply_role_change)) { _, _ ->
                val reason = binding.etReason.text?.toString()?.trim() ?: ""
                viewModel.changeUserRole(
                    targetUserId = user.uid,
                    newRole = newRole,
                    currentRole = user.role,
                    targetUserName = user.name,
                    institutionId = user.institutionId,
                    reason = reason
                )
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun buildWarning(from: String, to: String, name: String): String = when {
        from == "teacher" && to == "student" ->
            "Demoting $name will unassign them from all their classes."

        from == "student" && to == "teacher" ->
            "Promoting $name will remove them from all enrolled classes."

        to == "admin" ->
            "Granting admin to $name gives full institution control."

        else -> ""
    }

    private fun buildConfirmMessage(from: String, to: String, name: String): String = when {
        from == "teacher" && to == "student" ->
            "Changing $name from Teacher to Student will remove them as teacher from all their classes. Those classes will become unassigned. Are you sure?"

        from == "student" && to == "teacher" ->
            "Changing $name from Student to Teacher will remove them from all enrolled classes as a student. Are you sure?"

        to == "admin" ->
            "You are granting $name full Admin privileges for this institution. This gives them complete control. Are you sure?"

        else -> "Change $name's role from $from to $to?"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

