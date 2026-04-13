package com.syed.classconnect.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.syed.classconnect.R
import com.syed.classconnect.databinding.FragmentEmailVerificationWaitBinding
import com.syed.classconnect.ui.main.MainActivity
import com.syed.classconnect.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EmailVerificationWaitFragment : Fragment() {

    private var _binding: FragmentEmailVerificationWaitBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmailVerificationViewModel by viewModels()
    private var resendTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailVerificationWaitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = Firebase.auth.currentUser?.email ?: ""
        binding.tvEmailAddress.text = email
        binding.tvSubtext.text = getString(R.string.email_verify_sub, email)

        startResendCountdown()

        // Poll with repeatOnLifecycle — pauses when app goes to background
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.startPolling()
                viewModel.state.collect { state ->
                    when (state) {
                        VerificationState.Polling ->
                            binding.tvStatus.text = getString(R.string.verification_checking)
<<<<<<< HEAD
=======

>>>>>>> final
                        VerificationState.Verified -> {
                            binding.tvStatus.text = getString(R.string.verification_success)
                            // Navigate to MainActivity
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                            requireActivity().finish()
                        }
<<<<<<< HEAD
=======

>>>>>>> final
                        is VerificationState.Error -> showSnackbar(state.message)
                    }
                }
            }
        }

        binding.btnResendEmail.setOnClickListener {
            viewModel.resendEmail()
            showSnackbar(getString(R.string.resend_email))
            startResendCountdown()
        }

        binding.btnOpenEmailApp.setOnClickListener {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_EMAIL)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
<<<<<<< HEAD
            try { startActivity(Intent.createChooser(intent, getString(R.string.open_email_app))) }
            catch (_: Exception) { showSnackbar(getString(R.string.error_generic)) }
=======
            try {
                startActivity(Intent.createChooser(intent, getString(R.string.open_email_app)))
            } catch (_: Exception) {
                showSnackbar(getString(R.string.error_generic))
            }
>>>>>>> final
        }

        binding.tvWrongEmail.setOnClickListener {
            viewModel.signOutAndClear()
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun startResendCountdown() {
        binding.btnResendEmail.isEnabled = false
        resendTimer?.cancel()
        resendTimer = object : CountDownTimer(60_000L, 1_000L) {
            override fun onTick(ms: Long) {
<<<<<<< HEAD
                _binding?.btnResendEmail?.text = getString(R.string.resend_cooldown, (ms / 1000).toInt())
            }
=======
                _binding?.btnResendEmail?.text =
                    getString(R.string.resend_cooldown, (ms / 1000).toInt())
            }

>>>>>>> final
            override fun onFinish() {
                _binding?.btnResendEmail?.isEnabled = true
                _binding?.btnResendEmail?.text = getString(R.string.resend_email)
            }
        }.start()
    }

    override fun onDestroyView() {
        resendTimer?.cancel()
        viewModel.stopPolling()
        _binding = null
        super.onDestroyView()
    }
}
