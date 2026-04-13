package com.syed.classconnect.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
<<<<<<< HEAD
import com.syed.classconnect.R
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.databinding.FragmentProfileBinding
import com.syed.classconnect.ui.auth.AuthActivity
import com.syed.classconnect.util.loadAvatar
import com.syed.classconnect.util.animateEntrance
import com.syed.classconnect.util.animateScaleIn
=======
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.R
import com.syed.classconnect.databinding.FragmentProfileBinding
import com.syed.classconnect.ui.auth.AuthActivity
import com.syed.classconnect.util.animateEntrance
import com.syed.classconnect.util.animateScaleIn
import com.syed.classconnect.util.loadAvatar
>>>>>>> final
import com.syed.classconnect.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
<<<<<<< HEAD
    @Inject lateinit var auth: FirebaseAuth

    private val photoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@registerForActivityResult
        val uid = auth.currentUser?.uid ?: return@registerForActivityResult
        viewModel.uploadProfilePhoto(uid, uri)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
=======
    @Inject
    lateinit var auth: FirebaseAuth

    private val photoLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri ?: return@registerForActivityResult
            val uid = auth.currentUser?.uid ?: return@registerForActivityResult
            viewModel.uploadProfilePhoto(uid, uri)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
>>>>>>> final
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uid = auth.currentUser?.uid ?: return

        // Entrance animations
        binding.ivAvatar.animateScaleIn(startDelay = 100L)
<<<<<<< HEAD
        listOf<View>(binding.tvName, binding.tvEmail, binding.tvRole).animateEntrance(startDelay = 200L, stepDelay = 60L)
=======
        listOf<View>(
            binding.tvName,
            binding.tvEmail,
            binding.tvRole
        ).animateEntrance(startDelay = 200L, stepDelay = 60L)
>>>>>>> final

        viewModel.loadProfile(uid)

        viewModel.user.observe(viewLifecycleOwner) { user ->
            user ?: return@observe
            binding.tvName.text = user.name
            binding.tvEmail.text = user.email
            binding.tvRole.text = user.role.replaceFirstChar { it.uppercase() }
            binding.tvBio.text = user.bio.ifEmpty { "No bio yet" }
            binding.ivAvatar.loadAvatar(user.photoUrl)
            binding.etName.setText(user.name)
            binding.etBio.setText(user.bio)
            // Account type badge
            if (user.accountType == "independent") {
                binding.chipAccountType.text = getString(R.string.badge_independent)
                binding.chipAccountType.setChipBackgroundColorResource(R.color.success)
            } else {
<<<<<<< HEAD
                binding.chipAccountType.text = getString(R.string.badge_institution, user.institutionId.ifEmpty { "Institution" })
=======
                binding.chipAccountType.text = getString(
                    R.string.badge_institution,
                    user.institutionId.ifEmpty { "Institution" })
>>>>>>> final
                binding.chipAccountType.setChipBackgroundColorResource(R.color.info)
            }
        }

        binding.ivAvatar.setOnClickListener { photoLauncher.launch("image/*") }

        binding.btnSave.setOnClickListener {
            val uid2 = auth.currentUser?.uid ?: return@setOnClickListener
            val name = binding.etName.text.toString().trim()
            val bio = binding.etBio.text.toString().trim()
            viewModel.updateProfile(uid2, name, bio)
            showSnackbar("Profile saved!")
        }

        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
<<<<<<< HEAD
                .setMessage(getString(com.syed.classconnect.R.string.confirm_logout))
                .setPositiveButton(getString(com.syed.classconnect.R.string.yes)) { _, _ ->
=======
                .setMessage(getString(R.string.confirm_logout))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
>>>>>>> final
                    auth.signOut()
                    startActivity(Intent(requireContext(), AuthActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }
<<<<<<< HEAD
                .setNegativeButton(getString(com.syed.classconnect.R.string.cancel), null)
=======
                .setNegativeButton(getString(R.string.cancel), null)
>>>>>>> final
                .show()
        }

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_settings)
        }

        viewModel.photoUrl.observe(viewLifecycleOwner) { url ->
            binding.ivAvatar.loadAvatar(url)
        }
    }

<<<<<<< HEAD
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
=======
    override fun onDestroyView() {
        super.onDestroyView(); _binding = null
    }
>>>>>>> final
}

