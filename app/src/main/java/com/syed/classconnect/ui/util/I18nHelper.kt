package com.syed.classconnect.ui.util

import android.content.Context
import android.content.res.Configuration
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * I18nHelper - Internationalization (i18n) utilities for multi-language and locale support
 *
 * Features:
 * - Locale-aware date/time formatting
 * - Pluralization rules
 * - RTL layout support detection
 * - Language-specific formatting
 */
object I18nHelper {

    // ────────────────────────────────────────────────────────────────────────────
    // DATE & TIME FORMATTING
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Format date using device locale
     * @param date The date to format
     * @param pattern The format pattern (uses device locale for day/month names)
     * @return Formatted date string
     */
    fun formatDate(
        date: Date,
        pattern: String = "MMM dd, yyyy",
        locale: Locale = Locale.getDefault()
    ): String {
        return SimpleDateFormat(pattern, locale).format(date)
    }

    /**
     * Format time using device locale
     */
    fun formatTime(
        date: Date,
        pattern: String = "h:mm a",
        locale: Locale = Locale.getDefault()
    ): String {
        return SimpleDateFormat(pattern, locale).format(date)
    }

    /**
     * Format date and time together (locale-aware)
     */
    fun formatDateTime(
        date: Date,
        datePattern: String = "MMM dd, yyyy",
        timePattern: String = "h:mm a",
        locale: Locale = Locale.getDefault()
    ): String {
        val dateStr = formatDate(date, datePattern, locale)
        val timeStr = formatTime(date, timePattern, locale)
        return "$dateStr • $timeStr"
    }

    /**
     * Relative time formatting (e.g., "2m ago", "1h ago")
     */
    fun formatRelativeTime(date: Date, context: Context): String {
        val now = System.currentTimeMillis()
        val diff = now - date.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            seconds < 60 -> "just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days < 7 -> "${days}d ago"
            else -> formatDate(date)
        }
    }

    /**
     * Format countdown timer (e.g., "02:30" for 2 minutes 30 seconds)
     */
    fun formatCountdown(secondsRemaining: Long): String {
        val hours = secondsRemaining / 3600
        val minutes = (secondsRemaining % 3600) / 60
        val seconds = secondsRemaining % 60

        return when {
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%02d:%02d", minutes, seconds)
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // PLURALIZATION
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Get pluralized string using Android's pluralization rules
     * @param context Android context
     * @param pluralResId Resource ID for plurals (defined in strings.xml)
     * @param count The count to determine singular/plural
     * @param formatArgs Arguments to format the string with
     */
    fun getPlural(
        context: Context,
        pluralResId: Int,
        count: Int,
        vararg formatArgs: Any
    ): String {
        return context.resources.getQuantityString(pluralResId, count, *formatArgs)
    }

    /**
     * Shorthand for assignment pluralization
     */
    fun getAssignmentCountText(context: Context, count: Int): String {
        // Note: Using standard pluralization pattern, adjust resId as needed
        return "$count assignment" + if (count != 1) "s" else ""
    }

    /**
     * Shorthand for student count pluralization
     */
    fun getStudentCountText(context: Context, count: Int): String {
        return "$count student" + if (count != 1) "s" else ""
    }

    /**
     * Shorthand for message count pluralization
     */
    fun getUnreadMessageText(context: Context, count: Int): String {
        return "$count unread message" + if (count != 1) "s" else ""
    }

    // ────────────────────────────────────────────────────────────────────────────
    // RTL (Right-to-Left) SUPPORT
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Check if current locale is RTL (e.g., Arabic, Hebrew)
     */
    fun isRtl(context: Context): Boolean {
        return context.resources.configuration.layoutDirection == 1  // Configuration.LAYOUT_DIRECTION_RTL = 1
    }

    /**
     * Check if a locale is RTL
     */
    fun isLocaleRtl(locale: Locale): Boolean {
        val character = locale.language.toCharArray()[0]
        val directionality = Character.getDirectionality(character)
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC
    }

    /**
     * Get appropriate start/end margins based on RTL
     * Use in code instead of left/right for RTL compatibility
     */
    fun getStartMargin(startValue: Int, endValue: Int, context: Context): Int {
        return if (isRtl(context)) endValue else startValue
    }

    fun getEndMargin(startValue: Int, endValue: Int, context: Context): Int {
        return if (isRtl(context)) startValue else endValue
    }

    // ────────────────────────────────────────────────────────────────────────────
    // NUMBER FORMATTING
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Format number using device locale
     * Handles decimal separators correctly for different locales
     */
    fun formatNumber(number: Double, locale: Locale = Locale.getDefault()): String {
        return java.text.DecimalFormat.getInstance(locale).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }.format(number)
    }

    /**
     * Format percentage using device locale
     */
    fun formatPercentage(number: Double, locale: Locale = Locale.getDefault()): String {
        val formatter = java.text.DecimalFormat.getInstance(locale)
        return "${formatter.format(number * 100)}%"
    }

    // ────────────────────────────────────────────────────────────────────────────
    // CURRENCY (Future enhancement)
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Format currency using device locale
     */
    fun formatCurrency(
        amount: Double,
        locale: Locale = Locale.getDefault()
    ): String {
        val currency = java.text.DecimalFormat.getCurrencyInstance(locale)
        return currency.format(amount)
    }

    // ────────────────────────────────────────────────────────────────────────────
    // LANGUAGE DETECTION
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Get current device language
     */
    fun getCurrentLanguage(): String {
        return Locale.getDefault().language
    }

    /**
     * Get current device language with country
     */
    fun getCurrentLocale(): String {
        return Locale.getDefault().toString()
    }

    /**
     * Check if device language matches a specific language
     */
    fun isLanguage(languageCode: String): Boolean {
        return Locale.getDefault().language.equals(languageCode, ignoreCase = true)
    }

    // ────────────────────────────────────────────────────────────────────────────
    // TEXT DIRECTION HELPERS
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Get text direction for use in views (View.LAYOUT_DIRECTION_LTR or RTL)
     */
    fun getTextDirection(context: Context): Int {
        return if (isRtl(context)) {
            android.view.View.LAYOUT_DIRECTION_RTL
        } else {
            android.view.View.LAYOUT_DIRECTION_LTR
        }
    }

    /**
     * Mirror a horizontal value for RTL layouts
     */
    fun mirrorIfRtl(value: Int, context: Context): Int {
        return if (isRtl(context)) -value else value
    }

    /**
     * Mirror a horizontal float value for RTL layouts
     */
    fun mirrorIfRtl(value: Float, context: Context): Float {
        return if (isRtl(context)) -value else value
    }
}



