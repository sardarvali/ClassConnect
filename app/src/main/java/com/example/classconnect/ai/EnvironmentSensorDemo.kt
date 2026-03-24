package com.example.classconnect.ai

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EnvironmentSensorDemo : AppCompatActivity(), SensorEventListener {

    lateinit var sensorManager: SensorManager

    var lightSensor: Sensor? = null
    var tempSensor: Sensor? = null
    var pressureSensor: Sensor? = null
    var humiditySensor: Sensor? = null

    lateinit var lightText: TextView
    lateinit var tempText: TextView
    lateinit var pressureText: TextView
    lateinit var humidityText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_environment_sensor_demo)

        lightText = findViewById(R.id.lightText)
        tempText = findViewById(R.id.tempText)
        pressureText = findViewById(R.id.pressureText)
        humidityText = findViewById(R.id.humidityText)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)

        // Optional: Check availability
        if (lightSensor == null) lightText.text = "Light Sensor not available"
        if (tempSensor == null) tempText.text = "Temperature Sensor not available"
        if (pressureSensor == null) pressureText.text = "Pressure Sensor not available"
        if (humiditySensor == null) humidityText.text = "Humidity Sensor not available"
    }

    override fun onResume() {
        super.onResume()

        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        tempSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        pressureSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        humiditySensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {

            when (event.sensor.type) {

                Sensor.TYPE_LIGHT -> {
                    val value = event.values[0]
                    lightText.text = "Light: $value lx"
                }

                Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                    val value = event.values[0]
                    tempText.text = "Temperature: $value °C"
                }

                Sensor.TYPE_PRESSURE -> {
                    val value = event.values[0]
                    pressureText.text = "Pressure: $value hPa"
                }

                Sensor.TYPE_RELATIVE_HUMIDITY -> {
                    val value = event.values[0]
                    humidityText.text = "Humidity: $value %"
                }
            }
        }
    }
}