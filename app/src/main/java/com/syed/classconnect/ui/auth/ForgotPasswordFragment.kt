package com.syed.classconnect.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.syed.classconnect.R
import com.syed.classconnect.databinding.FragmentForgotPasswordBinding
import com.syed.classconnect.util.NetworkResult
import com.syed.classconnect.util.ValidationUtils
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.show
import com.syed.classconnect.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

<<<<<<< HEAD
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
=======
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
>>>>>>> final
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSendReset.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (!ValidationUtils.isValidEmail(email)) {
                binding.tilEmail.error = getString(R.string.error_invalid_email)
                return@setOnClickListener
            }
            binding.tilEmail.error = null
            viewModel.sendPasswordReset(email)
        }

        viewModel.resetState.observe(viewLifecycleOwner) { result ->
            when (result) {
<<<<<<< HEAD
                is NetworkResult.Loading -> { binding.progressBar.show(); binding.btnSendReset.isEnabled = false }
=======
                is NetworkResult.Loading -> {
                    binding.progressBar.show(); binding.btnSendReset.isEnabled = false
                }

>>>>>>> final
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    binding.formLayout.hide()
                    binding.tvSuccess.show()
                    binding.tvSuccess.text = getString(R.string.password_reset_sent)
                }
<<<<<<< HEAD
=======

>>>>>>> final
                is NetworkResult.Error -> {
                    binding.progressBar.hide()
                    binding.btnSendReset.isEnabled = true
                    showSnackbar(result.message)
                }
            }
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

