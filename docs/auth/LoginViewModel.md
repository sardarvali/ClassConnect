# LoginViewModel (AuthViewModel) — Authentication state management

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/auth/AuthViewModel.kt`

---

## 🎯 What This File Does
AuthViewModel manages all authentication-related state: login, registration (dual-path), Google Sign-In, password reset, and post-login routing. It's shared across LoginFragment, RegisterFragment, ForgotPasswordFragment, and PendingApprovalFragment. Contains the routing logic that determines where users go after authentication.

Note: The project uses a single `AuthViewModel` for all auth screens. The prompt references `LoginViewModel` and `RegisterViewModel` — both are this same `AuthViewModel`.

---

## 📦 Imports — Every Line Explained

| Import | Library / Package | What It Is | Why This File Needs It |
|--------|------------------|-----------|----------------------|
| `androidx.lifecycle.LiveData` / `MutableLiveData` | AndroidX Lifecycle | Observable data holders | Auth state, reset state, login route |
| `androidx.lifecycle.ViewModel` / `viewModelScope` | AndroidX Lifecycle | ViewModel base + coroutine scope | Survives rotation, launches coroutines |
| `com.google.firebase.auth.FirebaseAuth` | Firebase Auth | Auth service | Reload user for email verification check |
| `com.syed.classconnect.data.model.User` | App | Data model | User data |
| `com.syed.classconnect.data.repository.AuthRepository` | App | Data access | All auth operations |
| `com.syed.classconnect.util.NetworkResult` | App | State wrapper | Loading/Success/Error |
| `dagger.hilt.android.lifecycle.HiltViewModel` | Hilt | DI | ViewModel injection |
| `kotlinx.coroutines.flow.MutableStateFlow` / `StateFlow` | Coroutines | Hot observable | Registration result (one-shot event) |
| `kotlinx.coroutines.launch` | Coroutines | Starts coroutine | For viewModelScope.launch |
| `kotlinx.coroutines.tasks.await` | Coroutines Play Services | Task→suspend | For firebaseAuth.reload().await() |
| `javax.inject.Inject` | Hilt | DI | Constructor injection |

---

## 🏗️ Sealed Classes Defined in This File

### `RegistrationResult`
```kotlin
sealed class RegistrationResult {
    object InstitutionPath : RegistrationResult()  // → PendingApprovalFragment
    object IndependentPath : RegistrationResult()  // → EmailVerificationWaitFragment
    data class Error(val message: String) : RegistrationResult()
    object Loading : RegistrationResult()
}
```

### `LoginRouteResult`
```kotlin
sealed class LoginRouteResult {
    object ToMain : LoginRouteResult()              // approved → MainActivity
    object ToPending : LoginRouteResult()            // institution, not approved
    object ToEmailVerification : LoginRouteResult()  // independent, email not verified
}
```

---

## 📋 Properties

| Property | Type | Modifier | What It Stores |
|----------|------|----------|---------------|
| `_authState` | `MutableLiveData<NetworkResult<User>>` | `private` | Login/Google Sign-In state |
| `authState` | `LiveData<NetworkResult<User>>` | `public` | Read-only auth state for Fragments |
| `_resetState` | `MutableLiveData<NetworkResult<Unit>>` | `private` | Password reset state |
| `resetState` | `LiveData<NetworkResult<Unit>>` | `public` | Read-only reset state |
| `_registrationResult` | `MutableStateFlow<RegistrationResult?>` | `private` | Dual-path registration result |
| `registrationResult` | `StateFlow<RegistrationResult?>` | `public` | Read-only registration result |
| `_loginRoute` | `MutableLiveData<LoginRouteResult>` | `private` | Post-login navigation destination |
| `loginRoute` | `LiveData<LoginRouteResult>` | `public` | Read-only route for Fragments |

---

## ⚙️ Key Functions

### `login(email, password)`
Sets Loading → calls authRepository.login() → on success sets Success + resolves route → on failure sets Error.

### `resolvePostLoginRoute(user: User)`
Determines post-login destination:
- Independent + email NOT verified → ToEmailVerification
- Independent + verified but Firestore not updated → auto-approve → ToMain
- Institution + not approved (and not admin) → ToPending
- Otherwise → ToMain

### `register(name, email, password, role, institutionCode)`
Dual-path: if code is non-empty → Path A (registerWithInstitutionCode), else → Path B (registerIndependent).

### `signInWithGoogle(idToken)`
Google Sign-In flow. If new user → returns error with user info for role selection. If existing user → success + route resolution.

### `sendPasswordReset(email)`
Calls authRepository.sendPasswordReset().

### `logout()`
Calls authRepository.logout() (Firebase sign out).

---

## 🔄 Data Flow
```
LoginFragment.attemptLogin() → viewModel.login(email, password)
    → _authState = Loading
    → authRepository.login(email, password)
    → _authState = Success(user) → resolvePostLoginRoute(user)
    → _loginRoute = ToMain/ToPending/ToEmailVerification
    → Fragment observes loginRoute → navigates accordingly
```

---

## ⚠️ Important Notes
- `resolvePostLoginRoute()` reloads Firebase Auth user to get fresh `isEmailVerified` state
- Admin users always bypass approval checks (even if `isApproved=false` in Firestore)
- Google Sign-In for new users returns a specially formatted error string starting with "new_google_user:" — this is a workaround pattern, not a real error

