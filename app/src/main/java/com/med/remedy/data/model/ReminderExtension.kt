package com.med.remedy.data.model

import com.med.remedy.data.entity.ReminderStatus
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Locale

fun ReminderWithMedicine.formattedTime(): String {

    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }

    val amPm = if (hour >= 12) "PM" else "AM"

    return String.format(
        Locale.ROOT,
        "%02d:%02d %s",
        displayHour,
        minute,
        amPm
    )
}

fun ReminderWithMedicine.hasDay(bit: Int): Boolean {
    return repeatMask and bit != 0
}


fun ReminderWithStatus.nextTriggerMillis(): Long {
    return snoozedUntil ?: LocalDate.now()
        .atTime(hour, minute)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

data class TimeLeft(val minutes: Long, val seconds: Long)

fun ReminderWithStatus.timeLeft(now: LocalTime): TimeLeft? {
    if (reminderStatus == ReminderStatus.TAKEN) return null

    val totalSeconds: Long = if (reminderStatus == ReminderStatus.SNOOZED && snoozedUntil != null) {
        (snoozedUntil - System.currentTimeMillis()) / 1000
    } else {
        Duration.between(now, LocalTime.of(hour, minute)).seconds
    }

    if (totalSeconds !in 1..3599) return null

    return TimeLeft(totalSeconds / 60, totalSeconds % 60)
}

fun ReminderWithStatus.formattedDisplayTime(): String {

    val localTime = if (snoozedUntil != null) {
        Instant.ofEpochMilli(snoozedUntil)
            .atZone(ZoneId.systemDefault())
            .toLocalTime()
    } else {
        LocalTime.of(hour, minute)
    }

    val displayHour = when {
        localTime.hour == 0 -> 12
        localTime.hour > 12 -> localTime.hour - 12
        else -> localTime.hour
    }

    val amPm = if (localTime.hour >= 12) "PM" else "AM"

    return String.format(
        Locale.ROOT,
        "%02d:%02d %s",
        displayHour,
        localTime.minute,
        amPm
    )
}