# LoginFragment — Email/password login and Google Sign-In screen

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/auth/LoginFragment.kt`

---

## 🎯 What This File Does
LoginFragment is the primary authentication screen. It provides email/password login and Google Sign-In. After successful authentication, it routes users based on their account type and approval status: approved users go to MainActivity, institution users awaiting approval go to PendingApprovalFragment, and independent users needing email verification go to EmailVerificationWaitFragment.

---

## 📦 Imports — Every Line Explained

| Import | Library / Package | What It Is | Why This File Needs It |
|--------|------------------|-----------|----------------------|
| `android.content.Intent` | Android SDK | Starts Activities | Navigates to MainActivity after login |
| `android.os.Bundle` | Android SDK | Key-value data bundle | Received in `onCreateView`/`onViewCreated` |
| `android.view.LayoutInflater` | Android SDK | Inflates XML layouts | Creates view hierarchy from XML |
| `android.view.View` | Android SDK | Base UI element | Return type of `onCreateView` |
| `android.view.ViewGroup` | Android SDK | Container for views | Parent parameter in `onCreateView` |
| `android.view.animation.DecelerateInterpolator` | Android SDK | Animation interpolator | Slows entrance animations for natural feel |
| `android.view.animation.OvershootInterpolator` | Android SDK | Animation interpolator | Logo pop-in bounce effect |
| `androidx.activity.result.contract.ActivityResultContracts` | AndroidX Activity | Activity result API | Handles Google Sign-In result callback |
| `androidx.fragment.app.Fragment` | AndroidX Fragment | Base Fragment class | LoginFragment extends this |
| `androidx.fragment.app.viewModels` | AndroidX Fragment KTX | ViewModel delegation | `by viewModels()` for AuthViewModel |
| `androidx.navigation.fragment.findNavController` | AndroidX Navigation | Navigation controller | Navigates to Register, ForgotPassword, etc. |
| `com.google.android.gms.auth.api.signin.GoogleSignIn` | Google Sign-In SDK | Google auth client | Creates Google Sign-In intent |
| `com.google.android.gms.auth.api.signin.GoogleSignInOptions` | Google Sign-In SDK | Sign-in configuration | Configures token request and email scope |
| `com.google.android.gms.common.api.ApiException` | Google Play Services | API exception type | Catches Google Sign-In failures |
| `com.google.android.material.snackbar.Snackbar` | Material Components | Bottom notification | Shows error messages |
| `com.syed.classconnect.R` | App (Generated) | Resource references | String IDs, layout IDs, navigation actions |
| `com.syed.classconnect.databinding.FragmentLoginBinding` | ViewBinding (Generated) | Type-safe view access | Binds to `fragment_login.xml` |
| `com.syed.classconnect.ui.main.MainActivity` | App | Main screen | Navigation target after login |
| `com.syed.classconnect.util.NetworkResult` | App | State wrapper | Loading/Success/Error states |
| `com.syed.classconnect.util.ValidationUtils` | App | Input validation | Email format validation |
| `com.syed.classconnect.util.hide` | App | Extension function | Hides views (`visibility = GONE`) |
| `com.syed.classconnect.util.show` | App | Extension function | Shows views (`visibility = VISIBLE`) |
| `com.syed.classconnect.util.wiggle` | App | Extension function | Shake animation on invalid input |
| `dagger.hilt.android.AndroidEntryPoint` | Hilt DI | Enables injection | Required for `by viewModels()` with `@HiltViewModel` |

---

## 🏗️ Class Structure

```kotlin
@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    private val googleSignInLauncher = registerForActivityResult(...)
}
```

- Extends `Fragment` with `@AndroidEntryPoint` for Hilt
- ViewBinding pattern with `_binding`/`binding`
- AuthViewModel provided by Hilt via `by viewModels()`
- Google Sign-In uses the modern Activity Result API (`registerForActivityResult`)

---

## 📋 Properties

| Property | Type | Modifier | What It Stores |
|----------|------|----------|---------------|
| `_binding` | `FragmentLoginBinding?` | `private var` | Nullable ViewBinding reference, nulled in onDestroyView |
| `binding` | `FragmentLoginBinding` | `private val (get)` | Non-null accessor for _binding (crashes if accessed outside lifecycle) |
| `viewModel` | `AuthViewModel` | `private val` | ViewModel for auth operations, provided by Hilt |
| `googleSignInLauncher` | `ActivityResultLauncher` | `private val` | Handles Google Sign-In activity result |

---

## ⚙️ Functions / Methods

### `onCreateView(...): View`
Inflates the layout using ViewBinding and returns the root view.

### `onViewCreated(view, savedInstanceState)`
**What it does:** Sets up UI, animations, click listeners, and state observers.
**Step by step:**
1. Animates logo with pop-in overshoot effect
2. Staggered entrance animation for form elements
3. Sets click listeners: Login button, Forgot Password, Register, Google Sign-In
4. Observes `viewModel.authState` for Loading/Success/Error
5. Observes `viewModel.loginRoute` for post-login routing

### `attemptLogin()`
**What it does:** Validates input and triggers login.
**Step by step:**
1. Gets email and password from EditTexts
2. Validates email format with `ValidationUtils.isValidEmail()`
3. Validates password is not empty
4. Calls `viewModel.login(email, password)`

### `launchGoogleSignIn()`
**What it does:** Starts the Google Sign-In flow.
**Step by step:**
1. Configures GoogleSignInOptions with ID token request
2. Creates GoogleSignIn client
3. Launches sign-in intent via `googleSignInLauncher`

### `showLoading(loading: Boolean)`
Shows/hides progress bar and enables/disables login button.

### `showError(msg: String)`
Shows a Snackbar with the error message.

### `onDestroyView()`
Nulls `_binding` to prevent memory leaks.

---

## 🔄 Data Flow

```
User taps "Login" button
    → attemptLogin() validates email + password
    → viewModel.login(email, password)
    → AuthViewModel calls authRepository.login()
    → AuthRepository calls Firebase Auth .await()
    → Firebase returns AuthResult
    → Repository reads User document from Firestore
    → Returns Result.success(user)
    → ViewModel updates _authState = NetworkResult.Success(user)
    → ViewModel calls resolvePostLoginRoute(user)
    → _loginRoute.value = ToMain / ToPending / ToEmailVerification
    → Fragment's loginRoute observer fires
    → Fragment navigates to appropriate destination
```

---

## 🧩 This File Depends On

| Dependency | Why |
|-----------|-----|
| `AuthViewModel` | Handles login, Google Sign-In, route resolution |
| `NetworkResult` | Represents Loading/Success/Error states |
| `ValidationUtils` | Email format validation |
| `MainActivity` | Navigation target after successful login |

---

## ⚠️ Important Notes
- `_binding` MUST be set to null in `onDestroyView()` or the Fragment leaks its view hierarchy
- Google Sign-In requires SHA-1 fingerprint to be added to Firebase Console
- The `default_web_client_id` string is auto-generated by the Google Services plugin from `google-services.json`
- Login route observation handles three destinations: Main, PendingApproval, EmailVerification
- The wiggle animation on invalid fields provides visual feedback beyond just error text

