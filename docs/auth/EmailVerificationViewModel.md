# EmailVerificationViewModel — Polls Firebase Auth for email verification

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/auth/EmailVerificationViewModel.kt`

---

## 🎯 What This File Does
EmailVerificationViewModel manages the polling loop that checks whether a user has verified their email. It reloads the Firebase Auth user every 5 seconds. When verification is detected, it updates Firestore (sets `isApproved=true`, `emailVerified=true`) and emits `VerificationState.Verified`. Used by EmailVerificationWaitFragment.

---

## 📦 Imports — Every Line Explained

| Import | Library / Package | What It Is | Why This File Needs It |
|--------|------------------|-----------|----------------------|
| `androidx.lifecycle.ViewModel` / `viewModelScope` | AndroidX Lifecycle | ViewModel + coroutines | Launches polling coroutine |
| `com.google.firebase.auth.ktx.auth` | Firebase Auth KTX | Auth access | `Firebase.auth.currentUser` |
| `com.google.firebase.firestore.ktx.firestore` | Firebase Firestore KTX | Firestore access | Update user document |
| `com.google.firebase.ktx.Firebase` | Firebase KTX | Firebase access | Entry point for Firebase KTX |
| `dagger.hilt.android.lifecycle.HiltViewModel` | Hilt | DI | ViewModel injection |
| `kotlinx.coroutines.Job` | Coroutines | Job handle | Track/cancel polling coroutine |
| `kotlinx.coroutines.delay` | Coroutines | Suspending delay | 5-second polling interval |
| `kotlinx.coroutines.flow.MutableStateFlow` / `StateFlow` | Coroutines | Hot observable | Verification state |
| `kotlinx.coroutines.launch` | Coroutines | Starts coroutine | For polling loop |
| `kotlinx.coroutines.tasks.await` | Coroutines Play Services | Task→suspend | Firebase reload + Firestore update |
| `javax.inject.Inject` | Hilt | DI | Constructor injection |

---

## 🏗️ Sealed Class: VerificationState

```kotlin
sealed class VerificationState {
    object Polling : VerificationState()              // Still checking
    object Verified : VerificationState()             // Email verified!
    data class Error(val message: String) : VerificationState() // Something went wrong
}
```

---

## ⚙️ Key Functions

### `startPolling()`
Launches a coroutine that loops forever with 5-second delays. Each iteration: reloads Firebase Auth user → checks `isEmailVerified`. When verified, calls `updateFirestoreOnVerification()` and emits `Verified`. Transient errors are ignored (keeps polling).

### `stopPolling()`
Cancels the polling coroutine Job.

### `resendEmail()`
Calls `Firebase.auth.currentUser?.sendEmailVerification()?.await()`.

### `signOutAndClear()`
Stops polling and signs out from Firebase Auth. Used by "wrong email?" button.

### `updateFirestoreOnVerification()` (private)
Updates the user's Firestore document: `emailVerified=true`, `isApproved=true`. This is the auto-approve mechanism for independent users.

---

## 🔄 Data Flow
```
Fragment calls viewModel.startPolling()
    → Loop every 5 seconds:
        → Firebase.auth.currentUser.reload().await()
        → Check isEmailVerified
        ├── false → continue loop
        └── true → updateFirestoreOnVerification()
                  → Firestore: isApproved=true, emailVerified=true
                  → _state = Verified
                  → Fragment navigates to MainActivity
```

---

## ⚠️ Important Notes
- Polling only runs when `pollingJob?.isActive != true` — prevents multiple concurrent polls
- Uses `repeatOnLifecycle(STARTED)` in the Fragment to pause polling when app is backgrounded
- `updateFirestoreOnVerification()` catches and ignores exceptions — worst case, SplashActivity will fix it on next launch
- The empty `@Inject constructor()` means this ViewModel has no dependencies — Hilt still needs the annotation

