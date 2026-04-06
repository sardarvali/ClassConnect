package com.syed.classconnect

import com.syed.classconnect.util.ScheduleUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
}

