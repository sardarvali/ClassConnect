package com.syed.classconnect.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class SensorHandler(
    private val context: Context,
    private val onShake: () -> Unit,
    private val onFlat: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var lastShakeTime = 0L

    fun register() {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun unregister() = sensorManager.unregisterListener(this)

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> handleAccelerometer(event)
            Sensor.TYPE_ROTATION_VECTOR -> handleRotation(event)
        }
    }

    private fun handleAccelerometer(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        val acceleration = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH
        if (acceleration > SHAKE_THRESHOLD) {
            val now = System.currentTimeMillis()
            if (now - lastShakeTime > SHAKE_COOLDOWN_MS) {
                lastShakeTime = now
                onShake()
            }
        }
        // Detect flat (face-up) — z close to 9.8, x and y near 0
        if (z > 8.5f && Math.abs(x) < 2f && Math.abs(y) < 2f) onFlat()
    }

    private fun handleRotation(event: SensorEvent) {
        // Additional rotation handling if needed
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    companion object {
        private const val SHAKE_THRESHOLD = 8f
        private const val SHAKE_COOLDOWN_MS = 1000L
    }
}

