package com.syed.classconnect.ui.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.syed.classconnect.R
import com.syed.classconnect.databinding.ActivityPermissionsBinding
import com.syed.classconnect.ui.main.MainActivity
import com.syed.classconnect.util.Constants

class PermissionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionsBinding

    // ── Permission launchers ──────────────────────────────────────────

    private val notificationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> updateStatus(binding.statusNotifications, granted) }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> updateStatus(binding.statusCamera, granted) }

    private val bluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results -> updateStatus(binding.statusBluetooth, results.values.all { it }) }

    private val locationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> updateStatus(binding.statusLocation, granted) }

    // ── Lifecycle ─────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        refreshAllStatuses()

        // Card click handlers — each requests its permission group
        binding.cardNotifications.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!isGranted(Manifest.permission.POST_NOTIFICATIONS)) {
                    notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }

        binding.cardCamera.setOnClickListener {
            if (!isGranted(Manifest.permission.CAMERA)) {
                cameraLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        binding.cardBluetooth.setOnClickListener {
            val perms = bluetoothPermissions()
            if (!perms.all { isGranted(it) }) {
                bluetoothLauncher.launch(perms)
            }
        }

        binding.cardLocation.setOnClickListener {
            if (!isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        binding.btnContinue.setOnClickListener {
            markPermissionsRequested()
            goToMain()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshAllStatuses()
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private fun refreshAllStatuses() {
        // Notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            updateStatus(binding.statusNotifications, isGranted(Manifest.permission.POST_NOTIFICATIONS))
        } else {
            updateStatus(binding.statusNotifications, true) // pre-13 always granted
        }

        // Camera
        updateStatus(binding.statusCamera, isGranted(Manifest.permission.CAMERA))

        // Bluetooth
        updateStatus(binding.statusBluetooth, bluetoothPermissions().all { isGranted(it) })

        // Location
        updateStatus(binding.statusLocation, isGranted(Manifest.permission.ACCESS_FINE_LOCATION))
    }

    private fun updateStatus(tv: TextView, granted: Boolean) {
        if (granted) {
            tv.text = getString(R.string.permissions_granted)
            tv.setTextColor(ContextCompat.getColor(this, R.color.semantic_success))
        } else {
            tv.text = getString(R.string.permissions_denied)
            tv.setTextColor(ContextCompat.getColor(this, R.color.semantic_warning))
        }
    }

    private fun isGranted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    private fun bluetoothPermissions(): Array<String> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)
        }

    private fun markPermissionsRequested() {
        getSharedPreferences("classconnect_prefs", Context.MODE_PRIVATE)
            .edit().putBoolean(Constants.PREF_PERMISSIONS_REQUESTED, true).apply()
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        /** Returns true if the permission screen was already shown once. */
        fun alreadyRequested(context: Context): Boolean =
            context.getSharedPreferences("classconnect_prefs", Context.MODE_PRIVATE)
                .getBoolean(Constants.PREF_PERMISSIONS_REQUESTED, false)
    }
}

