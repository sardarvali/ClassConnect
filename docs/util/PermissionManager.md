# PermissionManager.kt — Singleton object for runtime permission checking and rationale/settings dialogs

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/util/PermissionManager.kt`

---

## 🎯 What This File Does
`PermissionManager` is a Kotlin `object` (singleton) that centralises all runtime permission logic. It defines the exact permission arrays needed for each feature (camera, BLE, notifications, storage), adapts them for API level (Android 12+ BLE, Android 13+ notifications and media), provides `isGranted()` / `areAllGranted()` checks, and shows two dialog types: a rationale dialog (before requesting) and a settings dialog (when permanently denied). Used by `PermissionsActivity`, `AttendanceFragment`, and `ChatFragment`. Without it, each screen would duplicate the same permission-checking boilerplate.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.Manifest` | Android SDK | Permission string constants | `Manifest.permission.CAMERA` etc. |
| `android.content.Context` | Android SDK | App context | `ContextCompat.checkSelfPermission` |
| `android.content.Intent` | Android SDK | App navigation | Open Settings intent |
| `android.content.pm.PackageManager` | Android SDK | Permission result constants | `PERMISSION_GRANTED` |
| `android.net.Uri` | Android SDK | URI builder | App settings URI |
| `android.os.Build` | Android SDK | API level check | `Build.VERSION.SDK_INT >= S` |
| `android.provider.Settings` | Android SDK | System settings action | `ACTION_APPLICATION_DETAILS_SETTINGS` |
| `androidx.appcompat.app.AlertDialog` | AndroidX | Confirmation dialogs | Rationale + settings dialogs |
| `androidx.core.content.ContextCompat` | AndroidX | Safe permission check | Works on all API levels |
| `androidx.fragment.app.Fragment` | AndroidX | Fragment reference | `fragment.requireContext()` for dialog |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)` — API-conditional permissions
Bluetooth permissions changed in Android 12 (API 31, `Build.VERSION_CODES.S`):
- **API < 31**: `BLUETOOTH` + `BLUETOOTH_ADMIN` (legacy)
- **API ≥ 31**: `BLUETOOTH_SCAN` + `BLUETOOTH_ADVERTISE` + `BLUETOOTH_CONNECT` (granular)

Notifications changed in Android 13 (API 33, `TIRAMISU`):
- **API < 33**: Notifications are granted automatically — `NOTIFICATION_PERMISSION = null`
- **API ≥ 33**: Must explicitly request `POST_NOTIFICATIONS`

### `ContextCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED`
Safe method to check a single permission. Returns `PERMISSION_GRANTED` (0) or `PERMISSION_DENIED` (-1). `ContextCompat` handles API differences internally.

### `Settings.ACTION_APPLICATION_DETAILS_SETTINGS`
Opens the app's dedicated settings page in the Android Settings app. Constructed with:
```kotlin
Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
    data = Uri.fromParts("package", context.packageName, null)
}
```
Used when the user has permanently denied a permission — the only way to grant it is through Settings.

### `NOTIFICATION_PERMISSION = null` when API < 33
The caller must handle the `null` case:
```kotlin
val perm = PermissionManager.NOTIFICATION_PERMISSION
if (perm != null) { requestPermission(perm) }
// else: permission is implicitly granted — no request needed
```

---

## 🏗️ Class Structure
`object PermissionManager` — singleton, no instances.

---

## 📋 Properties

| Property | Type | What It Holds |
|----------|------|--------------|
| `CAMERA_PERMISSION` | `String` | `Manifest.permission.CAMERA` |
| `BLUETOOTH_PERMISSIONS` | `Array<String>` | API-appropriate BLE permissions |
| `NOTIFICATION_PERMISSION` | `String?` | `POST_NOTIFICATIONS` on API 33+, `null` below |
| `STORAGE_PERMISSIONS` | `Array<String>` | `READ_MEDIA_IMAGES` on API 33+, `READ_EXTERNAL_STORAGE` below |

---

## ⚙️ Functions

### `isGranted(context, permission): Boolean`
Single permission check.

### `areAllGranted(context, permissions): Boolean`
Returns `true` only if ALL permissions in the array are granted. Used for BLE (needs all 3).

### `showRationaleDialog(fragment, message, onConfirm)`
Shows "Permission Required" dialog with Grant/Cancel. `onConfirm` lambda is called when user taps Grant — the Fragment then calls `requestPermissions`.

### `showSettingsDialog(fragment, message)`
Shows "Permission Required" dialog with Open Settings/Cancel. Opens `ACTION_APPLICATION_DETAILS_SETTINGS` on confirm.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
object PermissionManager {

    val CAMERA_PERMISSION = Manifest.permission.CAMERA
    // Single permission for camera use (QR scanning, file uploads)

    val BLUETOOTH_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Android 12+ needs granular BLE permissions:
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,      // Scan for nearby devices
            Manifest.permission.BLUETOOTH_ADVERTISE, // Advertise this device (teacher BLE beacon)
            Manifest.permission.BLUETOOTH_CONNECT    // Connect to found devices
        )
    } else {
        // Legacy BLE permissions (Android < 12):
        arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)
    }

    val NOTIFICATION_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.POST_NOTIFICATIONS  // Android 13+ requires explicit permission
    } else null  // Below 13: notifications are auto-granted

    val STORAGE_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)  // Android 13+ granular media access
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)  // Legacy storage access
    }

    fun isGranted(context: Context, permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    fun areAllGranted(context: Context, permissions: Array<String>): Boolean =
        permissions.all { isGranted(context, it) }  // all{} = true only if every item matches

    fun showRationaleDialog(fragment: Fragment, message: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(fragment.requireContext())
            .setTitle("Permission Required")
            .setMessage(message)
            .setPositiveButton("Grant") { _, _ -> onConfirm() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun showSettingsDialog(fragment: Fragment, message: String) {
        AlertDialog.Builder(fragment.requireContext())
            .setTitle("Permission Required")
            .setMessage(message)
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", fragment.requireContext().packageName, null)
                    // package URI: "package:com.syed.classconnect"
                }
                fragment.startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
```
