# AttendanceBleService — Bluetooth Low Energy foreground service for attendance

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/service/AttendanceBleService.kt`

---

## 🎯 What This File Does
AttendanceBleService is a foreground Service that advertises a BLE beacon while a teacher's attendance session is active. Students with the app can detect this beacon as a proximity-based attendance alternative to QR scanning. Runs as a foreground service with a persistent notification.

---

## 📦 Imports — Every Line Explained

| Import | Library / Package | What It Is | Why This File Needs It |
|--------|------------------|-----------|----------------------|
| `android.app.Notification` / `NotificationChannel` / `NotificationManager` | Android SDK | Notifications | Foreground service requires a notification |
| `android.app.Service` | Android SDK | Service base class | This class extends Service |
| `android.bluetooth.BluetoothManager` | Android SDK | Bluetooth access | Gets BluetoothAdapter |
| `android.bluetooth.le.*` | Android SDK | BLE APIs | Advertising settings, data, callback |
| `android.content.Context` / `Intent` | Android SDK | System services | Service lifecycle |
| `android.os.Build` / `IBinder` | Android SDK | OS version check, binding | Notification channel creation, onBind |
| `android.os.ParcelUuid` | Android SDK | UUID wrapper | Wraps service UUID for BLE advertising |
| `androidx.core.app.NotificationCompat` | AndroidX | Backward-compatible notifications | Builds foreground notification |
| `com.syed.classconnect.R` | App (Generated) | Resources | Icon references |
| `java.util.UUID` | Java | UUID | Fixed service UUID for discovery |

---

## ⚙️ Key Functions

### `onCreate()`
Starts the service in foreground mode with a persistent notification "Attendance Session Active".

### `onStartCommand(intent, flags, startId): Int`
Calls `startAdvertising()`. Returns `START_STICKY` so the system restarts the service if killed.

### `startAdvertising()`
1. Gets BluetoothAdapter from BluetoothManager
2. Gets BluetoothLeAdvertiser
3. Configures: LOW_LATENCY mode, non-connectable, MEDIUM power, no timeout
4. Advertises with a fixed service UUID

### `onDestroy()`
Stops BLE advertising and cleans up.

---

## ⚠️ Important Notes
- Uses a FIXED UUID: `550e8400-e29b-41d4-a716-446655440000` — both teacher and student apps must agree on this
- Requires `BLUETOOTH_ADVERTISE` permission (Android 12+) or `BLUETOOTH_ADMIN` (older)
- Foreground service notification is required by Android for services running while app is in background
- `START_STICKY`: system restarts service if killed for memory reasons

