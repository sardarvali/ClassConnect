# ForgotPasswordFragment — Password reset via email

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/auth/ForgotPasswordFragment.kt`

---

## 🎯 What This File Does
ForgotPasswordFragment provides a simple form where users enter their email address to receive a password reset link from Firebase Auth. Shows a success message after the email is sent. Without this file, users who forget their password would have no way to recover their account.

---

## 📦 Imports — Every Line Explained

| Import | Library / Package | What It Is | Why This File Needs It |
|--------|------------------|-----------|----------------------|
| `android.os.Bundle` | Android SDK | Saved state data | Fragment lifecycle |
| `android.view.LayoutInflater` / `View` / `ViewGroup` | Android SDK | View creation | Fragment view inflation |
| `androidx.fragment.app.Fragment` | AndroidX | Base Fragment | This class extends Fragment |
| `androidx.fragment.app.viewModels` | AndroidX Fragment KTX | ViewModel delegation | `by viewModels()` for AuthViewModel |
| `com.syed.classconnect.R` | App (Generated) | Resources | String resources |
| `com.syed.classconnect.databinding.FragmentForgotPasswordBinding` | ViewBinding | Type-safe views | Layout binding |
| `com.syed.classconnect.util.NetworkResult` | App | State wrapper | Loading/Success/Error |
| `com.syed.classconnect.util.ValidationUtils` | App | Validation | Email validation |
| `com.syed.classconnect.util.hide` / `show` / `showSnackbar` | App | Extensions | UI helpers |
| `dagger.hilt.android.AndroidEntryPoint` | Hilt | DI | Enables injection |

---

## ⚙️ Key Functions

### `onViewCreated(view, savedInstanceState)`
1. Sets click listener on "Send Reset" button
2. Validates email format before sending
3. Observes `viewModel.resetState` for Loading/Success/Error

### Button click flow:
- Validates email → `viewModel.sendPasswordReset(email)` → Firebase sends reset email
- On success: hides form, shows success message
- On error: shows Snackbar with error

---

## 🔄 Data Flow
```
User enters email → taps "Send Reset"
    → ValidationUtils.isValidEmail(email)
    → viewModel.sendPasswordReset(email)
    → AuthRepository.sendPasswordReset(email)
    → FirebaseAuth.sendPasswordResetEmail(email).await()
    → Success → hide form, show "Check your email" message
```

---

## 📝 Full Annotated Source Code

```kotlin
package com.syed.classconnect.ui.auth
// Package: auth UI layer.

// ...imports omitted for brevity — see import table above...

@AndroidEntryPoint
// Enables Hilt DI for this Fragment.
class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    // Nullable ViewBinding. Set in onCreateView, nulled in onDestroyView.
    private val binding get() = _binding!!
    // Non-null accessor. Safe between onCreateView and onDestroyView.
    private val viewModel: AuthViewModel by viewModels()
    // Hilt provides AuthViewModel via by viewModels() delegation.

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        // Inflates fragment_forgot_password.xml into ViewBinding.
        return binding.root
        // Returns the root view of the inflated layout.
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSendReset.setOnClickListener {
            // When "Send Reset" button is tapped:
            val email = binding.etEmail.text.toString().trim()
            // Get email from EditText, trim whitespace.
            if (!ValidationUtils.isValidEmail(email)) {
                binding.tilEmail.error = getString(R.string.error_invalid_email)
                // Show error on TextInputLayout if email is invalid.
                return@setOnClickListener
                // return@setOnClickListener: exit the lambda, don't proceed.
            }
            binding.tilEmail.error = null
            // Clear any previous error.
            viewModel.sendPasswordReset(email)
            // Trigger password reset via ViewModel.
        }

        viewModel.resetState.observe(viewLifecycleOwner) { result ->
            // Observe the reset operation state.
            when (result) {
                is NetworkResult.Loading -> { binding.progressBar.show(); binding.btnSendReset.isEnabled = false }
                // Show progress, disable button during loading.
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    binding.formLayout.hide()
                    // Hide the form after success.
                    binding.tvSuccess.show()
                    binding.tvSuccess.text = getString(R.string.password_reset_sent)
                    // Show success message.
                }
                is NetworkResult.Error -> {
                    binding.progressBar.hide()
                    binding.btnSendReset.isEnabled = true
                    showSnackbar(result.message)
                    // Show error message in Snackbar.
                }
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
    // Null binding to prevent memory leaks.
}
```

