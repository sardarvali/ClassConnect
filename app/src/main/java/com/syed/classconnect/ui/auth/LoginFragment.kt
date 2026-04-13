package com.syed.classconnect.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.syed.classconnect.R
import com.syed.classconnect.databinding.FragmentLoginBinding
import com.syed.classconnect.ui.main.MainActivity
import com.syed.classconnect.util.NetworkResult
import com.syed.classconnect.util.ValidationUtils
<<<<<<< HEAD
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.show
=======
import com.syed.classconnect.util.addPressEffect
>>>>>>> final
import com.syed.classconnect.util.wiggle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

<<<<<<< HEAD
    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).getResult(ApiException::class.java)
            account.idToken?.let { viewModel.signInWithGoogle(it) }
        } catch (e: ApiException) {
            showError(getString(R.string.error_generic))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
=======
    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    .getResult(ApiException::class.java)
                account.idToken?.let { viewModel.signInWithGoogle(it) }
            } catch (e: ApiException) {
                showError(getString(R.string.error_generic))
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =   FragmentLoginBinding.inflate(inflater, container, false)
>>>>>>> final
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Logo pop-in with overshoot
        binding.ivLogo.alpha = 0f; binding.ivLogo.scaleX = 0f; binding.ivLogo.scaleY = 0f
        binding.ivLogo.animate().alpha(1f).scaleX(1f).scaleY(1f)
            .setDuration(500).setStartDelay(100)
            .setInterpolator(OvershootInterpolator(2f)).start()

        // Staggered entrance animation for the rest
<<<<<<< HEAD
        listOf(binding.tvTitle, binding.cardForm,
            binding.layoutDivider, binding.btnGoogle, binding.tvRegister)
=======
        listOf(
            binding.tvTitle, binding.cardForm,
            binding.layoutDivider, binding.btnGoogle, binding.tvRegister
        )
>>>>>>> final
            .forEachIndexed { i, v ->
                v.alpha = 0f; v.translationY = 40f
                v.animate().alpha(1f).translationY(0f)
                    .setDuration(450).setStartDelay(300 + i * 70L)
                    .setInterpolator(DecelerateInterpolator(2f)).start()
            }

<<<<<<< HEAD
=======
        binding.btnLogin.addPressEffect()
        binding.btnGoogle.addPressEffect()
        binding.tvForgotPassword.addPressEffect()
        binding.tvRegister.addPressEffect()

>>>>>>> final
        binding.btnLogin.setOnClickListener { attemptLogin() }
        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_forgot)
        }
        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }
        binding.btnGoogle.setOnClickListener { launchGoogleSignIn() }

        viewModel.authState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> showLoading(true)
                is NetworkResult.Success -> {
                    showLoading(false)
                    // Route is handled by loginRoute observer below
                }
<<<<<<< HEAD
=======

>>>>>>> final
                is NetworkResult.Error -> {
                    showLoading(false)
                    if (result.message.startsWith("new_google_user:")) {
                        showGoogleRoleDialog()
                    } else {
                        showError(result.message)
                    }
                }
            }
        }

        viewModel.loginRoute.observe(viewLifecycleOwner) { route ->
            when (route) {
                is LoginRouteResult.ToMain -> {
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    @Suppress("DEPRECATION")
<<<<<<< HEAD
                    requireActivity().overridePendingTransition(R.anim.fade_scale_in, R.anim.fade_scale_out)
                    requireActivity().finish()
                }
                is LoginRouteResult.ToPending -> {
                    findNavController().navigate(R.id.pendingApprovalFragment)
                }
=======
                    requireActivity().overridePendingTransition(
                        R.anim.fade_scale_in,
                        R.anim.fade_scale_out
                    )
                    requireActivity().finish()
                }

                is LoginRouteResult.ToPending -> {
                    findNavController().navigate(R.id.pendingApprovalFragment)
                }

>>>>>>> final
                is LoginRouteResult.ToEmailVerification -> {
                    findNavController().navigate(R.id.action_login_to_emailVerificationWait)
                }
            }
        }
    }

    private fun attemptLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        if (!ValidationUtils.isValidEmail(email)) {
            binding.tilEmail.error = getString(R.string.error_invalid_email)
            binding.tilEmail.wiggle()
            return
        }
        binding.tilEmail.error = null
        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_empty_field)
            binding.tilPassword.wiggle()
            return
        }
        binding.tilPassword.error = null
        viewModel.login(email, password)
    }

    private fun launchGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()
        val client = GoogleSignIn.getClient(requireActivity(), gso)
        googleSignInLauncher.launch(client.signInIntent)
    }

    private fun showGoogleRoleDialog() {
        // show dialog for role selection — simplified here
        showError("Please register first to set your role.")
    }

    private fun showLoading(loading: Boolean) {
<<<<<<< HEAD
        if (loading) { binding.progressBar.show(); binding.btnLogin.isEnabled = false }
        else { binding.progressBar.hide(); binding.btnLogin.isEnabled = true }
=======
        if (loading) {
            binding.progressBar.show(); binding.btnLogin.isEnabled = false
        } else {
            binding.progressBar.hide(); binding.btnLogin.isEnabled = true
        }
>>>>>>> final
    }

    private fun showError(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
    }

<<<<<<< HEAD
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
=======
    override fun onDestroyView() {
        super.onDestroyView(); _binding = null
    }
>>>>>>> final
}

