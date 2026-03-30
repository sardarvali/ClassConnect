package com.syed.classconnect.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.syed.classconnect.R
import com.syed.classconnect.databinding.FragmentRegisterBinding
import com.syed.classconnect.util.ValidationUtils
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.show
import com.syed.classconnect.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Password strength watcher
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = s?.toString() ?: ""
                if (password.isEmpty()) {
                    binding.passwordStrength.progress = 0
                    binding.strengthLabel.isVisible = false
                    binding.passwordRequirementsContainer.isVisible = false
                    return
                }

                binding.passwordRequirementsContainer.isVisible = true
                binding.strengthLabel.isVisible = true

                val strength = ValidationUtils.passwordStrength(password)
                binding.passwordStrength.progress = strength * 25

                val hasLength = password.length >= 8
                val hasUppercase = password.any { it.isUpperCase() }
                val hasNumber = password.any { it.isDigit() }
                val hasSpecial = password.any { !it.isLetterOrDigit() }

                updateReq(binding.reqLength, hasLength, getString(R.string.req_min_length))
                updateReq(binding.reqUppercase, hasUppercase, getString(R.string.req_uppercase))
                updateReq(binding.reqNumber, hasNumber, getString(R.string.req_number))
                updateReq(binding.reqSpecial, hasSpecial, getString(R.string.req_special))

                val (color, label) = when (strength) {
                    0, 1 -> Pair(R.color.semantic_error, getString(R.string.password_strength_weak))
                    2 -> Pair(R.color.semantic_warning, getString(R.string.password_strength_fair))
                    3 -> Pair(R.color.semantic_info, getString(R.string.password_strength_good))
                    else -> Pair(R.color.semantic_success, getString(R.string.password_strength_strong))
                }

                binding.passwordStrength.setIndicatorColor(
                    ContextCompat.getColor(requireContext(), color)
                )
                binding.strengthLabel.text = label
                binding.strengthLabel.setTextColor(
                    ContextCompat.getColor(requireContext(), color)
                )
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Institution code watcher — updates path info card
        binding.etInstitutionCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { updatePathCard(s?.toString()?.trim() ?: "") }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Set initial card state
        updatePathCard("")

        binding.btnRegister.setOnClickListener { attemptRegister() }
        binding.tvLogin.setOnClickListener { findNavController().navigateUp() }

        // Observe dual-path registration result
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.registrationResult.collectLatest { result ->
                when (result) {
                    is RegistrationResult.Loading -> showLoading(true)
                    is RegistrationResult.InstitutionPath -> {
                        showLoading(false)
                        findNavController().navigate(R.id.action_register_to_pending)
                    }
                    is RegistrationResult.IndependentPath -> {
                        showLoading(false)
                        findNavController().navigate(R.id.action_register_to_emailVerificationWait)
                    }
                    is RegistrationResult.Error -> {
                        showLoading(false)
                        val msg = when {
                            result.message.contains("Invalid institution code", ignoreCase = true) ->
                                getString(R.string.error_invalid_institution_code)
                            else -> result.message
                        }
                        showSnackbar(msg)
                    }
                    null -> Unit
                }
            }
        }
    }

    private fun updatePathCard(code: String) {
        if (code.isNotEmpty()) {
            binding.pathIcon.setImageResource(R.drawable.ic_institution)
            binding.pathTitle.setText(R.string.path_institution_title)
            binding.pathSubtext.setText(R.string.path_institution_sub)
            binding.registrationPathCard.setCardBackgroundColor(
                requireContext().getColor(R.color.info)
            )
        } else {
            binding.pathIcon.setImageResource(R.drawable.ic_email)
            binding.pathTitle.setText(R.string.path_independent_title)
            binding.pathSubtext.setText(R.string.path_independent_sub)
            binding.registrationPathCard.setCardBackgroundColor(
                requireContext().getColor(R.color.success)
            )
        }
    }

    private fun updateReq(textView: TextView, met: Boolean, text: String) {
        textView.text = "${if (met) "✓" else "✗"}  $text"
        textView.setTextColor(ContextCompat.getColor(requireContext(),
            if (met) R.color.semantic_success else R.color.semantic_error))
    }

    private fun attemptRegister() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirm = binding.etConfirmPassword.text.toString()
        val institutionCode = binding.etInstitutionCode.text.toString().trim()
        val role = if (binding.rbTeacher.isChecked) "teacher" else "student"

        if (!ValidationUtils.isNotEmpty(name)) { binding.tilName.error = getString(R.string.error_empty_field); return }
        binding.tilName.error = null
        if (!ValidationUtils.isValidEmail(email)) { binding.tilEmail.error = getString(R.string.error_invalid_email); return }
        binding.tilEmail.error = null
        // Validate password strength
        val passwordValidation = ValidationUtils.validatePasswordStrength(password)
        if (!passwordValidation.isValid) {
            binding.tilPassword.error = passwordValidation.errorMessage
            return
        }
        binding.tilPassword.error = null
        if (!ValidationUtils.doPasswordsMatch(password, confirm)) { binding.tilConfirmPassword.error = getString(R.string.error_passwords_dont_match); return }
        binding.tilConfirmPassword.error = null
        // Institution code is OPTIONAL — only validate format if non-empty
        if (institutionCode.isNotEmpty() && !ValidationUtils.isValidClassCode(institutionCode)) {
            binding.tilInstitutionCode.error = getString(R.string.error_invalid_class_code); return
        }
        binding.tilInstitutionCode.error = null

        viewModel.register(name, email, password, role, institutionCode)
    }

    private fun showLoading(loading: Boolean) {
        if (loading) { binding.progressBar.show(); binding.btnRegister.isEnabled = false }
        else { binding.progressBar.hide(); binding.btnRegister.isEnabled = true }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
