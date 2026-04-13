# EmailVerificationWaitFragment — Polls for email verification (Path B)

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/auth/EmailVerificationWaitFragment.kt`

---

## 🎯 What This File Does
EmailVerificationWaitFragment is the waiting screen for independent users (Path B). It polls Firebase Auth every 5 seconds to check if the user has verified their email. Once verified, it auto-approves the user in Firestore and navigates to MainActivity. Provides resend email and "open email app" buttons.

---

## 📦 Imports — Every Line Explained

| Import | Library / Package | What It Is | Why This File Needs It |
|--------|------------------|-----------|----------------------|
| `android.content.Intent` | Android SDK | Start Activities | Navigate to MainActivity, open email app |
| `android.os.Bundle` | Android SDK | Saved state | Fragment lifecycle |
| `android.os.CountDownTimer` | Android SDK | Countdown timer | 60-second cooldown between resend attempts |
| `android.view.LayoutInflater` / `View` / `ViewGroup` | Android SDK | View creation | Fragment inflation |
| `androidx.fragment.app.Fragment` | AndroidX | Base Fragment | This class extends Fragment |
| `androidx.fragment.app.viewModels` | AndroidX Fragment KTX | ViewModel delegation | `by viewModels()` |
| `androidx.lifecycle.Lifecycle` / `lifecycleScope` / `repeatOnLifecycle` | AndroidX Lifecycle | Lifecycle-aware coroutines | Pauses polling when app goes to background |
| `androidx.navigation.fragment.findNavController` | AndroidX Navigation | Navigation | Navigate back to login on "wrong email" |
| `com.google.firebase.auth.ktx.auth` / `com.google.firebase.ktx.Firebase` | Firebase KTX | Firebase access | Get current user's email |
| `com.syed.classconnect.R` | App (Generated) | Resources | String resources |
| `com.syed.classconnect.databinding.FragmentEmailVerificationWaitBinding` | ViewBinding | Type-safe views | Layout binding |
| `com.syed.classconnect.ui.main.MainActivity` | App | Main screen | Navigation after verification |
| `com.syed.classconnect.util.showSnackbar` | App | Extension | Show messages |
| `dagger.hilt.android.AndroidEntryPoint` | Hilt | DI | Enables injection |
| `kotlinx.coroutines.launch` | Coroutines | Starts coroutine | For lifecycleScope.launch |

---

## ⚙️ Key Functions

### `onViewCreated(view, savedInstanceState)`
1. Displays user's email address
2. Starts resend countdown timer (60s cooldown)
3. Launches lifecycle-aware polling via `repeatOnLifecycle(STARTED)`
4. Collects `viewModel.state` StateFlow for Polling/Verified/Error
5. Sets up resend, open email app, and "wrong email" click listeners

### `startResendCountdown()`
Creates a 60-second CountDownTimer that disables the resend button and shows countdown text. Re-enables after 60 seconds.

### `onDestroyView()`
Cancels the resend timer, stops polling, and nulls binding.

---

## 🔄 Data Flow
```
Fragment starts → viewModel.startPolling()
    → Every 5 seconds: Firebase.auth.currentUser.reload()
    → Check isEmailVerified
    ├── false → continue polling
    └── true → updateFirestoreOnVerification()
              → Set isApproved=true, emailVerified=true in Firestore
              → _state.value = VerificationState.Verified
              → Fragment navigates to MainActivity
```

---

## ⚠️ Important Notes
- Uses `repeatOnLifecycle(STARTED)` so polling STOPS when app goes to background (saves battery)
- Resend cooldown prevents spam — Firebase rate-limits verification emails
- "Wrong email?" link signs out and returns to LoginFragment
- CountDownTimer must be cancelled in `onDestroyView()` to prevent leaks

