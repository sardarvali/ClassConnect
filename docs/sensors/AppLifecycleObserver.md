# AppLifecycleObserver.kt — Singleton lifecycle observer that tracks app background duration for biometric lock

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/sensor/AppLifecycleObserver.kt`

---

## 🎯 What This File Does
`AppLifecycleObserver` is a `@Singleton` `DefaultLifecycleObserver` registered with `ProcessLifecycleOwner` in `ClassConnectApp.onCreate()`. It tracks when the app transitions from foreground to background (`onStop`) and back (`onStart`). If the app was in the background for ≥3 seconds, it sets `shouldShowBiometric = true`. `MainActivity` checks this flag (and also does its own direct time check) to decide whether to prompt for biometric authentication when the user returns to the app. Without this observer, the biometric lock feature would have no background-duration tracking.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `androidx.lifecycle.DefaultLifecycleObserver` | AndroidX Lifecycle | Default (no-op) implementations of lifecycle methods | Override only `onStart` and `onStop` |
| `androidx.lifecycle.LifecycleOwner` | AndroidX Lifecycle | The entity whose lifecycle is being observed | `ProcessLifecycleOwner` passes itself |
| `javax.inject.Inject` | Javax / Hilt | Constructor injection | Hilt creates this singleton |
| `javax.inject.Singleton` | Javax / Hilt | One instance per app | `@Singleton` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `DefaultLifecycleObserver`
Interface with empty default implementations for all lifecycle events. We override only `onStart` and `onStop`. Registered with `ProcessLifecycleOwner` in `ClassConnectApp`.

### `var shouldShowBiometric = false` with `private set`
Public read / private write. `MainActivity` reads this flag; only this class sets it.

### `resetBiometricFlag()`
Called by `MainActivity` after successful biometric auth to clear the flag.

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `backgroundedAt` | `Long` | `private var` | Timestamp at `onStop` | Background duration calculation |
| `isInBackground` | `Boolean` | `private var` | Currently backgrounded? | Guard against first-launch trigger |
| `shouldShowBiometric` | `Boolean` | `var` with `private set` | Trigger for biometric prompt | Read by MainActivity |

---

## ⚙️ Functions

### `onStop(owner: LifecycleOwner)`
Sets `isInBackground = true`, records current time.

### `onStart(owner: LifecycleOwner)`
If returning from background and duration ≥ 3000ms → `shouldShowBiometric = true`.

### `resetBiometricFlag()`
Clears `shouldShowBiometric` after successful authentication.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.sensor

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLifecycleObserver @Inject constructor() : DefaultLifecycleObserver {

    private var backgroundedAt = 0L
    private var isInBackground = false

    var shouldShowBiometric = false
        private set

    override fun onStop(owner: LifecycleOwner) {
        isInBackground = true
        backgroundedAt = System.currentTimeMillis()
    }

    override fun onStart(owner: LifecycleOwner) {
        if (isInBackground) {
            val duration = System.currentTimeMillis() - backgroundedAt
            if (duration >= 3_000L) {
                shouldShowBiometric = true
            }
            isInBackground = false
        }
    }

    fun resetBiometricFlag() {
        shouldShowBiometric = false
    }
}
```

