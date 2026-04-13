package com.syed.classconnect.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothManager
<<<<<<< HEAD
import android.content.Context
=======
>>>>>>> final
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
<<<<<<< HEAD
import android.content.Intent
=======
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
>>>>>>> final
import android.os.Build
import android.os.IBinder
import android.os.ParcelUuid
import androidx.core.app.NotificationCompat
import com.syed.classconnect.R
import java.util.UUID

class AttendanceBleService : Service() {

    private var advertiser: BluetoothLeAdvertiser? = null
    private val serviceUuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")

    override fun onCreate() {
        super.onCreate()
<<<<<<< HEAD
        startForeground(1, buildNotification())
=======
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                1,
                buildNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
            )
        } else {
            startForeground(1, buildNotification())
        }
>>>>>>> final
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startAdvertising()
        return START_STICKY
    }

    private fun startAdvertising() {
<<<<<<< HEAD
        val adapter = (getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter ?: return
=======
        val adapter =
            (getSystemService(BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter ?: return
>>>>>>> final
        advertiser = adapter.bluetoothLeAdvertiser ?: return

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setConnectable(false)
            .setTimeout(0)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
            .build()

        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(false)
            .addServiceUuid(ParcelUuid(serviceUuid))
            .build()

        advertiser?.startAdvertising(settings, data, advertiseCallback)
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {}
        override fun onStartFailure(errorCode: Int) {}
    }

    override fun onDestroy() {
        advertiser?.stopAdvertising(advertiseCallback)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val channelId = "ble_attendance"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
<<<<<<< HEAD
            val channel = NotificationChannel(channelId, "Attendance BLE", NotificationManager.IMPORTANCE_LOW)
=======
            val channel =
                NotificationChannel(channelId, "Attendance BLE", NotificationManager.IMPORTANCE_LOW)
>>>>>>> final
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_qr_scan)
            .setContentTitle("Attendance Session Active")
            .setContentText("Students can scan attendance")
            .build()
    }
}

