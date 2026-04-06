# AuthViewModel.kt — Shared ViewModel for login, registration, Google sign-in, and post-login routing

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/auth/AuthViewModel.kt`

---

## 🎯 What This File Does
`AuthViewModel` is the single ViewModel shared by `LoginFragment` and `RegisterFragment`. It handles all four authentication operations: email/password login, dual-path registration (institution code vs. independent), Google Sign-In, and password reset. After a successful login it runs `resolvePostLoginRoute()` to determine where to send the user: main app (approved), pending approval screen (institution user not yet approved), or email verification screen (independent user email not verified). It exposes its state via `LiveData` (`authState`, `loginRoute`, `resetState`) and `StateFlow` (`registrationResult`). Without this ViewModel, the login and registration screens would have no business logic and no way to persist state across screen rotations.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `androidx.lifecycle.LiveData` | AndroidX Lifecycle | Read-only observable holder | Public exposure of `_authState` and `_loginRoute` |
| `androidx.lifecycle.MutableLiveData` | AndroidX Lifecycle | Writable observable holder | Private backing fields for state |
| `androidx.lifecycle.ViewModel` | AndroidX Lifecycle | Lifecycle-aware state container | AuthViewModel extends it |
| `androidx.lifecycle.viewModelScope` | AndroidX Lifecycle | Coroutine scope tied to ViewModel lifetime | Launching coroutines |
| `com.google.firebase.auth.FirebaseAuth` | Firebase Auth | Firebase Authentication SDK | `currentUser?.reload()?.await()` |
| `com.syed.classconnect.data.model.User` | Project | User data model | Return type of login/register |
| `com.syed.classconnect.data.repository.AuthRepository` | Project | Auth operations | All Firebase Auth + Firestore calls |
| `com.syed.classconnect.util.NetworkResult` | Project | Sealed class for loading/success/error state | `_authState` type |
| `dagger.hilt.android.lifecycle.HiltViewModel` | Hilt | Marks ViewModel for Hilt injection | Required for `by viewModels()` |
| `kotlinx.coroutines.flow.MutableStateFlow` | Coroutines Flow | Writable state holder with current-value semantics | `_registrationResult` |
| `kotlinx.coroutines.flow.StateFlow` | Coroutines Flow | Read-only state flow | Public `registrationResult` |
| `kotlinx.coroutines.launch` | Coroutines | Start a coroutine | `viewModelScope.launch { }` |
| `kotlinx.coroutines.tasks.await` | Coroutines / Firebase | Suspend Firebase Task | `auth.currentUser?.reload()?.await()` |
| `javax.inject.Inject` | Javax / Hilt | Marks constructor for DI | `@Inject constructor(...)` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `sealed class RegistrationResult`
Defines all possible outcomes of the registration flow. The compiler knows every subtype — no `else` needed in `when` expressions.
- `InstitutionPath`: success via institution code → route to PendingApproval
- `IndependentPath`: success as independent user → route to EmailVerificationWait
- `Error(val message: String)`: registration failed
- `Loading`: operation in progress

### `sealed class LoginRouteResult`
Defines where to send the user after login:
- `ToMain`: approved user → launch MainActivity
- `ToPending`: institution user not approved → stay in AuthActivity (pending screen)
- `ToEmailVerification`: independent user email not verified → stay in AuthActivity (email screen)

### `MutableLiveData` vs `StateFlow`
Both are used here:
- `LiveData` is used for `_authState` and `_loginRoute` — observed in Fragments with `observe()`.
- `StateFlow` is used for `_registrationResult` — collected with `collect {}` in `repeatOnLifecycle`. StateFlow always has a current value; LiveData can be uninitialized.

### `result.fold(onSuccess = { ... }, onFailure = { ... })`
`AuthRepository` methods return `Result<T>` (Kotlin's built-in). `fold` runs `onSuccess` if the operation succeeded, `onFailure` if it threw an exception. Clean alternative to try/catch.

### `resolvePostLoginRoute(user: User)`
The core post-login routing logic:
1. Independent user, email not verified → `ToEmailVerification`
2. Independent user, email verified but Firestore not updated → auto-approve + `ToMain`
3. Institution user, not approved, not admin → `ToPending`
4. Everyone else → `ToMain`

### `try { firebaseAuth.currentUser?.reload()?.await() } catch (_: Exception) {}`
Forces Firebase to fetch fresh user state from the server (including `isEmailVerified`). This is critical — the local Firebase Auth cache might show `isEmailVerified = false` even if the user just clicked the email link. The `catch` discards network errors (offline tolerance).

### `if (isNew)` in `signInWithGoogle`
Google Sign-In can return an existing user or a new one. For new Google users, the app needs to ask them to select a role — this is signaled by an error state with a special message format: `"new_google_user:{uid}:{name}:{email}:{photoUrl}"`. The Fragment parses this to show the role selection dialog.

---

## 🏗️ Class Structure
`AuthViewModel @Inject constructor(authRepository, firebaseAuth) : ViewModel()` — Hilt-injected ViewModel with two dependencies.

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `_authState` | `MutableLiveData<NetworkResult<User>>` | `private` | Login/Google sign-in operation state | Drives loading spinner + error UI |
| `authState` | `LiveData<NetworkResult<User>>` | `val` | Read-only public exposure | Observed by LoginFragment |
| `_resetState` | `MutableLiveData<NetworkResult<Unit>>` | `private` | Password reset state | Drives ForgotPasswordFragment UI |
| `resetState` | `LiveData<NetworkResult<Unit>>` | `val` | Read-only | Observed by ForgotPasswordFragment |
| `_registrationResult` | `MutableStateFlow<RegistrationResult?>` | `private` | Registration outcome | Drives RegisterFragment routing |
| `registrationResult` | `StateFlow<RegistrationResult?>` | `val` | Read-only | Collected by RegisterFragment |
| `_loginRoute` | `MutableLiveData<LoginRouteResult>` | `private` | Where to navigate after login | Drives LoginFragment routing |
| `loginRoute` | `LiveData<LoginRouteResult>` | `val` | Read-only | Observed by LoginFragment |

---

## ⚙️ Functions

### `login(email: String, password: String)`
**Purpose:** Authenticate with Firebase Auth using email/password, then determine routing.
**Step by step:**
1. Sets `_authState.value = NetworkResult.Loading()`.
2. Calls `authRepository.login(email, password)` (suspend) in a viewModelScope coroutine.
3. On success: sets `_authState.value = NetworkResult.Success(user)`, calls `resolvePostLoginRoute(user)`.
4. On failure: sets `_authState.value = NetworkResult.Error(message)`.

### `resolvePostLoginRoute(user: User)`
**Purpose:** Determine the correct screen to show after successful authentication.
**Step by step:**
1. Checks `user.accountType`:
   - `"independent"`:
     - Calls `firebaseAuth.currentUser?.reload()?.await()` to get fresh email verification status.
     - If email NOT verified → `_loginRoute.value = ToEmailVerification`.
     - If email verified but `user.isApproved == false` → calls `authRepository.approveIndependentUser(uid)` then `ToMain`.
     - Otherwise → `ToMain`.
   - Institution user (`"institution"` or null):
     - If not approved AND not admin → `ToPending`.
     - Otherwise → `ToMain`.

### `register(name, email, password, role, institutionCode)`
**Purpose:** Dual-path registration — institution code or independent.
**Step by step:**
1. Sets `_registrationResult.value = RegistrationResult.Loading`.
2. Trims `institutionCode`.
3. If code is NOT empty (PATH A): calls `authRepository.registerWithInstitutionCode(...)`.
   - Success → `RegistrationResult.InstitutionPath`.
4. If code IS empty (PATH B): calls `authRepository.registerIndependent(...)`.
   - Success → `RegistrationResult.IndependentPath`.
5. Failure in either path → `RegistrationResult.Error(message)`.

### `signInWithGoogle(idToken: String)`
**Purpose:** Authenticate or register via Google credential.
**Step by step:**
1. Sets `_authState.value = NetworkResult.Loading()`.
2. Calls `authRepository.signInWithGoogle(idToken)` which returns `Result<Pair<User, Boolean>>`.
3. If `isNew == true`: emits a special error state string so the Fragment can show a role-selection dialog.
4. If `isNew == false`: sets Success state and routes.

### `completeGoogleRegistration(user: User)`
**Purpose:** Called after the user selects their role during Google sign-in.
**Step by step:**
1. Saves the user to Firestore via `authRepository.saveNewGoogleUser(user)`.
2. Sets `_authState.value = NetworkResult.Success(user)`.
3. Calls `resolvePostLoginRoute(user)`.

### `sendPasswordReset(email: String)`
**Purpose:** Send a Firebase Auth password reset email.
**Step by step:**
1. Sets `_resetState.value = NetworkResult.Loading()`.
2. Calls `authRepository.sendPasswordReset(email)`.
3. On success → `NetworkResult.Success(Unit)`.
4. On failure → `NetworkResult.Error(message)`.

### `logout()`
**Purpose:** Signs out the current user.
**Returns:** Delegates directly to `authRepository.logout()`.

---

## 🔄 Data Flow Diagram
```
User taps "Login" button in LoginFragment
        ↓
LoginFragment calls viewModel.login(email, password)
        ↓
_authState = Loading → Fragment shows spinner
        ↓
authRepository.login() → Firebase Auth signInWithEmailAndPassword().await()
        ↓
Success → _authState = Success(user)
        ↓
resolvePostLoginRoute(user) runs
        ↓
   ┌────────────────────────────────────────┐
   │           user.accountType             │
   ├─ "independent" ─────────────────────────┤
   │  reload Firebase Auth                  │
   │  isEmailVerified? ─── YES → ToMain     │
   │                   └── NO  → ToVerify   │
   └─ institution user ──────────────────────┤
      isApproved? ──── YES → ToMain         │
                  └─── NO  → ToPending      │
                                            │
_loginRoute posts result                    │
        ↓                                   │
LoginFragment observes → navigates          ┘
```

---

## 🧩 Dependencies

| Depends On | Why |
|-----------|-----|
| `AuthRepository` | All Firebase Auth + Firestore operations |
| `FirebaseAuth` | `currentUser?.reload()?.await()` — fresh email verification status |
| `NetworkResult` | State wrapper for Loading/Success/Error |

---

## ⚠️ Important Notes & Gotchas
- `reload()` before checking `isEmailVerified` is critical. Firebase Auth caches the user state locally. Without reload, a user who just verified their email will still see `isEmailVerified = false` until the next app launch.
- The Google sign-in new-user detection uses an error state with a special string format. This is a workaround — a dedicated `StateFlow<NewGoogleUserEvent?>` would be cleaner.
- `resolvePostLoginRoute` is called from multiple places (`login`, `signInWithGoogle`, `completeGoogleRegistration`) — this is correct since routing logic should not be duplicated.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
// ═══════════════════════════════════════
// AuthViewModel.kt
// ═══════════════════════════════════════

package com.syed.classconnect.ui.auth

// ... (imports as listed above)

/** Result of the dual-path registration flow. */
sealed class RegistrationResult {
    object InstitutionPath : RegistrationResult()  // → PendingApprovalFragment
    // User registered with an institution code — awaiting admin approval.
    object IndependentPath : RegistrationResult()  // → EmailVerificationWaitFragment
    // User registered independently — awaiting email verification.
    data class Error(val message: String) : RegistrationResult()
    object Loading : RegistrationResult()
}

/** Where to route after a successful login. */
sealed class LoginRouteResult {
    object ToMain : LoginRouteResult()                   // approved → MainActivity
    object ToPending : LoginRouteResult()                // institution, not approved
    object ToEmailVerification : LoginRouteResult()      // independent, email not verified
}

@HiltViewModel
// Hilt generates a factory for this ViewModel.
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firebaseAuth: FirebaseAuth
    // Hilt provides both dependencies automatically.
) : ViewModel() {

    private val _authState = MutableLiveData<NetworkResult<User>>()
    val authState: LiveData<NetworkResult<User>> = _authState
    // Drives the login/google sign-in loading spinner and error display.

    private val _resetState = MutableLiveData<NetworkResult<Unit>>()
    val resetState: LiveData<NetworkResult<Unit>> = _resetState
    // Drives the "Reset email sent" / "Error" display in ForgotPasswordFragment.

    private val _registrationResult = MutableStateFlow<RegistrationResult?>(null)
    val registrationResult: StateFlow<RegistrationResult?> = _registrationResult
    // null = idle; Loading/Error/InstitutionPath/IndependentPath on register.

    private val _loginRoute = MutableLiveData<LoginRouteResult>()
    val loginRoute: LiveData<LoginRouteResult> = _loginRoute
    // Drives LoginFragment to navigate to Main, Pending, or EmailVerification.

    fun login(email: String, password: String) {
        _authState.value = NetworkResult.Loading()
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            result.fold(
                onSuccess = { user ->
                    _authState.value = NetworkResult.Success(user)
                    resolvePostLoginRoute(user)
                    // Success: also determine WHERE to go.
                },
                onFailure = {
                    _authState.value = NetworkResult.Error(it.message ?: "Login failed")
                }
            )
        }
    }

    fun resolvePostLoginRoute(user: User) {
        viewModelScope.launch {
            when (user.accountType) {
                "independent" -> {
                    try { firebaseAuth.currentUser?.reload()?.await() } catch (_: Exception) {}
                    // reload() fetches fresh isEmailVerified from the server.
                    val emailVerified = firebaseAuth.currentUser?.isEmailVerified ?: false
                    if (!emailVerified) {
                        _loginRoute.value = LoginRouteResult.ToEmailVerification
                    } else {
                        if (!user.isApproved) {
                            authRepository.approveIndependentUser(user.uid)
                            // Email is verified but Firestore not updated yet — fix it now.
                        }
                        _loginRoute.value = LoginRouteResult.ToMain
                    }
                }
                else -> {
                    if (!user.isApproved && user.role != "admin") {
                        _loginRoute.value = LoginRouteResult.ToPending
                        // Admins bypass approval check.
                    } else {
                        _loginRoute.value = LoginRouteResult.ToMain
                    }
                }
            }
        }
    }

    fun register(name: String, email: String, password: String, role: String, institutionCode: String) {
        _registrationResult.value = RegistrationResult.Loading
        viewModelScope.launch {
            val trimmedCode = institutionCode.trim()
            if (trimmedCode.isNotEmpty()) {
                // PATH A: institution code provided — register under that institution.
                val result = authRepository.registerWithInstitutionCode(name, email, password, role, trimmedCode)
                _registrationResult.value = result.fold(
                    onSuccess = { RegistrationResult.InstitutionPath },
                    onFailure = { RegistrationResult.Error(it.message ?: "Registration failed") }
                )
            } else {
                // PATH B: no code — independent user, verify email.
                val result = authRepository.registerIndependent(name, email, password, role)
                _registrationResult.value = result.fold(
                    onSuccess = { RegistrationResult.IndependentPath },
                    onFailure = { RegistrationResult.Error(it.message ?: "Registration failed") }
                )
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        _authState.value = NetworkResult.Loading()
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(idToken)
            result.fold(
                onSuccess = { (user, isNew) ->
                    if (isNew) {
                        // Encode new user info in an error state — Fragment shows role picker.
                        _authState.value = NetworkResult.Error("new_google_user:${user.uid}:${user.name}:${user.email}:${user.photoUrl}")
                    } else {
                        _authState.value = NetworkResult.Success(user)
                        resolvePostLoginRoute(user)
                    }
                },
                onFailure = { _authState.value = NetworkResult.Error(it.message ?: "Google sign-in failed") }
            )
        }
    }

    fun completeGoogleRegistration(user: User) {
        viewModelScope.launch {
            authRepository.saveNewGoogleUser(user)
            _authState.value = NetworkResult.Success(user)
            resolvePostLoginRoute(user)
        }
    }

    fun sendPasswordReset(email: String) {
        _resetState.value = NetworkResult.Loading()
        viewModelScope.launch {
            val result = authRepository.sendPasswordReset(email)
            _resetState.value = result.fold(
                onSuccess = { NetworkResult.Success(it) },
                onFailure = { NetworkResult.Error(it.message ?: "Failed to send email") }
            )
        }
    }

    fun logout() = authRepository.logout()
    // Delegates sign-out to the repository (calls FirebaseAuth.signOut()).
}
```

