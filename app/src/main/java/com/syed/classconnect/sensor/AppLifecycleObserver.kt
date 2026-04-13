package com.syed.classconnect.sensor

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Monitors app foreground/background transitions.
 * Used for biometric lock: if the app has been in the background for >3 seconds,
 * the biometric prompt should be shown when the user returns.
 */
@Singleton
class AppLifecycleObserver @Inject constructor() : DefaultLifecycleObserver {

    private var backgroundedAt = 0L
    private var isInBackground = false

    /** True when the user should be prompted for biometric auth on return */
    var shouldShowBiometric = false
        private set

    override fun onStop(owner: LifecycleOwner) {
        isInBackground = true
        backgroundedAt = System.currentTimeMillis()
    }

    override fun onStart(owner: LifecycleOwner) {
        if (isInBackground) {
            val duration = System.currentTimeMillis() - backgroundedAt
            // Only require biometric if the app was backgrounded for >3 seconds
            if (duration >= 3_000L) {
                shouldShowBiometric = true
            }
            isInBackground = false
        }
    }

    /** Called after successful biometric authentication to clear the flag */
    fun resetBiometricFlag() {
        shouldShowBiometric = false
    }
}

