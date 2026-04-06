# ClassConnectApp.kt — Application entry point that initializes Hilt, Timber, Crashlytics, and notification channels

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ClassConnectApp.kt`

---

## 🎯 What This File Does
`ClassConnectApp` is the very first class Android instantiates when the app process starts — before any Activity, Fragment, or Service. It extends `Application`, meaning one singleton instance exists for the entire app lifetime. Here it sets up Hilt dependency injection (via `@HiltAndroidApp`), configures the Timber logging library, toggles Firebase Crashlytics collection based on build type, registers `AppLifecycleObserver` with the process lifecycle, and creates all FCM notification channels required on Android 8+. If this class were removed or the `@HiltAndroidApp` annotation dropped, the entire DI graph would break and every `@Inject` site in the app would crash.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.app.Application` | Android SDK | Base class for global application state | ClassConnectApp extends it |
| `android.app.NotificationChannel` | Android SDK | Describes a category of notifications (Android 8+) | Creating the 5 app channels |
| `android.app.NotificationManager` | Android SDK | System service that manages notifications | Registers the channels with the OS |
| `android.os.Build` | Android SDK | Device/OS version constants | Guards channel creation behind API 26+ |
| `androidx.lifecycle.ProcessLifecycleOwner` | AndroidX Lifecycle | Singleton LifecycleOwner for the whole app process | Attaches AppLifecycleObserver |
| `com.google.firebase.crashlytics.FirebaseCrashlytics` | Firebase Crashlytics | Crash reporting SDK | Disabled in debug, enabled in release |
| `com.syed.classconnect.sensor.AppLifecycleObserver` | Project | Tracks app foreground/background transitions | Registered as a lifecycle observer |
| `com.syed.classconnect.util.Constants` | Project | App-wide string constants | Channel ID constants |
| `dagger.hilt.android.HiltAndroidApp` | Hilt | Annotation that generates the root Hilt component | Required for all DI in the app |
| `timber.log.Timber` | Timber | Logging library with auto-tagging | Planted with DebugTree in debug builds |
| `javax.inject.Inject` | Javax / Hilt | Marks fields/constructors for injection | Field injection of AppLifecycleObserver |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `@HiltAndroidApp`
Placed on the `Application` subclass. Triggers Hilt's annotation processor to generate `ClassConnectApp_GeneratedInjector` — the root DI component that holds all `@Singleton` bindings for the entire app lifetime. Without this annotation, Hilt cannot inject anything anywhere in the app.

```kotlin
@HiltAndroidApp
class ClassConnectApp : Application()
// Hilt generates the component at compile time. No runtime reflection.
```

### `@Inject lateinit var appLifecycleObserver: AppLifecycleObserver`
Field injection: Hilt creates the `AppLifecycleObserver` (which is `@Singleton`) and injects it into this field after the Application is created. `lateinit` is needed because the field cannot be initialized in the constructor (Hilt injects after construction).

### `override fun onCreate()`
Called once when the Application object is created — before any Activity. This is the correct place for one-time global initialization.

### `if (BuildConfig.DEBUG)`
`BuildConfig` is a generated class that contains boolean flags reflecting the current build variant. `DEBUG` is `true` for debug builds, `false` for release. Used here to:
- In debug: plant Timber's `DebugTree` (logs to Logcat with class name as tag) and disable Crashlytics (so test crashes don't pollute the dashboard).
- In release: enable Crashlytics collection.

### `Timber.plant(Timber.DebugTree())`
`Timber` is a logging wrapper. `plant()` installs a `Tree` (log handler). `DebugTree` automatically uses the calling class name as the log tag. In release builds, we don't call `plant()`, so all `Timber.d(...)` calls become no-ops — zero logging overhead in production.

### `ProcessLifecycleOwner.get().lifecycle.addObserver(...)`
`ProcessLifecycleOwner` is a singleton that represents the entire app process lifecycle. Its `ON_START` event fires when any activity comes to the foreground; `ON_STOP` fires when ALL activities are stopped (app is truly in background). This is different from a single Activity's lifecycle which fires on every navigation. Used to detect the app going to background for the biometric lock feature.

### `createNotificationChannels()`
On Android 8.0 (API 26)+, notifications must be posted to a named "channel". Channels must be created before any notification can appear. This is done in `Application.onCreate()` so the channels exist before any notification-posting code runs. Creating a channel that already exists is a no-op — safe to call every startup.

### `.apply { }` — Scope Function
Configures the `NotificationChannel` object inline. `this` inside `apply` refers to the channel being configured.
```kotlin
NotificationChannel(id, name, importance).apply {
    description = "..."   // this.description = ...
}
```

### `Build.VERSION.SDK_INT >= Build.VERSION_CODES.O`
`Build.VERSION_CODES.O` = API 26 (Android 8.0). Notification channels only exist from API 26. Below that, `NotificationChannel` doesn't exist at all — hence the guard.

### `channels.forEach { nm.createNotificationChannel(it) }`
Iterates the list of 5 `NotificationChannel` objects and registers each with the system `NotificationManager`.

---

## 🏗️ Class Structure
`ClassConnectApp` extends `Application()`. It is NOT a Fragment or Activity. Hilt treats it as the root component source due to `@HiltAndroidApp`.

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `appLifecycleObserver` | `AppLifecycleObserver` | `@Inject lateinit var` | The singleton observer for app background/foreground | Registered with ProcessLifecycleOwner |

---

## ⚙️ Functions

### `onCreate()`
**Purpose:** One-time app initialization — logging, crash reporting, lifecycle observer, notification channels.
**Called when:** The OS creates the Application process, before any Activity.
**Step by step:**
1. Calls `super.onCreate()` — required.
2. In debug build: plants Timber's DebugTree so logs appear in Logcat. Disables Crashlytics collection.
3. In release build: enables Crashlytics so real crashes are reported to the Firebase console.
4. Registers `appLifecycleObserver` with `ProcessLifecycleOwner` so it receives `onStart`/`onStop` events for the whole process.
5. Calls `createNotificationChannels()`.

### `createNotificationChannels()`
**Purpose:** Registers the 5 app notification channels with the Android OS (API 26+ only).
**Called when:** `onCreate()` runs.
**Step by step:**
1. Guards with `Build.VERSION.SDK_INT >= Build.VERSION_CODES.O` — channels don't exist before API 26.
2. Gets `NotificationManager` via `getSystemService(NotificationManager::class.java)`.
3. Creates a list of 5 `NotificationChannel` objects, each with an ID from `Constants`, a user-visible name from `strings.xml`, and an importance level.
4. Each channel has its description set via `apply`.
5. `channels.forEach { nm.createNotificationChannel(it) }` registers them all. Already-existing channels are unaffected.

**Channel IDs and Importance Levels:**

| Channel | Constant | Importance | Purpose |
|---------|----------|-----------|---------|
| Assignments | `CHANNEL_ASSIGNMENTS` | HIGH | Assignment deadlines, new assignments |
| Chat | `CHANNEL_CHAT` | DEFAULT | New chat messages |
| Announcements | `CHANNEL_ANNOUNCEMENTS` | HIGH | Teacher class announcements |
| Attendance | `CHANNEL_ATTENDANCE` | HIGH | Attendance session started |
| Grades | `CHANNEL_GRADES` | DEFAULT | Grade released notification |

---

## 🔄 Data Flow Diagram
```
App process starts
        ↓
Android creates ClassConnectApp instance
        ↓
Hilt generates DI component (@HiltAndroidApp)
        ↓
onCreate() runs
        ↓
Timber planted (debug) / Crashlytics configured
        ↓
AppLifecycleObserver registered with ProcessLifecycleOwner
        ↓
5 notification channels created/confirmed in OS
        ↓
App ready — Android starts the launcher Activity
```

---

## 🧩 Dependencies

| Depends On | Why |
|-----------|-----|
| `AppLifecycleObserver` | Field-injected; registered for process lifecycle events |
| `Constants` | Channel ID string constants |
| `FirebaseCrashlytics` | Toggled based on build type |
| `Timber` | Logging setup |

---

## ⚠️ Important Notes & Gotchas
- `@HiltAndroidApp` MUST be on the `Application` class. If removed, every `@Inject` in the app fails at runtime.
- `@Inject lateinit var` field injection in `Application` works because Hilt processes it via the generated component. If you use constructor injection here it will NOT work — Hilt does not control Application construction.
- `createNotificationChannels()` is called every launch. This is intentional and correct — the OS ignores duplicate channel registrations.
- `ProcessLifecycleOwner` tracks the WHOLE process. Don't confuse it with `ViewLifecycleOwner` (per Fragment) or `lifecycleOwner` (per Activity).

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
// ═══════════════════════════════════════
// ClassConnectApp.kt
// ═══════════════════════════════════════

package com.syed.classconnect
// Root package of the app.

import android.app.Application
// Base class for the Application object — must be subclassed.

import android.app.NotificationChannel
// Represents one notification category (e.g. "Assignments"). Requires API 26+.

import android.app.NotificationManager
// System service that manages all notifications and channels.

import android.os.Build
// Contains SDK_INT — the device's Android API level.

import androidx.lifecycle.ProcessLifecycleOwner
// Singleton LifecycleOwner for the whole process — fires onStart/onStop when
// the app enters/leaves the foreground across ALL activities.

import com.google.firebase.crashlytics.FirebaseCrashlytics
// Firebase crash reporting SDK.

import com.syed.classconnect.sensor.AppLifecycleObserver
// Custom observer that tracks how long the app has been in the background.

import com.syed.classconnect.util.Constants
// Project constants — notification channel IDs live here.

import dagger.hilt.android.HiltAndroidApp
// Annotation that triggers Hilt code generation for the root DI component.

import timber.log.Timber
// Logging library. Must be "planted" before use.

import javax.inject.Inject
// Marks fields that should be dependency-injected by Hilt.

@HiltAndroidApp
// Tells Hilt: generate the Hilt component for this Application.
// This is REQUIRED for any Hilt injection to work anywhere in the app.
class ClassConnectApp : Application() {
// Extends Android's Application class. Created once per process lifetime.

    @Inject lateinit var appLifecycleObserver: AppLifecycleObserver
    // Hilt injects the @Singleton AppLifecycleObserver into this field.
    // lateinit = will be set by Hilt after construction, before onCreate.

    override fun onCreate() {
        super.onCreate()
        // Always call super first.

        if (BuildConfig.DEBUG) {
            // BuildConfig.DEBUG is true in debug builds, false in release.
            Timber.plant(Timber.DebugTree())
            // DebugTree: logs to Logcat using the calling class name as tag.
            // In release, we skip this — Timber.d() calls become no-ops.

            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
            // Disable crash reporting in debug — prevents test crashes polluting dashboard.
        } else {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
            // Enable in release so real user crashes are captured.
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)
        // ProcessLifecycleOwner.get() returns the singleton process LifecycleOwner.
        // addObserver() registers our AppLifecycleObserver to receive lifecycle events.
        // When ALL activities stop → onStop() is called on the observer.
        // When any activity starts → onStart() is called on the observer.

        createNotificationChannels()
        // Register the 5 notification channels with the OS.
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // NotificationChannel only exists from Android 8.0 (API 26).
            // Below API 26, this block is skipped entirely.

            val nm = getSystemService(NotificationManager::class.java)
            // getSystemService() returns the system NotificationManager.

            val channels = listOf(
                NotificationChannel(Constants.CHANNEL_ASSIGNMENTS,
                    getString(R.string.channel_assignments),
                    NotificationManager.IMPORTANCE_HIGH).apply {
                    // IMPORTANCE_HIGH = shows heads-up notification (pops on screen).
                    description = getString(R.string.channel_assignments_desc)
                    // description shown in system Settings > Notifications.
                },
                NotificationChannel(Constants.CHANNEL_CHAT,
                    getString(R.string.channel_chat),
                    NotificationManager.IMPORTANCE_DEFAULT).apply {
                    // IMPORTANCE_DEFAULT = sound + status bar, no heads-up.
                    description = getString(R.string.channel_chat_desc)
                },
                NotificationChannel(Constants.CHANNEL_ANNOUNCEMENTS,
                    getString(R.string.channel_announcements),
                    NotificationManager.IMPORTANCE_HIGH).apply {
                    description = getString(R.string.channel_announcements_desc)
                },
                NotificationChannel(Constants.CHANNEL_ATTENDANCE,
                    getString(R.string.channel_attendance),
                    NotificationManager.IMPORTANCE_HIGH).apply {
                    description = getString(R.string.channel_attendance_desc)
                },
                NotificationChannel(Constants.CHANNEL_GRADES,
                    getString(R.string.channel_grades),
                    NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = getString(R.string.channel_grades_desc)
                }
            )
            // 5-item list — one channel per notification category.

            channels.forEach { nm.createNotificationChannel(it) }
            // Register each channel. If a channel with this ID already exists,
            // this call is a safe no-op — existing user settings are preserved.
        }
    }
}
```

