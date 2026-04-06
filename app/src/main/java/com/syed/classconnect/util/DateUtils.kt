package com.syed.classconnect.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {
    private val displayFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun Timestamp.toDisplayDate(): String = displayFormat.format(toDate())
    fun Timestamp.toDisplayTime(): String = timeFormat.format(toDate())
    fun Timestamp.toDisplayDateTime(): String = dateTimeFormat.format(toDate())
    fun Timestamp.toIsoDate(): String = isoFormat.format(toDate())

    fun Timestamp.daysUntil(): Long {
        val now = Calendar.getInstance().timeInMillis
        val due = toDate().time
        val diff = due - now
        return TimeUnit.MILLISECONDS.toDays(diff)
    }

    fun Timestamp.isOverdue(): Boolean = toDate().before(Date())

    fun Timestamp.toRelativeTime(): String {
        val now = System.currentTimeMillis()
        val diff = now - toDate().time
        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
            diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m ago"
            diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}h ago"
            diff < TimeUnit.DAYS.toMillis(7) -> "${TimeUnit.MILLISECONDS.toDays(diff)}d ago"
            else -> displayFormat.format(toDate())
        }
    }

    fun todayIsoString(): String = isoFormat.format(Date())

    fun formatDuration(seconds: Long): String {
        val m = seconds / 60
        val s = seconds % 60
        return "%02d:%02d".format(m, s)
    }

    /** Static helper used by adapters — delegates to Timestamp.toRelativeTime() extension */
    fun formatRelative(timestamp: Timestamp?): String {
        return timestamp?.toRelativeTime() ?: ""
    }
}

