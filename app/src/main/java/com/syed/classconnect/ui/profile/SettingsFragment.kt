package com.syed.classconnect.ui.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.syed.classconnect.R
import com.syed.classconnect.databinding.FragmentSettingsBinding
import com.syed.classconnect.util.BiometricHelper
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ── Theme ──────────────────────────────────────────────────────
        val themeMode = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        binding.rgTheme.check(
            when (themeMode) {
                AppCompatDelegate.MODE_NIGHT_NO -> binding.rbLight.id
                AppCompatDelegate.MODE_NIGHT_YES -> binding.rbDark.id
                else -> binding.rbSystem.id
            }
        )

        binding.rgTheme.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                binding.rbLight.id -> AppCompatDelegate.MODE_NIGHT_NO
                binding.rbDark.id -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            prefs.edit().putInt("theme_mode", mode).apply()
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        // ── Notifications ──────────────────────────────────────────────
        binding.switchNotifications.isChecked = prefs.getBoolean("notifications_enabled", true)
        binding.switchNotifications.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean("notifications_enabled", checked).apply()
        }

        // ── Biometric Lock ─────────────────────────────────────────────
        setupBiometric(prefs)

        // ── About links ────────────────────────────────────────────────
        binding.tvPrivacyPolicy.setOnClickListener {
            startActivity(
                android.content.Intent(
                    requireContext(),
                    com.syed.classconnect.ui.webview.WebViewActivity::class.java
                ).apply {
                    putExtra("url", "https://classconnect.app/privacy")
                    putExtra("title", "Privacy Policy")
                })
        }

        binding.tvTerms.setOnClickListener {
            startActivity(
                android.content.Intent(
                    requireContext(),
                    com.syed.classconnect.ui.webview.WebViewActivity::class.java
                ).apply {
                    putExtra("url", "https://classconnect.app/terms")
                    putExtra("title", "Terms of Service")
                })
        }

        val version = try {
            requireContext().packageManager.getPackageInfo(
                requireContext().packageName,
                0
            ).versionName
        } catch (e: Exception) {
            "1.0.0"
        }
        binding.tvVersion.text = "Version $version"
    }

    private fun setupBiometric(prefs: android.content.SharedPreferences) {
        val status = BiometricHelper.canAuthenticate(requireContext())
        val isEnabled = prefs.getBoolean(Constants.PREF_BIOMETRIC_ENABLED, false)

        when (status) {
            BiometricHelper.BiometricStatus.NO_HARDWARE,
            BiometricHelper.BiometricStatus.UNAVAILABLE -> {
                // Device doesn't support biometric — disable the switch
                binding.switchBiometric.isEnabled = false
                binding.switchBiometric.isChecked = false
                binding.switchBiometric.text = getString(R.string.biometric_not_available)
            }

            BiometricHelper.BiometricStatus.NONE_ENROLLED -> {
                // Hardware exists but no fingerprint/face enrolled
                binding.switchBiometric.isEnabled = true
                binding.switchBiometric.isChecked = false
                binding.switchBiometric.setOnCheckedChangeListener { btn, checked ->
                    if (checked) {
                        btn.isChecked = false
                        showSnackbar(
                            getString(R.string.biometric_none_enrolled),
                            Snackbar.LENGTH_LONG
                        )
                    }
                }
            }

            BiometricHelper.BiometricStatus.AVAILABLE -> {
                binding.switchBiometric.isEnabled = true
                binding.switchBiometric.isChecked = isEnabled
                binding.switchBiometric.setOnCheckedChangeListener { btn, checked ->
                    if (checked) {
                        // Authenticate to confirm before enabling
                        BiometricHelper.authenticate(
                            activity = requireActivity(),
                            onSuccess = {
                                prefs.edit().putBoolean(Constants.PREF_BIOMETRIC_ENABLED, true)
                                    .apply()
                                showSnackbar(getString(R.string.biometric_enabled_msg))
                            },
                            onError = { msg ->
                                btn.isChecked = false
                                if (msg.isNotEmpty()) {
                                    showSnackbar(getString(R.string.biometric_auth_error, msg))
                                }
                            },
                            onFailed = {
                                Toast.makeText(
                                    requireContext(),
                                    R.string.biometric_auth_failed,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    } else {
                        prefs.edit().putBoolean(Constants.PREF_BIOMETRIC_ENABLED, false).apply()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView(); _binding = null
    }
}
