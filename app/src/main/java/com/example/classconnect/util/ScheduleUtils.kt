package com.syed.classconnect.util

import java.util.Calendar
import java.util.Locale

object ScheduleUtils {
            private val dayAliases = mapOf(
                "mon" to "Monday",
                "tue" to "Tuesday",
                "wed" to "Wednesday",
                "thu" to "Thursday",
                "fri" to "Friday",
                "sat" to "Saturday",
                "sun" to "Sunday"
            )

    val weekDays = listOf(
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
        "Sunday"
    )

    fun currentDayName(): String = when (Calendar.getInstance()[Calendar.DAY_OF_WEEK]) {
        Calendar.SUNDAY -> "Sunday"
        Calendar.MONDAY -> "Monday"
        Calendar.TUESDAY -> "Tuesday"
        Calendar.WEDNESDAY -> "Wednesday"
        Calendar.THURSDAY -> "Thursday"
        Calendar.FRIDAY -> "Friday"
        Calendar.SATURDAY -> "Saturday"
        else -> "Monday"
    }

    fun normalizeSchedule(schedule: Map<String, String>): Map<String, String> {
        val normalized = linkedMapOf<String, String>()
        schedule.forEach { (rawDay, rawValue) ->
            val canonical = canonicalDay(rawDay) ?: return@forEach
            val value = rawValue.trim()
            if (value.isNotEmpty()) {
                normalized[canonical] = value
            }
        }
        return weekDays
            .filter { normalized[it].isNullOrBlank().not() }
            .associateWith { normalized[it].orEmpty() }
    }

    fun findScheduleForDay(schedule: Map<String, String>, day: String = currentDayName()): String? {
        val normalized = normalizeSchedule(schedule)
        val targetDay = canonicalDay(day) ?: return null
        return normalized[targetDay]
    }

    fun isScheduledToday(schedule: Map<String, String>): Boolean =
        !findScheduleForDay(schedule, currentDayName()).isNullOrBlank()

    fun weeklyRows(schedule: Map<String, String>): List<Pair<String, String>> {
        val normalized = normalizeSchedule(schedule)
        return weekDays.mapNotNull { day ->
            normalized[day]?.takeIf { it.isNotBlank() }?.let { day to it }
        }
    }

    fun todayStartMinutes(schedule: Map<String, String>): Int? =
        findScheduleForDay(schedule)?.let(::parseStartMinutes)

    private fun canonicalDay(value: String): String? {
        val trimmed = value.trim()
        if (trimmed.isBlank()) return null

        weekDays.firstOrNull { it.equals(trimmed, ignoreCase = true) }?.let { return it }

        val short = trimmed.take(3).lowercase(Locale.US)
        return dayAliases[short]
    }

    private fun parseStartMinutes(slot: String): Int? {
        val text = slot.trim()
        if (text.isEmpty()) return null

        val twelveHour = Regex("""(1[0-2]|0?[1-9]):([0-5]\\d)\\s*([AaPp][Mm])""")
            .find(text)
        if (twelveHour != null) {
            val hourRaw = twelveHour.groupValues[1].toInt()
            val minute = twelveHour.groupValues[2].toInt()
            val meridiem = twelveHour.groupValues[3].uppercase(Locale.US)
            val hour24 = when {
                meridiem == "AM" && hourRaw == 12 -> 0
                meridiem == "PM" && hourRaw != 12 -> hourRaw + 12
                else -> hourRaw
            }
            return hour24 * 60 + minute
        }

        val twentyFourHour = Regex("""([01]?\\d|2[0-3]):([0-5]\\d)""").find(text)
        if (twentyFourHour != null) {
            val hour = twentyFourHour.groupValues[1].toInt()
            val minute = twentyFourHour.groupValues[2].toInt()
            return hour * 60 + minute
        }

        return null
    }
}
