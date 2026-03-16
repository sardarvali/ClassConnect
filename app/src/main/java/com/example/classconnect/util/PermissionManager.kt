package com.syed.classconnect.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionManager {

    val CAMERA_PERMISSION = Manifest.permission.CAMERA

    val BLUETOOTH_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    } else {
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
        )
    }

    val NOTIFICATION_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.POST_NOTIFICATIONS
    } else null

    val STORAGE_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    fun isGranted(context: Context, permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    fun areAllGranted(context: Context, permissions: Array<String>): Boolean =
        permissions.all { isGranted(context, it) }

    fun showRationaleDialog(
        fragment: Fragment,
        message: String,
        onConfirm: () -> Unit
    ) {
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
                }
                fragment.startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

