# MainActivity.kt — Main nav host with role-based bottom navigation, biometric lock, FCM token registration, and notification badge

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/main/MainActivity.kt`

---

## 🎯 What This File Does
`MainActivity` is the primary authenticated shell of the app — the screen users see after login. It hosts the Navigation Component's nav host fragment (`main_nav_host`) which contains all post-login screens (home, classes, assignments, quiz, attendance, chat, AI, notifications, profile, admin). On `onCreate` it reads the current user's role from Firestore and dynamically inflates the correct bottom navigation menu (student/teacher/admin). It registers a real-time Firestore listener to show an unread-notification badge on the bell icon. It saves the FCM push token to Firestore so other users can send this user notifications. It implements a biometric lock: when the app has been backgrounded for ≥3 seconds and the user returns, it shows a biometric prompt and blocks all interaction behind a full-screen overlay until authentication succeeds. Without this Activity, the entire post-login experience would not exist.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.Manifest` | Android SDK | Permission name constants | `Manifest.permission.POST_NOTIFICATIONS` |
| `android.content.Context` | Android SDK | App context | Reading SharedPreferences |
| `android.content.pm.PackageManager` | Android SDK | Package/permission utilities | `PERMISSION_GRANTED` constant |
| `android.os.Build` | Android SDK | SDK version constants | Guards notification permission request behind API 33+ |
| `android.os.Bundle` | Android SDK | Key-value map for Activity state | `onCreate` parameter |
| `android.view.View` | Android SDK | Base class for all UI views | Showing/hiding the biometric overlay |
| `android.view.animation.OvershootInterpolator` | Android SDK | Animation easing curve | Bottom nav entrance + tab bounce |
| `androidx.appcompat.app.AppCompatActivity` | AndroidX AppCompat | Base Activity with Material/fragment support | MainActivity extends it |
| `androidx.core.app.ActivityCompat` | AndroidX Core | Permission request helper | `requestPermissions()` |
| `androidx.core.content.ContextCompat` | AndroidX Core | Context utilities | `checkSelfPermission()` |
| `androidx.interpolator.view.animation.FastOutSlowInInterpolator` | AndroidX Interpolator | Smooth deceleration curve | Bottom nav hide animation |
| `androidx.navigation.NavController` | Navigation Component | Controls fragment navigation | Navigate, popBackStack |
| `androidx.navigation.fragment.NavHostFragment` | Navigation Component | Fragment that hosts nav graph | Finding the nav host |
| `androidx.navigation.ui.setupWithNavController` | Navigation Component | Wires BottomNavigationView to NavController | Automatic tab ↔ destination sync |
| `com.google.firebase.messaging.FirebaseMessaging` | Firebase Messaging | FCM token access | Getting current device token |
| `com.google.firebase.firestore.FirebaseFirestore` | Firebase Firestore | Real-time database | Unread notification listener |
| `com.syed.classconnect.R` | Project | Resource IDs | Menu, nav graph, view IDs |
| `com.syed.classconnect.databinding.ActivityMainBinding` | ViewBinding | Type-safe view references | `binding.bottomNav`, `binding.biometricOverlay` |
| `com.syed.classconnect.util.BiometricHelper` | Project | Biometric authentication helper | `canAuthenticate()`, `authenticate()` |
| `com.syed.classconnect.util.Constants` | Project | App constants | `PREF_BIOMETRIC_ENABLED`, role constants |
| `dagger.hilt.android.AndroidEntryPoint` | Hilt | Enables DI in this Activity | Required for `@Inject` fields |
| `com.google.firebase.auth.FirebaseAuth` | Firebase Auth | Current authenticated user | `auth.currentUser?.uid` |
| `com.syed.classconnect.data.repository.AuthRepository` | Project | User data access | `getUserById()`, `updateFcmToken()` |
| `kotlinx.coroutines.CoroutineScope` | Coroutines | Creates a coroutine scope | Ad-hoc scope for FCM + nav setup |
| `kotlinx.coroutines.Dispatchers` | Coroutines | Thread pool selectors | `Dispatchers.IO`, `Dispatchers.Main` |
| `kotlinx.coroutines.launch` | Coroutines | Start a coroutine | Fire-and-forget coroutine blocks |
| `javax.inject.Inject` | Javax / Hilt | Field injection marker | `auth`, `authRepository` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `@AndroidEntryPoint`
Required on every Activity/Fragment that uses Hilt. Without it, `@Inject` fields in this Activity will not be populated and will be `null` at runtime.

### `private lateinit var binding: ActivityMainBinding`
`lateinit` = skip initialization now; it will be set in `onCreate` before first use. ViewBinding: generated class with typed references to every view in `activity_main.xml`.

### `private lateinit var navController: NavController`
The Navigation Component controller. Manages the back stack and current destination for `main_nav_host`.

### `@Inject lateinit var auth: FirebaseAuth`
Hilt injects the app-scoped `FirebaseAuth` instance. `lateinit` because field injection happens after construction.

### `private var wasInBackground = false`
State flag: set to `true` in `onStop()`, back to `false` in `onStart()`. Used to distinguish: "returning from background" vs. "first launch".

### `private var initialLaunch = true`
Prevents the biometric prompt from showing on first startup (it would fire on the very first `onStart()` after `onCreate()`).

### `private val minBackgroundDuration = 3000L`
Only require biometric if the app was backgrounded for ≥3 seconds. Prevents prompting when the user briefly switches apps.

### `OvershootInterpolator(0.8f)`
Animation interpolator that "overshoots" the target and bounces back. Creates a spring-like feel for the bottom nav entrance and tab icon bounce.

### `navController.navInflater.inflate(R.navigation.nav_main)`
Inflates the navigation graph from XML at runtime. Allows us to dynamically set the start destination based on user role before assigning the graph.

### `navGraph.setStartDestination(startDest)`
Sets which Fragment destination is shown first when the nav graph loads. Different roles start at different home screens.

### `binding.bottomNav.getOrCreateBadge(menuItemId)`
Material Design API that creates or retrieves a `BadgeDrawable` on a bottom nav item. Used to show the unread notification count as a red bubble.

### `addSnapshotListener { snapshot, error -> }`
Firestore real-time listener. Fires immediately with current data, then again whenever the query result changes. Used here to keep the notification badge count live.

### `CoroutineScope(Dispatchers.IO).launch { }`
Ad-hoc coroutine scope (not tied to lifecycle). Used here for FCM token save — a one-shot operation at launch. Note: for long-running work, prefer `lifecycleScope` which is automatically cancelled.

### `finishAffinity()`
Closes this Activity AND all activities below it in the task stack. Used when biometric is cancelled — effectively exits the app.

### `isChangingConfigurations`
`true` when the Activity is being destroyed/recreated due to a configuration change (e.g., screen rotation). In `onStop()`, we skip setting `wasInBackground = true` during rotation — otherwise the biometric prompt would appear after every rotation.

---

## 🏗️ Class Structure
`MainActivity : AppCompatActivity()` — a single Activity that hosts the entire post-login navigation graph. Uses ViewBinding (`ActivityMainBinding`).

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `binding` | `ActivityMainBinding` | `private lateinit var` | ViewBinding for `activity_main.xml` | Type-safe view access |
| `navController` | `NavController` | `private lateinit var` | Navigation controller for `main_nav_host` | Navigate, observe destinations |
| `auth` | `FirebaseAuth` | `@Inject lateinit var` | Injected Firebase Auth | Get current user UID |
| `authRepository` | `AuthRepository` | `@Inject lateinit var` | Injected user repository | Load user role, save FCM token |
| `wasInBackground` | `Boolean` | `private var` | Whether app returned from background | Biometric lock trigger |
| `initialLaunch` | `Boolean` | `private var` | Is this the very first onStart? | Prevent biometric on startup |
| `isLocked` | `Boolean` | `private var` | Is the biometric overlay showing? | Block interaction during auth |
| `backgroundTimestamp` | `Long` | `private var` | `System.currentTimeMillis()` at onStop | Calculate background duration |
| `minBackgroundDuration` | `Long` | `private val` | 3000ms | Threshold before biometric required |

---

## ⚙️ Functions

### `onCreate(savedInstanceState: Bundle?)`
**Purpose:** Full Activity setup — ViewBinding, nav host, bottom nav, deep links, FCM, notifications, badge.
**Called when:** Activity is first created.
**Step by step:**
1. Inflates `activity_main.xml` via ViewBinding, sets as content view.
2. Finds `main_nav_host` fragment, gets its NavController.
3. Calls `setupBottomNavigation()` — async: loads user role then wires bottom nav.
4. Calls `handleDeepLink()` — handles any URI intents (e.g., from widgets).
5. Calls `saveFcmToken()` — saves device push token to Firestore.
6. Calls `requestNotificationPermission()` — requests POST_NOTIFICATIONS on API 33+.
7. Calls `observeUnreadNotifications()` — attaches real-time badge listener.

### `observeUnreadNotifications()`
**Purpose:** Shows a live count badge on the notifications tab.
**Step by step:**
1. Gets current user UID; returns if not logged in.
2. Attaches a Firestore snapshot listener to `notifications/{uid}/items` where `isRead == false`.
3. On every update, calls `updateNotificationBadge(count)`.

### `updateNotificationBadge(count: Int)`
**Purpose:** Shows/hides the red notification count badge on the bell icon.
**Step by step:**
1. Gets or creates `BadgeDrawable` for `R.id.notificationsFragment`.
2. If count > 0: shows badge with count, max 3 digits (99+).
3. If count == 0: hides badge.
4. Wrapped in try/catch — badge setup can fail if the menu hasn't inflated yet.

### `saveFcmToken()`
**Purpose:** Saves the device's FCM push token to Firestore so other users (or Cloud Functions) can send push notifications to this device.
**Step by step:**
1. Gets FCM token via `FirebaseMessaging.getInstance().token`.
2. On success: gets current user UID; if null, returns early.
3. Launches an IO coroutine to call `authRepository.updateFcmToken(uid, token)`.

### `requestNotificationPermission()`
**Purpose:** Requests `POST_NOTIFICATIONS` permission on Android 13+.
**Step by step:**
1. Guards behind `Build.VERSION.SDK_INT >= TIRAMISU` (API 33).
2. Checks if permission already granted via `ContextCompat.checkSelfPermission`.
3. If not granted, calls `ActivityCompat.requestPermissions` with request code 1001.

### `onStart()`
**Purpose:** Checks if biometric lock should engage when user returns to the app.
**Step by step:**
1. If `wasInBackground` is true AND this is not the initial launch:
2. Calculates how long the app was backgrounded.
3. If ≥ `minBackgroundDuration` (3s): calls `checkBiometricLock()`.
4. Resets `wasInBackground = false` and `initialLaunch = false`.

### `onStop()`
**Purpose:** Records that the app went to background.
**Step by step:**
1. Guards: skips during screen rotation (`isChangingConfigurations`).
2. Sets `wasInBackground = true`.
3. Records `backgroundTimestamp = System.currentTimeMillis()`.

### `checkBiometricLock()`
**Purpose:** Shows biometric prompt if the user has it enabled and hardware is available.
**Step by step:**
1. Reads `PREF_BIOMETRIC_ENABLED` from SharedPreferences. Returns if disabled.
2. Calls `BiometricHelper.canAuthenticate()`. Returns if not AVAILABLE.
3. Sets `isLocked = true`, shows `biometricOverlay` (a full-screen blocking view).
4. Calls `BiometricHelper.authenticate()`:
   - `onSuccess`: hides overlay, sets `isLocked = false`.
   - `onError` (cancel/other): calls `finishAffinity()` — exits the app.
   - `onFailed` (wrong biometric): prompt stays open, user can retry.

### `setupBottomNavigation()`
**Purpose:** Loads user role from Firestore, inflates the correct menu, connects to NavController, adds animations.
**Called when:** `onCreate()`.
**Step by step:**
1. Launches a Main-thread coroutine.
2. Gets UID; loads User from Firestore via `authRepository.getUserById(uid)`.
3. Selects menu resource (`R.menu.bottom_nav_teacher`, `admin`, or `student`) and start destination fragment based on role.
4. Inflates the nav graph, sets its start destination, assigns to navController.
5. Clears and re-inflates the bottom nav menu.
6. Calls `setupWithNavController` — handles tab selection ↔ navigation automatically.
7. Entrance animation: slides the bottom nav container up from below with OvershootInterpolator.
8. `setOnItemSelectedListener`: bounces the icon (scale to 1.3x then back) and navigates.
9. `addOnDestinationChangedListener`: hides the bottom nav bar on `onboardingFragment`.

### `handleDeepLink()`
**Purpose:** Processes URI intents for direct navigation (e.g., from home screen widgets).
**Step by step:**
1. Checks `intent?.data` for a URI.
2. Routes by `uri.host`: "ai_buddy" → AI fragment, "my_classes" → class list, "attendance" → attendance.

---

## 🔄 Data Flow Diagram
```
User returns to app after 3+ seconds in background
        ↓
onStart() detects wasInBackground=true
        ↓
checkBiometricLock() reads SharedPreferences
        ↓
biometricOverlay shown (blocks UI)
        ↓
BiometricHelper.authenticate() shows system prompt
        ↓
    SUCCESS            ERROR/CANCEL
       ↓                    ↓
overlay hidden         finishAffinity()
isLocked=false         (app closes)
```

```
User taps notification tab (badge shows 3)
        ↓
Firestore listener: notifications/{uid}/items where isRead==false
        ↓
count = snapshot.size() = 3
        ↓
updateNotificationBadge(3) → badge.number = 3, badge.isVisible = true
```

---

## 🧩 Dependencies

| Depends On | Why |
|-----------|-----|
| `AuthRepository` | Load user role for nav menu, update FCM token |
| `FirebaseAuth` | Get current user UID |
| `FirebaseFirestore` | Real-time unread notification count |
| `FirebaseMessaging` | Get device FCM token |
| `BiometricHelper` | Biometric hardware check and authentication |
| `Constants` | Preference keys, role constants |
| Navigation Component | Fragment back-stack management |

---

## ⚠️ Important Notes & Gotchas
- `setupBottomNavigation()` runs a coroutine on Main thread. If Firestore is slow, the bottom nav will appear after a short delay — this is expected.
- The `CoroutineScope(Dispatchers.IO)` in `saveFcmToken()` is NOT lifecycle-aware. For this short one-shot operation it's acceptable, but avoid this pattern for long-running work.
- `isChangingConfigurations` guard in `onStop()` prevents the biometric prompt from appearing after screen rotation — a common Android bug.
- The `badge.clearNumber()` when count is 0 is important — without it, the badge stays at the old count even when `isVisible = false`.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
// ═══════════════════════════════════════
// MainActivity.kt
// ═══════════════════════════════════════

package com.syed.classconnect.ui.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.firestore.FirebaseFirestore
import com.syed.classconnect.R
import com.syed.classconnect.databinding.ActivityMainBinding
import com.syed.classconnect.util.BiometricHelper
import com.syed.classconnect.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.data.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
// Enables Hilt DI for this Activity. Required for @Inject fields to work.
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    // ViewBinding — gives us typed references to all views in activity_main.xml.

    private lateinit var navController: NavController
    // Navigation controller for the main_nav_host fragment.

    @Inject lateinit var auth: FirebaseAuth
    // Hilt injects the singleton FirebaseAuth instance.

    @Inject lateinit var authRepository: AuthRepository
    // Hilt injects the singleton AuthRepository.

    private var wasInBackground = false
    // true when app returned from background (onStop was called).

    private var initialLaunch = true
    // true on first onStart() — prevents biometric prompt right at app open.

    private var isLocked = false
    // true while the biometric overlay is visible.

    private var backgroundTimestamp = 0L
    // Millisecond timestamp when onStop() was called.

    private val minBackgroundDuration = 3000L
    // 3 seconds — minimum background time before biometric is required.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        // Inflate activity_main.xml and wrap in a typed binding object.
        setContentView(binding.root)
        // Set the root view as the Activity's content.

        val navHost = supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment
        // Find the NavHostFragment declared in activity_main.xml.
        navController = navHost.navController
        // Extract the NavController that manages the fragment back stack.

        setupBottomNavigation()
        handleDeepLink()
        saveFcmToken()
        requestNotificationPermission()
        observeUnreadNotifications()
    }

    private fun observeUnreadNotifications() {
        val uid = auth.currentUser?.uid ?: return
        // If no user logged in, skip — happens briefly during auth transitions.
        FirebaseFirestore.getInstance()
            .collection("notifications")
            .document(uid)
            .collection("items")
            .whereEqualTo("isRead", false)
            // Real-time query: all unread notification documents for this user.
            .addSnapshotListener { snap, _ ->
                val count = snap?.size() ?: 0
                updateNotificationBadge(count)
                // Called immediately with current data, then on every change.
            }
    }

    private fun updateNotificationBadge(count: Int) {
        try {
            val badge = binding.bottomNav.getOrCreateBadge(R.id.notificationsFragment)
            // Get existing or create new BadgeDrawable on the notifications tab.
            if (count > 0) {
                badge.isVisible = true
                badge.number = count
                badge.maxCharacterCount = 3  // Shows "99+" for counts ≥ 100
            } else {
                badge.isVisible = false
                badge.clearNumber()
                // clearNumber() is important — just setting isVisible=false leaves stale count.
            }
        } catch (_: Exception) {
            // Badge setup may fail if the BottomNavigationView menu hasn't inflated yet.
            // Safe to swallow — it will succeed on the next snapshot update.
        }
    }

    private fun saveFcmToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            val uid = auth.currentUser?.uid ?: return@addOnSuccessListener
            CoroutineScope(Dispatchers.IO).launch {
                authRepository.updateFcmToken(uid, token)
                // Saves the token to users/{uid}/fcmToken in Firestore.
                // Other users/Cloud Functions use this token to send push notifications.
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // POST_NOTIFICATIONS is a runtime permission on Android 13+ (API 33).
            // On older versions, it's granted automatically.
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001  // Request code — identifies this permission request in onRequestPermissionsResult.
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (wasInBackground && !initialLaunch) {
            val timeInBackground = System.currentTimeMillis() - backgroundTimestamp
            if (timeInBackground >= minBackgroundDuration) {
                checkBiometricLock()
                // Only prompt if backgrounded for ≥3 seconds.
            }
        }
        wasInBackground = false
        initialLaunch = false
    }

    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) {
            // isChangingConfigurations is true during screen rotation.
            // We don't want to lock the screen just because the user rotated the device.
            wasInBackground = true
            backgroundTimestamp = System.currentTimeMillis()
        }
    }

    private fun checkBiometricLock() {
        val prefs = getSharedPreferences("classconnect_prefs", Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean(Constants.PREF_BIOMETRIC_ENABLED, false)
        if (!enabled) return
        // User hasn't enabled biometric lock in Settings — skip.

        val status = BiometricHelper.canAuthenticate(this)
        if (status != BiometricHelper.BiometricStatus.AVAILABLE) return
        // Hardware not present or no biometrics enrolled — skip.

        isLocked = true
        binding.biometricOverlay.visibility = View.VISIBLE
        // Show a full-screen overlay that blocks all UI interaction.

        BiometricHelper.authenticate(
            activity = this,
            onSuccess = {
                isLocked = false
                binding.biometricOverlay.visibility = View.GONE
                // Authentication passed — hide overlay, resume normal UI.
            },
            onError = { msg ->
                // User pressed Cancel or Negative button.
                finishAffinity()
                // Close the app — user explicitly refused to authenticate.
            },
            onFailed = {
                // Wrong fingerprint/face — the system prompt handles retry.
                // We do nothing here; the prompt remains open.
            }
        )
    }

    private fun setupBottomNavigation() {
        CoroutineScope(Dispatchers.Main).launch {
            val uid = auth.currentUser?.uid ?: return@launch
            val user = authRepository.getUserById(uid) ?: return@launch
            // Load the User model to read their role.

            val menuRes = when (user.role) {
                Constants.ROLE_TEACHER -> R.menu.bottom_nav_teacher
                Constants.ROLE_ADMIN -> R.menu.bottom_nav_admin
                else -> R.menu.bottom_nav_student
                // Different bottom nav menus for different roles.
            }
            val startDest = when (user.role) {
                Constants.ROLE_TEACHER -> R.id.teacherHomeFragment
                Constants.ROLE_ADMIN -> R.id.adminDashboardFragment
                else -> R.id.studentHomeFragment
                // Different start destination per role.
            }

            val navGraph = navController.navInflater.inflate(R.navigation.nav_main)
            navGraph.setStartDestination(startDest)
            // Set the start destination before assigning the graph —
            // this determines which fragment is shown on first load.
            navController.graph = navGraph

            binding.bottomNav.menu.clear()
            binding.bottomNav.inflateMenu(menuRes)
            // Replace the bottom nav menu items with the role-specific set.
            binding.bottomNav.setupWithNavController(navController)
            // Navigation Component handles: tab click → navigate to destination.

            // ── Entrance animation ─────────────────────────────────────────
            binding.bottomNavContainer.translationY = 200f
            binding.bottomNavContainer.alpha = 0f
            // Start below screen and invisible.
            binding.bottomNavContainer.animate()
                .translationY(0f).alpha(1f)
                .setDuration(500)
                .setStartDelay(300)
                .setInterpolator(OvershootInterpolator(0.8f))
                // Slide up with slight overshoot spring effect.
                .start()

            // ── Icon bounce on tab selection ──────────────────────────────
            binding.bottomNav.setOnItemSelectedListener { item ->
                val iconView = binding.bottomNav.findViewById<View>(item.itemId)
                iconView?.animate()
                    ?.scaleX(1.3f)?.scaleY(1.3f)?.setDuration(100)
                    ?.withEndAction {
                        iconView.animate().scaleX(1f).scaleY(1f).setDuration(180)
                            .setInterpolator(OvershootInterpolator(3f)).start()
                        // Scale up then bounce back to original size.
                    }?.start()
                val handled = navController.popBackStack(item.itemId, false)
                if (!handled) navController.navigate(item.itemId)
                // Try to pop to an existing instance of this destination first.
                // If it's not on the back stack, navigate to it fresh.
                true
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hideNav = destination.id in listOf(R.id.onboardingFragment)
            // Hide the bottom nav bar on the onboarding screen.
            if (hideNav && binding.bottomNavContainer.visibility == View.VISIBLE) {
                binding.bottomNavContainer.animate()
                    .translationY(binding.bottomNavContainer.height.toFloat() + 40f)
                    .alpha(0f)
                    .setDuration(280)
                    .setInterpolator(FastOutSlowInInterpolator())
                    .withEndAction { binding.bottomNavContainer.visibility = View.GONE }
                    .start()
            } else if (!hideNav && binding.bottomNavContainer.visibility == View.GONE) {
                binding.bottomNavContainer.visibility = View.VISIBLE
                binding.bottomNavContainer.animate()
                    .translationY(0f).alpha(1f)
                    .setDuration(400)
                    .setInterpolator(OvershootInterpolator(0.8f))
                    .start()
            }
        }
    }

    private fun handleDeepLink() {
        intent?.data?.let { uri ->
            when (uri.host) {
                "ai_buddy"   -> navController.navigate(R.id.aiBuddyFragment)
                "my_classes" -> navController.navigate(R.id.classListFragment)
                "attendance" -> { /* TODO: navigate to attendance */ }
            }
        }
    }
}
```

