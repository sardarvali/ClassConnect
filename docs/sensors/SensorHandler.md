# SensorHandler.kt — Accelerometer + rotation vector listener for shake detection and flat-phone detection

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/sensor/SensorHandler.kt`

---

## 🎯 What This File Does
`SensorHandler` wraps Android's `SensorManager` to listen to two hardware sensors: the **accelerometer** (detects shake and flat placement) and the **rotation vector** (available for future orientation-based features). When the device is shaken sharply, it calls `onShake()`. When the device is placed face-up and flat, it calls `onFlat()`. A 1-second cooldown prevents multiple rapid `onShake()` callbacks from a single shake gesture. The owner (`ProfileFragment` or any screen that uses it) calls `register()` in `onResume` and `unregister()` in `onPause`. Without this class, no screen can respond to physical device gestures.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.content.Context` | Android SDK | App context | `getSystemService(SensorManager)` |
| `android.hardware.Sensor` | Android SDK | Sensor type constants | `TYPE_ACCELEROMETER`, `TYPE_ROTATION_VECTOR` |
| `android.hardware.SensorEvent` | Android SDK | Sensor reading snapshot | Contains `values[]` array |
| `android.hardware.SensorEventListener` | Android SDK | Interface for sensor callbacks | `SensorHandler` implements it |
| `android.hardware.SensorManager` | Android SDK | System sensor service | Register/unregister listeners |
| `kotlin.math.sqrt` | Kotlin Math | Square root | Computes acceleration magnitude |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `SensorEventListener`
Interface with two methods:
- `onSensorChanged(event: SensorEvent)` — called at `SENSOR_DELAY_NORMAL` (~5 readings/sec) with new sensor data
- `onAccuracyChanged(sensor, accuracy)` — called when sensor calibration changes (not used here)

### `SENSOR_DELAY_NORMAL`
Sampling rate constant: ~200ms between readings (5Hz). Suitable for gesture detection. Faster rates (`SENSOR_DELAY_GAME` = ~20ms) consume more battery — not needed here.

### Shake detection algorithm
```kotlin
val acceleration = sqrt(x*x + y*y + z*z) - SensorManager.GRAVITY_EARTH
```
- `sqrt(x² + y² + z²)` = total vector magnitude of the accelerometer reading
- `SensorManager.GRAVITY_EARTH` = 9.80665 m/s² (subtracted to remove gravity's constant contribution)
- Result: net "extra" acceleration beyond gravity
- If `> SHAKE_THRESHOLD` (8 m/s²) → shake detected

### Flat detection
```kotlin
if (z > 8.5f && Math.abs(x) < 2f && Math.abs(y) < 2f) onFlat()
```
When the phone is face-up on a table: z-axis reads ~9.8 m/s² (gravity pointing down = up in phone coords), x and y are near 0. Threshold of 8.5 catches slight tilts.

### `lastShakeTime` cooldown
```kotlin
val now = System.currentTimeMillis()
if (now - lastShakeTime > SHAKE_COOLDOWN_MS) {  // 1000ms
    lastShakeTime = now
    onShake()
}
```
A single vigorous shake can trigger 5–10 accelerometer readings above threshold. The cooldown ensures `onShake()` is called at most once per second.

### `sensorManager.getDefaultSensor(type)?.let { }` — safe null handling
`getDefaultSensor()` returns `null` if the device has no such sensor. The `?.let { }` only registers if the sensor exists — prevents NullPointerException on devices without these sensors.

### `register()` / `unregister()`
Must be called in lifecycle callbacks:
```kotlin
// In Fragment:
override fun onResume() { super.onResume(); sensorHandler.register() }
override fun onPause()  { super.onPause();  sensorHandler.unregister() }
```
`unregister()` is critical — if omitted, the sensor listener continues firing even when the app is in the background, draining battery rapidly.

---

## 🏗️ Class Structure
`class SensorHandler(context, onShake, onFlat) : SensorEventListener` — plain class, not a Fragment or ViewModel. Instantiated by the Feature Fragment/Activity that needs it.

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `sensorManager` | `SensorManager` | `private val` | System sensor service | Register/unregister listener |
| `lastShakeTime` | `Long` | `private var` | Timestamp of last shake callback | 1-second cooldown enforcement |
| `onShake` | `() -> Unit` | Constructor param | Callback for shake gesture | Caller-defined action |
| `onFlat` | `() -> Unit` | Constructor param | Callback for flat placement | Caller-defined action |

---

## ⚙️ Functions

### `register()`
Registers the listener for both accelerometer and rotation vector sensors. Called in `onResume`.

### `unregister()`
Unregisters from ALL sensors at once via `sensorManager.unregisterListener(this)`. Called in `onPause`.

### `onSensorChanged(event: SensorEvent)`
Routes by `event.sensor.type` to the appropriate handler.

### `handleAccelerometer(event: SensorEvent)`
Computes net acceleration magnitude, checks shake threshold with cooldown, checks flat condition.

### `handleRotation(event: SensorEvent)`
Empty — available for future orientation-based features (e.g., flip-to-mute).

### `onAccuracyChanged(sensor, accuracy)`
Empty — no calibration-dependent behavior needed.

---

## 🔄 Data Flow Diagram
```
Device is shaken
        ↓
Android calls onSensorChanged(event) at 5Hz
        ↓
handleAccelerometer() computes sqrt(x²+y²+z²) - 9.8
        ↓
magnitude > 8 m/s²?  AND  >1s since last shake?
        ↓ YES
onShake() called → caller executes its lambda
        (e.g., ProfileFragment shows random tip,
               or triggers a UI easter egg)
```

---

## ⚠️ Important Notes & Gotchas
- **Always call `unregister()` in `onPause()`** — sensor listeners are NOT automatically removed when the screen goes away.
- `GRAVITY_EARTH` subtraction makes the threshold device-orientation-independent. Without it, holding the phone differently would change the baseline.
- `onFlat()` is called **continuously** while the phone remains flat — callers should guard against repeated calls if they only need a one-time trigger.
- The `handleRotation()` stub exists for future use — if rotation-based features are added, add logic here.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
// ═══════════════════════════════════════
// SensorHandler.kt
// ═══════════════════════════════════════

package com.syed.classconnect.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class SensorHandler(
    private val context: Context,
    private val onShake: () -> Unit,   // Lambda called when shake detected
    private val onFlat: () -> Unit     // Lambda called when phone placed flat
) : SensorEventListener {
// Implements SensorEventListener — Android calls onSensorChanged() with readings.

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    // Get the system sensor service. Cast is safe — SENSOR_SERVICE always returns SensorManager.

    private var lastShakeTime = 0L
    // Epoch ms of last shake callback. Used to enforce 1-second cooldown.

    fun register() {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            // SENSOR_DELAY_NORMAL = ~5 readings/sec. Sufficient for gesture detection.
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        // ?.let { } = only register if the sensor exists on this device.
    }

    fun unregister() = sensorManager.unregisterListener(this)
    // Unregisters from ALL sensors. MUST be called in onPause() to stop battery drain.

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER  -> handleAccelerometer(event)
            Sensor.TYPE_ROTATION_VECTOR -> handleRotation(event)
        }
    }

    private fun handleAccelerometer(event: SensorEvent) {
        val x = event.values[0]   // Left/right acceleration (m/s²)
        val y = event.values[1]   // Forward/back acceleration
        val z = event.values[2]   // Up/down acceleration

        val acceleration = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH
        // Total vector magnitude minus gravity. Result = "extra" acceleration from motion.
        // SensorManager.GRAVITY_EARTH = 9.80665 m/s²

        if (acceleration > SHAKE_THRESHOLD) {
            // 8 m/s² threshold — a gentle tap is ~3, a sharp shake is 10-20.
            val now = System.currentTimeMillis()
            if (now - lastShakeTime > SHAKE_COOLDOWN_MS) {
                // At least 1 second since last shake callback — debounce.
                lastShakeTime = now
                onShake()   // Notify the caller.
            }
        }

        // Flat detection: z ≈ 9.8 (gravity straight down), x and y near 0.
        if (z > 8.5f && Math.abs(x) < 2f && Math.abs(y) < 2f) onFlat()
    }

    private fun handleRotation(event: SensorEvent) {
        // Available for future orientation-based features.
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No action needed — we don't depend on sensor calibration level.
    }

    companion object {
        private const val SHAKE_THRESHOLD = 8f       // m/s² net acceleration
        private const val SHAKE_COOLDOWN_MS = 1000L  // 1 second between shake events
    }
}
```

