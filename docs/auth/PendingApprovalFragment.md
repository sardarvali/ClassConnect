# PendingApprovalFragment — Waiting screen for institution user approval

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/auth/PendingApprovalFragment.kt`

---

## 🎯 What This File Does
PendingApprovalFragment is a simple waiting screen shown to users who registered with an institution code (Path A). Their account exists but `isApproved` is `false` — they must wait for an admin to approve them in the admin dashboard. The only action available is logging out.

---

## 📦 Imports — Every Line Explained

| Import | Library / Package | What It Is | Why This File Needs It |
|--------|------------------|-----------|----------------------|
| `android.content.Intent` | Android SDK | Start Activities | Navigates back to AuthActivity on logout |
| `android.os.Bundle` | Android SDK | Saved state | Fragment lifecycle |
| `android.view.LayoutInflater` / `View` / `ViewGroup` | Android SDK | View creation | Fragment inflation |
| `androidx.fragment.app.Fragment` | AndroidX | Base Fragment | This class extends Fragment |
| `androidx.fragment.app.viewModels` | AndroidX Fragment KTX | ViewModel delegation | `by viewModels()` |
| `com.syed.classconnect.databinding.FragmentPendingApprovalBinding` | ViewBinding | Type-safe views | Layout binding |
| `com.syed.classconnect.ui.auth.AuthActivity` | App | Auth screen | Navigation target on logout |
| `dagger.hilt.android.AndroidEntryPoint` | Hilt | DI | Enables injection |

---

## ⚙️ Key Functions

### `onViewCreated(view, savedInstanceState)`
Sets a click listener on the logout button that:
1. Calls `viewModel.logout()` (signs out from Firebase Auth)
2. Starts AuthActivity with `FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK` (clears back stack)

---

## 📝 Full Annotated Source Code

```kotlin
package com.syed.classconnect.ui.auth

@AndroidEntryPoint
class PendingApprovalFragment : Fragment() {
    private var _binding: FragmentPendingApprovalBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPendingApprovalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            // Signs out from Firebase Auth.
            startActivity(Intent(requireContext(), AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                // Clears entire back stack so user can't press back to return.
            })
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
```

