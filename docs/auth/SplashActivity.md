# SplashActivity — App entry point with routing logic

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/splash/SplashActivity.kt`

---

## 🎯 What This File Does
SplashActivity is the first screen the user sees when opening the app. It shows a branded splash screen with animated text, then determines where to navigate: Login (not logged in), PendingApproval (institution user awaiting admin), EmailVerification (independent user needing email confirm), or MainActivity (approved user). It handles the dual-path registration routing logic.

---

## 📦 Imports — Every Line Explained

| Import | Library / Package | What It Is | Why This File Needs It |
|--------|------------------|-----------|----------------------|
| `android.annotation.SuppressLint` | Android SDK | Suppresses lint warnings | Suppresses "CustomSplashScreen" warning (we use our own splash, not SplashScreen API) |
| `android.content.Intent` | Android SDK | Starts new Activities | Navigates to AuthActivity or MainActivity |
| `android.os.Bundle` | Android SDK | Key-value data bundle | Received in `onCreate()` as saved state |
| `android.view.View` | Android SDK | Base class for UI elements | Used for finding views by tag |
| `android.view.animation.DecelerateInterpolator` | Android SDK | Animation interpolator | Slows down animation at the end for natural feel |
| `android.view.animation.OvershootInterpolator` | Android SDK | Animation interpolator | Bounces past target then settles (springy effect) |
| `androidx.appcompat.app.AppCompatActivity` | AndroidX AppCompat | Base Activity class | SplashActivity extends this for backward-compatible features |
| `androidx.lifecycle.lifecycleScope` | AndroidX Lifecycle | Lifecycle-aware coroutine scope | Launches the `delay()` + `navigateNext()` coroutine |
| `com.google.firebase.auth.FirebaseAuth` | Firebase Auth | Authentication service | Checks if a user is currently logged in |
| `com.syed.classconnect.data.repository.AuthRepository` | App | User data access | Reads user document from Firestore to check approval status |
| `com.syed.classconnect.R` | App (Generated) | Resource references | Layout IDs, animation references |
| `com.syed.classconnect.databinding.ActivitySplashBinding` | ViewBinding (Generated) | Type-safe view access | Binds to `activity_splash.xml` layout |
| `com.syed.classconnect.ui.auth.AuthActivity` | App | Authentication screen | Navigated to when user is not logged in |
| `com.syed.classconnect.ui.main.MainActivity` | App | Main app screen | Navigated to when user is approved |
| `com.syed.classconnect.ui.permissions.PermissionsActivity` | App | Permission request screen | Shown on first approved launch for runtime permissions |
| `dagger.hilt.android.AndroidEntryPoint` | Hilt DI | Enables injection | Required for `@Inject lateinit var` fields |
| `kotlinx.coroutines.delay` | Kotlinx Coroutines | Suspending delay | 2.2 second delay for splash animation |
| `kotlinx.coroutines.launch` | Kotlinx Coroutines | Starts a coroutine | Launches the splash delay + routing coroutine |
| `kotlinx.coroutines.tasks.await` | Kotlinx Coroutines Play Services | Task → suspend conversion | Converts `firebaseUser.reload()` Task to suspend call |
| `javax.inject.Inject` | javax.inject | DI annotation | Marks fields for Hilt injection |

---

## 🔑 Kotlin & Android Keywords

### `@SuppressLint("CustomSplashScreen")`
Suppresses Android Lint warning about using a custom splash screen instead of Android 12's SplashScreen API. We use our own splash for custom animations.

### `@AndroidEntryPoint`
Enables Hilt dependency injection in this Activity. Required because we `@Inject` FirebaseAuth and AuthRepository.

### `lateinit var`
```kotlin
@Inject lateinit var auth: FirebaseAuth
```
`lateinit` tells the compiler the variable will be initialized before first use. Hilt injects the value after the constructor runs but before `onCreate()`.

### `suspend fun` — `navigateNext()`
The routing function is `suspend` because it makes async calls (Firestore reads, Firebase Auth reload). Called from `lifecycleScope.launch {}`.

### `enum class`
```kotlin
enum class AuthDestination { LOGIN, PENDING_APPROVAL, EMAIL_VERIFICATION }
```
A fixed set of possible navigation destinations. Used to make `goToAuth()` type-safe.

---

## 🏗️ Class Structure
`SplashActivity` extends `AppCompatActivity` (the base class for Android activities with backward compatibility). It uses `@AndroidEntryPoint` for Hilt injection.

---

## 📋 Properties

| Property | Type | Modifier | What It Stores |
|----------|------|----------|---------------|
| `binding` | `ActivitySplashBinding` | `private lateinit var` | ViewBinding for `activity_splash.xml` |
| `auth` | `FirebaseAuth` | `@Inject lateinit var` | Firebase Authentication instance |
| `authRepository` | `AuthRepository` | `@Inject lateinit var` | Repository for reading user data |

---

## ⚙️ Functions / Methods

### `onCreate(savedInstanceState: Bundle?)`
**What it does:** Sets up the splash screen with animations, then waits 2.2s before routing.
**When it's called:** When the Activity is first created (app launch).
**Step by step:**
1. Inflates the layout using ViewBinding
2. Animates the app name with scale + overshoot interpolator
3. Animates the tagline with slide-up + fade-in
4. Animates the version text with fade-in
5. Launches a coroutine that delays 2.2 seconds then calls `navigateNext()`

### `navigateNext()`
**What it does:** Determines where to send the user based on their auth/approval state.
**Step by step:**
1. Check `auth.currentUser` — if null → go to Login
2. Fetch user document from Firestore via `authRepository.getUserById(uid)`
3. If user doc is null → go to Login
4. If `accountType == "independent"`:
   - Reload Firebase Auth to check `isEmailVerified`
   - If NOT verified → go to EmailVerification screen
   - If verified but Firestore not updated → auto-approve, then go to Main
   - If approved → go to Main
5. If `accountType == "institution"`:
   - If admin → self-heal approval, go to Main
   - If NOT approved → go to PendingApproval screen
   - If approved → go to Main

### `goToAuth(destination: AuthDestination)`
**What it does:** Navigates to AuthActivity with an optional destination override.
**Step by step:**
1. Creates Intent for AuthActivity
2. Puts destination extra if not LOGIN
3. Starts activity with fade transition
4. Finishes SplashActivity (removes from back stack)

### `goToMain()`
**What it does:** Navigates to MainActivity or PermissionsActivity (first launch).
**Step by step:**
1. Checks if permissions have been requested before
2. If not → shows PermissionsActivity first
3. If yes → goes directly to MainActivity
4. Applies fade transition animation
5. Finishes SplashActivity

---

## 🔄 Data Flow

```
App launches
    → SplashActivity.onCreate()
    → 2.2 second animated delay
    → navigateNext()
    │
    ├── No user logged in → AuthActivity (LoginFragment)
    │
    ├── Independent user, email NOT verified → AuthActivity (EmailVerificationWaitFragment)
    │
    ├── Independent user, email verified → auto-approve → MainActivity
    │
    ├── Institution user, NOT approved → AuthActivity (PendingApprovalFragment)
    │
    ├── Admin user → self-heal approval → MainActivity
    │
    └── Approved user → MainActivity
```

---

## 🧩 This File Depends On

| Dependency | Why |
|-----------|-----|
| `FirebaseAuth` | Check if user is currently logged in; reload for email verification |
| `AuthRepository` | Read user document from Firestore; approve independent users |
| `AuthActivity` | Navigation target for unauthenticated/unapproved users |
| `MainActivity` | Navigation target for approved users |
| `PermissionsActivity` | Shown on first approved launch for runtime permissions |

---

## ⚠️ Important Notes
- Admin self-healing: if an admin's `isApproved` is false in Firestore, SplashActivity fixes it automatically
- Independent user auto-approval: if email is verified but Firestore not yet updated, SplashActivity calls `approveIndependentUser()` before routing to Main
- The 2.2s delay is intentional — gives time for the entrance animations to complete
- `@SuppressLint("CustomSplashScreen")` is needed because Android 12+ has a system splash screen API, but we use our own

