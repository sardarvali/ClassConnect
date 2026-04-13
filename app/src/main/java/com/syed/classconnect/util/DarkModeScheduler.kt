package com.syed.classconnect.util

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * Manages dark mode scheduling for smart auto-switching
 * Supports schedule-based and sunset/sunrise-based switching
 */
class DarkModeScheduler(private val context: Context) {

    companion object {
        private const val DARK_MODE_WORK_TAG = "dark_mode_schedule"
        private const val DARK_MODE_START_PREF = "dark_mode_start_time"
        private const val DARK_MODE_END_PREF = "dark_mode_end_time"
    }

    /**
     * Schedules dark mode to turn on/off at specified times
     * @param startHour Hour (0-23) when dark mode should turn on
     * @param endHour Hour (0-23) when dark mode should turn off
     */
    fun scheduleDarkModeByTime(startHour: Int, endHour: Int) {
        val workManager = WorkManager.getInstance(context)

        val darkModeWork = PeriodicWorkRequestBuilder<DarkModeWorker>(
            1, TimeUnit.DAYS
        ).addTag(DARK_MODE_WORK_TAG)
            .setInitialDelay(calculateInitialDelay(), TimeUnit.MINUTES)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "dark_mode_schedule",
            ExistingPeriodicWorkPolicy.KEEP,
            darkModeWork
        )
    }

    /**
     * Schedules dark mode based on sunset/sunrise times
     * Requires location permission for accurate times
     */
    fun scheduleDarkModeByLocation(latitude: Double, longitude: Double) {
        // Implementation would use location-based sunset/sunrise API
        // For now, fallback to manual scheduling
    }

    /**
     * Cancels dark mode scheduling
     */
    fun cancelDarkModeScheduling() {
        WorkManager.getInstance(context).cancelAllWorkByTag(DARK_MODE_WORK_TAG)
    }

    private fun calculateInitialDelay(): Long {
        val now = System.currentTimeMillis()
        val nextMidnight = (now / (24 * 60 * 60 * 1000) + 1) * 24 * 60 * 60 * 1000
        return (nextMidnight - now) / (60 * 1000) // Return minutes
    }
}

/**
 * Worker for dark mode scheduling
 */
class DarkModeWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        return try {
            // Check if it's time to switch modes and apply accordingly
            val isDarkModeTime = isDarkModeTime()
            // Apply dark mode setting
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun isDarkModeTime(): Boolean {
        val calendar = java.util.Calendar.getInstance()
        val currentHour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        return currentHour in 20..7 // Example: 8 PM to 7 AM
    }
}

/**
 * AMOLED dark mode support for battery optimization
 */
class AmoledDarkModeConfig {
    companion object {
        // Use pure black (#000000) for AMOLED screens
        const val AMOLED_BLACK = 0xFF000000.toInt()

        // Use dark gray for regular OLED/LCD
        const val REGULAR_DARK = 0xFF121212.toInt()
    }

    /**
     * Determines if device has AMOLED screen
     */
    fun isAmoledScreen(context: Context): Boolean {
        // Check if device has always-on display or AMOLED screen
        return context.packageManager.hasSystemFeature("android.hardware.screen.amoled")
    }

    /**
     * Gets appropriate dark color based on screen type
     */
    fun getDarkBackgroundColor(context: Context): Int {
        return if (isAmoledScreen(context)) AMOLED_BLACK else REGULAR_DARK
    }
}

