# PermissionsActivity.kt — One-time permission request screen for camera, Bluetooth, location, and notifications

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/permissions/PermissionsActivity.kt`

---

## 🎯 What This File Does
`PermissionsActivity` presents a one-time permissions setup screen shown to users after first login (before reaching `MainActivity`). It displays four permission cards: Push Notifications, Camera (for QR scanning), Bluetooth (for BLE attendance), and Location (for BLE scanning). Each card shows a live status ("Granted" in green / "Not granted" in amber) and tapping a card requests that permission. A "Continue" button is always available — permissions are optional (the app works without them but with reduced functionality). `alreadyRequested()` is a companion method checked by `SplashActivity` to avoid showing this screen again. Without this Activity, users are never prompted for necessary permissions.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.Manifest` | Android SDK | Permission name constants | All permission string names |
| `android.content.Context` | Android SDK | App context | SharedPreferences |
| `android.content.Intent` | Android SDK | Activity navigation | Go to `MainActivity` |
| `android.content.pm.PackageManager` | Android SDK | Permission check | `PERMISSION_GRANTED` constant |
| `android.os.Build` | Android SDK | API level check | Guards Android 12+ Bluetooth permissions |
| `android.os.Bundle` | Android SDK | State map | `onCreate` lifecycle |
| `android.widget.TextView` | Android SDK | Text view | `updateStatus()` parameter type |
| `androidx.activity.result.contract.ActivityResultContracts` | AndroidX | Permission launcher contracts | `RequestPermission`, `RequestMultiplePermissions` |
| `androidx.appcompat.app.AppCompatActivity` | AndroidX | Activity base | PermissionsActivity extends it |
| `androidx.core.content.ContextCompat` | AndroidX | Color + permission utilities | `checkSelfPermission()`, `getColor()` |
| `com.syed.classconnect.R` | Project | Resource IDs | String + color resources |
| `com.syed.classconnect.databinding.ActivityPermissionsBinding` | ViewBinding | `activity_permissions.xml` | All card views and status TextViews |
| `com.syed.classconnect.ui.main.MainActivity` | Project | Main app screen | Navigation target |
| `com.syed.classconnect.util.Constants` | Project | `PREF_PERMISSIONS_REQUESTED` key | SharedPreferences flag |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `registerForActivityResult(ActivityResultContracts.RequestPermission())`
Modern permission API. `registerForActivityResult` must be called during `onCreate` (before `onStart`). Returns a launcher that can be called with a permission string. The lambda receives `Boolean` (granted/denied).

### `registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())`
For requesting multiple permissions at once (Bluetooth on Android 12+ requires 3 permissions: `BLUETOOTH_SCAN`, `BLUETOOTH_ADVERTISE`, `BLUETOOTH_CONNECT`). The lambda receives `Map<String, Boolean>`.

### `bluetoothPermissions(): Array<String>`
Returns different arrays based on Android version:
- Android 12+ (API 31+): new runtime Bluetooth permissions (`BLUETOOTH_SCAN`, `BLUETOOTH_ADVERTISE`, `BLUETOOTH_CONNECT`)
- Android 11 and below: old permissions (`BLUETOOTH`, `BLUETOOTH_ADMIN`)

### `updateStatus(tv: TextView, granted: Boolean)`
Updates a status TextView: green "Granted" or amber "Not granted". Called both on initial setup and after permission result callbacks.

### `refreshAllStatuses()`
Called in both `onCreate` and `onResume`. In `onResume`, it catches the case where the user went to system Settings to manually grant a permission — the status updates automatically on return.

### `markPermissionsRequested()`
Saves `PREF_PERMISSIONS_REQUESTED = true`. `SplashActivity` reads this to skip showing `PermissionsActivity` on subsequent launches.

### `companion object { fun alreadyRequested(context: Context): Boolean }`
Static helper method for `SplashActivity` to check whether the permission screen has been shown. Reads from SharedPreferences.

---

## 🏗️ Class Structure
`class PermissionsActivity : AppCompatActivity()` — no Hilt (no DI needed).

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `binding` | `ActivityPermissionsBinding` | `private lateinit var` | ViewBinding | Type-safe view access |
| `notificationLauncher` | `ActivityResultLauncher<String>` | `private val` | Notification permission launcher | Requests POST_NOTIFICATIONS |
| `cameraLauncher` | `ActivityResultLauncher<String>` | `private val` | Camera permission launcher | Requests CAMERA |
| `bluetoothLauncher` | `ActivityResultLauncher<Array<String>>` | `private val` | Multi-permission launcher | Requests all BT permissions |
| `locationLauncher` | `ActivityResultLauncher<String>` | `private val` | Location permission launcher | Requests ACCESS_FINE_LOCATION |

---

## ⚙️ Functions

### `onCreate(savedInstanceState: Bundle?)`
Inflates binding, calls `refreshAllStatuses()`, sets up card click listeners, "Continue" button listener.

### `onResume()`
Calls `refreshAllStatuses()` — catches manual permission grants from device Settings.

### `refreshAllStatuses()`
Checks each permission with `isGranted()` and calls `updateStatus()` for each card.

### `updateStatus(tv, granted)`
Sets text to "Granted" (green) or "Not granted" (amber).

### `isGranted(permission: String): Boolean`
`ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED`.

### `bluetoothPermissions(): Array<String>`
Returns API-version-appropriate Bluetooth permission array.

### `markPermissionsRequested()`
Saves the "seen" flag to SharedPreferences.

### `goToMain()`
Starts `MainActivity`, finishes `PermissionsActivity`.

### `alreadyRequested(context: Context): Boolean` *(companion)*
Returns `true` if the permissions screen was already shown.

---

## 🔄 Data Flow Diagram
```
SplashActivity: user logged in, onboarding complete
        ↓
PermissionsActivity.alreadyRequested(context)?
        ├── YES → start MainActivity directly
        └── NO  → start PermissionsActivity
                        ↓
                  User reviews permissions
                        ↓
                  "Continue" → markPermissionsRequested() → goToMain()
```

---

## ⚠️ Important Notes & Gotchas
- Permissions are OPTIONAL. Tapping "Continue" without granting all permissions is allowed. The app handles missing permissions gracefully (e.g., camera unavailable → can't scan QR, but can still check in via other means).
- `refreshAllStatuses()` runs in `onResume()` — if the user grants a permission in system Settings while this Activity is paused, the status updates when they return.
- `registerForActivityResult` must be called in the constructor or `onCreate` — it cannot be called after `onStart`.

