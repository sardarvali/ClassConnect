package com.syed.classconnect.ui.settings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Settings and preferences for user customization
 */
object SettingsKeys {
    // Notification preferences
    val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
    val NOTIFICATION_SOUND_ENABLED = booleanPreferencesKey("notification_sound")
    val NOTIFICATION_VIBRATION_ENABLED = booleanPreferencesKey("notification_vibration")
    val NOTIFICATION_SOUND = stringPreferencesKey("notification_sound_uri")

    // Per-class notification settings
    val CLASS_NOTIFICATIONS = stringPreferencesKey("class_notifications") // JSON map

    // Auto-lock settings
    val AUTO_LOCK_TIMEOUT = intPreferencesKey("auto_lock_timeout") // in seconds

    // Language & localization
    val LANGUAGE = stringPreferencesKey("language") // e.g., "en", "es", "fr"
    val TIMEZONE = stringPreferencesKey("timezone")

    // Dark mode
    val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode")
    val DARK_MODE_SCHEDULING = booleanPreferencesKey("dark_mode_scheduling")
    val DARK_MODE_START_TIME = intPreferencesKey("dark_mode_start_time") // hour (0-23)
    val DARK_MODE_END_TIME = intPreferencesKey("dark_mode_end_time") // hour (0-23)
    val AMOLED_MODE_ENABLED = booleanPreferencesKey("amoled_mode")
    val ACCENT_COLOR = intPreferencesKey("accent_color")

    // Data & Privacy
    val CACHE_SIZE_MB = intPreferencesKey("cache_size_mb")
    val AUTO_BACKUP_ENABLED = booleanPreferencesKey("auto_backup")
    val BACKUP_FREQUENCY = stringPreferencesKey("backup_frequency") // daily, weekly, monthly

    // App settings
    val FONT_SIZE = stringPreferencesKey("font_size") // small, normal, large
    val COMPACT_VIEW = booleanPreferencesKey("compact_view")
}

/**
 * User notification preferences per class
 */
data class NotificationPreference(
    val classId: String,
    val assignmentsEnabled: Boolean = true,
    val quizzesEnabled: Boolean = true,
    val chatEnabled: Boolean = true,
    val announcementsEnabled: Boolean = true,
    val attendanceEnabled: Boolean = true,
    val mutedUntil: Long? = null // Mute notifications until this timestamp
)

/**
 * Data class for app settings
 */
data class AppSettings(
    val notificationsEnabled: Boolean = true,
    val notificationSound: String? = null,
    val vibrationEnabled: Boolean = true,
    val autoLockTimeoutSeconds: Int = 300,
    val language: String = "en",
    val timezone: String = "UTC",
    val darkModeEnabled: Boolean = false,
    val amoledModeEnabled: Boolean = false,
    val accentColorResId: Int? = null,
    val fontSizeMultiplier: Float = 1f,
    val compactViewEnabled: Boolean = false
)

/**
 * Data usage information
 */
data class DataUsage(
    val cacheSizeMB: Long = 0,
    val databaseSizeMB: Long = 0,
    val totalSizeMB: Long = 0,
    val lastCleanupDate: Long? = null
)

