# BiometricHelper.kt — Singleton object for biometric hardware detection and authentication

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/util/BiometricHelper.kt`

---

## 🎯 What This File Does
`BiometricHelper` is a Kotlin `object` (singleton) that abstracts all biometric authentication logic used in two places: `MainActivity` (lock on background) and `SettingsFragment` (enable/disable the lock). It provides: `canAuthenticate()` which checks whether biometric hardware is present and enrolled, returning a `BiometricStatus` enum; and `authenticate()` which shows the system biometric prompt and calls the appropriate callback (`onSuccess`, `onError`, or `onFailed`). The `BiometricStatus` enum distinguishes four states so the UI can display the right message for each scenario. Without this helper, biometric logic would be duplicated across multiple screens.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.content.Context` | Android SDK | App context | Required by `BiometricManager.from(context)` |
| `androidx.biometric.BiometricManager` | AndroidX Biometric | Checks hardware and enrollment | `canAuthenticate()` |
| `androidx.biometric.BiometricPrompt` | AndroidX Biometric | Shows fingerprint/face prompt | The actual authentication dialog |
| `androidx.core.content.ContextCompat` | AndroidX Core | `getMainExecutor()` | Provides main thread executor |
| `androidx.fragment.app.FragmentActivity` | AndroidX Fragment | Activity that can host dialogs | Required by `BiometricPrompt` constructor |
| `com.syed.classconnect.R` | Project | String resources | Prompt title, subtitle, negative button text |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `object BiometricHelper`
`object` = singleton. No constructor, no instantiation. Access methods directly: `BiometricHelper.canAuthenticate(ctx)`.

### `BiometricManager.Authenticators.BIOMETRIC_STRONG or BIOMETRIC_WEAK`
`or` is a bitwise OR for combining authenticator types. `BIOMETRIC_STRONG` includes Class 3 biometrics (high-security fingerprint sensors). `BIOMETRIC_WEAK` includes Class 2 biometrics (face recognition). Combined = use either.

### `BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE`
No biometric sensor at all on this device (e.g., low-end phone).

### `BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE`
Hardware exists but is temporarily unavailable (too many failed attempts, locked out).

### `BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED`
Hardware exists but the user hasn't set up any fingerprints/face ID. The Settings app can enroll them.

### `BiometricPrompt` lifecycle
`BiometricPrompt` is tied to a `FragmentActivity`. The prompt is automatically dismissed if the Activity is destroyed. Uses a `CoroutineContext.Executor` (main executor) to ensure callbacks are delivered on the main thread.

### `BiometricPrompt.ERROR_USER_CANCELED`, `ERROR_NEGATIVE_BUTTON`, `ERROR_CANCELED`
Three error codes that all mean "user voluntarily dismissed" (tapped Cancel, tapped the negative button, or the prompt was cancelled programmatically). In these cases, `onError("")` is called with an empty string so the caller can distinguish "user cancelled" from "real error".

### `PromptInfo.Builder`
Builder for configuring the biometric prompt dialog:
- `.setTitle()`: Main heading shown in the dialog
- `.setSubtitle()`: Subtitle text
- `.setNegativeButtonText()`: Cancel button text
- `.setAllowedAuthenticators()`: Which biometric types are accepted

---

## 🏗️ Class Structure
`object BiometricHelper` — not a class, a singleton object. No instances.

### `enum class BiometricStatus`
Four states for `canAuthenticate()`:
- `AVAILABLE`: show enabled switch, allow enabling
- `NO_HARDWARE`: disable switch, explain no sensor
- `UNAVAILABLE`: disable switch, explain hardware unavailable
- `NONE_ENROLLED`: allow tapping switch but show "please enroll" message

---

## ⚙️ Functions

### `canAuthenticate(context: Context): BiometricStatus`
**Purpose:** Check whether biometric authentication is possible on this device.
**Returns:** `BiometricStatus` enum.
**Step by step:**
1. Gets `BiometricManager.from(context)`.
2. Calls `canAuthenticate(BIOMETRIC_STRONG or BIOMETRIC_WEAK)`.
3. Maps the result code to a `BiometricStatus`.

### `authenticate(activity, onSuccess, onError, onFailed)`
**Purpose:** Show the biometric prompt dialog and deliver results via callbacks.
**Parameters:**
- `activity`: `FragmentActivity` — the Activity hosting the prompt
- `onSuccess: () -> Unit` — called when auth succeeds
- `onError: (String) -> Unit` — called with error message, or empty string for user cancel
- `onFailed: () -> Unit` — called for wrong biometric (but prompt stays open)
**Step by step:**
1. Gets `getMainExecutor(activity)` — ensures callbacks run on main thread.
2. Creates `BiometricPrompt.AuthenticationCallback` with three overrides.
3. `onAuthenticationSucceeded`: calls `onSuccess()`.
4. `onAuthenticationError(code, message)`: if code is cancel/negative/cancelled → `onError("")`, otherwise `onError(message)`.
5. `onAuthenticationFailed`: calls `onFailed()`.
6. Creates `BiometricPrompt(activity, executor, callback)`.
7. Builds `PromptInfo` from string resources.
8. Calls `prompt.authenticate(promptInfo)`.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.util

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.syed.classconnect.R

/**
 * Handles BiometricPrompt authentication.
 */
object BiometricHelper {
// object = singleton — no instances, access methods directly.

    fun canAuthenticate(context: Context): BiometricStatus {
        val bm = BiometricManager.from(context)
        return when (bm.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS          -> BiometricStatus.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE  -> BiometricStatus.NO_HARDWARE
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.UNAVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED  -> BiometricStatus.NONE_ENROLLED
            else -> BiometricStatus.UNAVAILABLE
        }
    }

    fun authenticate(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onFailed: () -> Unit = {}
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        // Ensures callbacks are delivered on the Android main (UI) thread.

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                    errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                    errorCode == BiometricPrompt.ERROR_CANCELED) {
                    onError("")   // Empty string = "user voluntarily dismissed"
                } else {
                    onError(errString.toString())   // Real error message
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onFailed()
                // Wrong fingerprint — prompt remains open, user can try again.
            }
        }

        val prompt = BiometricPrompt(activity, executor, callback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(activity.getString(R.string.biometric_auth_title))
            .setSubtitle(activity.getString(R.string.biometric_auth_subtitle))
            .setNegativeButtonText(activity.getString(R.string.biometric_auth_negative))
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK)
            .build()

        prompt.authenticate(promptInfo)
    }

    enum class BiometricStatus {
        AVAILABLE,       // Hardware present and biometrics enrolled
        NO_HARDWARE,     // No biometric sensor
        UNAVAILABLE,     // Hardware exists but not usable right now
        NONE_ENROLLED    // Hardware exists but no biometrics set up
    }
}
```

