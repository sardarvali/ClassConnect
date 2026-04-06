package com.syed.classconnect

import com.google.firebase.Timestamp
import com.syed.classconnect.util.DateUtils.daysUntil
import com.syed.classconnect.util.DateUtils.formatDuration
import com.syed.classconnect.util.DateUtils.isOverdue
import com.syed.classconnect.util.DateUtils.toDisplayDate
import com.syed.classconnect.util.DateUtils.toIsoDate
import com.syed.classconnect.util.DateUtils.toRelativeTime
import com.syed.classconnect.util.DateUtils.todayIsoString
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class DateUtilsTest {

    @Test
    fun `formatDuration formats mm-ss correctly`() {
        assertEquals("00:00", formatDuration(0))
        assertEquals("01:00", formatDuration(60))
        assertEquals("01:30", formatDuration(90))
        assertEquals("10:05", formatDuration(605))
    }

    @Test
    fun `todayIsoString returns yyyy-MM-dd format`() {
        val today = todayIsoString()
        assertTrue(today.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
    }

    @Test
    fun `toDisplayDate returns non-empty string`() {
        val cal = Calendar.getInstance().apply { set(2025, 0, 1) }
        val ts = Timestamp(cal.time)
        assertTrue(ts.toDisplayDate().isNotEmpty())
    }

    @Test
    fun `toIsoDate returns yyyy-MM-dd format`() {
        val cal = Calendar.getInstance().apply { set(2025, 0, 1) }
        val ts = Timestamp(cal.time)
        assertTrue(ts.toIsoDate().matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
    }

    @Test
    fun `isOverdue returns true for past timestamp`() {
        val past = Timestamp(Date(System.currentTimeMillis() - 100_000))
        assertTrue(past.isOverdue())
    }

    @Test
    fun `isOverdue returns false for future timestamp`() {
        val future = Timestamp(Date(System.currentTimeMillis() + 100_000_000))
        assertFalse(future.isOverdue())
    }

    @Test
    fun `daysUntil is negative for past timestamps`() {
        val past = Timestamp(Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000L))
        assertTrue(past.daysUntil() < 0)
    }

    @Test
    fun `daysUntil is positive for future timestamps`() {
        val future = Timestamp(Date(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000L))
        assertTrue(future.daysUntil() > 0)
    }

    @Test
    fun `toRelativeTime returns Just now for recent timestamp`() {
        val recent = Timestamp(Date(System.currentTimeMillis() - 5000))
        assertEquals("Just now", recent.toRelativeTime())
    }

    @Test
    fun `toRelativeTime returns minutes ago`() {
        val fiveMinAgo = Timestamp(Date(System.currentTimeMillis() - 5 * 60 * 1000L))
        assertTrue(fiveMinAgo.toRelativeTime().contains("m ago"))
    }
}
