package com.syed.classconnect

import com.syed.classconnect.util.ScheduleUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ScheduleUtilsTest {

    @Test
    fun `slotsForDay splits and sorts multiline sessions`() {
        val schedule = mapOf(
            "Monday" to "2:00 PM - 3:00 PM\n9:00 AM - 10:00 AM"
        )

        val slots = ScheduleUtils.slotsForDay(schedule, "Monday")

        assertEquals(listOf("9:00 AM - 10:00 AM", "2:00 PM - 3:00 PM"), slots)
    }

    @Test
    fun `todayStartMinutes uses earliest slot when multiple sessions exist`() {
        val schedule = mapOf(
            ScheduleUtils.currentDayName() to "11:30 AM - 12:30 PM\n8:15 AM - 9:00 AM"
        )

        val start = ScheduleUtils.todayStartMinutes(schedule)

        assertEquals(8 * 60 + 15, start)
    }

    @Test
    fun `isScheduledForDay ignores blank and malformed rows`() {
        val schedule = mapOf(
            "Friday" to " \n ; \n"
        )

        assertFalse(ScheduleUtils.isScheduledForDay(schedule, "Friday"))
        assertTrue(ScheduleUtils.slotsForDay(mapOf("Friday" to "10:00 - 11:00"), "Friday").isNotEmpty())
    }

    @Test
    fun `slotWindow parses start and end time from 12 hour slot`() {
        val window = ScheduleUtils.slotWindow("2:00 PM - 3:15 PM")

        assertNotNull(window)
        assertEquals(14 * 60, window?.startMinutes)
        assertEquals(15 * 60 + 15, window?.endMinutes)
    }

    @Test
    fun `slotWindow returns null when slot does not contain range`() {
        assertNull(ScheduleUtils.slotWindow("2:00 PM"))
    }

    @Test
    fun `isNowWithinAnySlot checks current minute window`() {
        val now = java.util.Calendar.getInstance()
        val hour = now.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = now.get(java.util.Calendar.MINUTE)
        val startMinute = if (minute == 0) 0 else minute - 1
        val endMinute = if (minute == 59) 59 else minute + 1
        val slot = String.format("%02d:%02d - %02d:%02d", hour, startMinute, hour, endMinute)
        val schedule = mapOf(ScheduleUtils.currentDayName() to slot)

        assertTrue(ScheduleUtils.isNowWithinAnySlot(schedule))
    }

    @Test
    fun `isDateScheduled maps iso date to weekday`() {
        val schedule = mapOf("Monday" to "2:00 PM - 3:00 PM")

        assertTrue(ScheduleUtils.isDateScheduled(schedule, "2026-04-06"))
        assertFalse(ScheduleUtils.isDateScheduled(schedule, "2026-04-07"))
    }
}

