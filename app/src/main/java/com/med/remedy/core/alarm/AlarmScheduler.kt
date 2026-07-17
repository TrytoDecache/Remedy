package com.med.remedy.core.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.med.remedy.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @param:ApplicationContext val context: Context,
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(
        reminderId: Long,
        medicineName: String,
        dosage: String,
        hour: Int,
        minute: Int,
        repeatMask: Int
    ) {
        cancel(reminderId)

        Log.d("AlarmScheduler", "Scheduling precise alarm for ID: $reminderId")
        val triggerAt = calcNextTriggerTime(hour, minute, repeatMask)
        val alarmIntent = pendingIntent(reminderId, medicineName, dosage, hour, minute, repeatMask)

        val showIntent = PendingIntent.getActivity(
            context,
            reminderId.toInt(),
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        try {
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(
                    triggerAt,
                    showIntent
                ),
                alarmIntent
            )

            Log.d("AlarmScheduler", "AlarmClock scheduled: ${Date(triggerAt)}")

        } catch (e: SecurityException) {
            Log.e("AlarmScheduler", "Unable to schedule AlarmClock", e)
        }
    }

    fun cancel(reminderId: Long) {
        val intent = reminderIdPendingIntent(reminderId)
        alarmManager.cancel(intent)
        Log.d("AlarmScheduler", "Alarm canceled for ID: $reminderId")
    }


    fun reschedule(
        reminderId: Long,
        medicineName: String,
        dosage: String,
        hour: Int,
        minute: Int,
        repeatMask: Int
    ) = schedule(reminderId, medicineName, dosage, hour, minute, repeatMask)

    fun snooze(reminderId: Long) {
        val triggerAt = System.currentTimeMillis() + 10 * 60_000L
        val intent = reminderIdPendingIntent(reminderId)

        val showIntent = PendingIntent.getActivity(
            context,
            reminderId.toInt(),
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(
                    triggerAt,
                    showIntent
                ),
                intent
            )
            Log.d("AlarmScheduler", "Snooze AlarmClock scheduled for 10 minutes")

        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, intent)
        }
    }

    private fun pendingIntent(
        reminderId: Long,
        medicineName: String,
        dosage: String,
        hour: Int,
        minute: Int,
        repeatMask: Int
    ): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("reminder_id", reminderId)
            putExtra("medicine_name", medicineName)
            putExtra("dosage", dosage)
            putExtra("hour", hour)
            putExtra("minute", minute)
            putExtra("repeat_mask", repeatMask)
        }

        return PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun reminderIdPendingIntent(reminderId: Long): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("reminder_id", reminderId)
        }

        return PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun calcNextTriggerTime(
        hour: Int,
        minute: Int,
        repeatMask: Int
    ): Long {
        val now = LocalDateTime.now()

        for (i in 0..7) {
            val candidate = now
                .plusDays(i.toLong())
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0)

            val dayBit = dayToBit(candidate.dayOfWeek)

            if ((repeatMask and dayBit) != 0 && candidate.isAfter(now)) {
                return candidate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            }
        }

        var fallback = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
        if (!fallback.isAfter(now)) {
            fallback = fallback.plusDays(1)
        }
        return fallback.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    private fun dayToBit(day: DayOfWeek): Int {
        return when (day) {
            DayOfWeek.MONDAY -> 1 shl 0
            DayOfWeek.TUESDAY -> 1 shl 1
            DayOfWeek.WEDNESDAY -> 1 shl 2
            DayOfWeek.THURSDAY -> 1 shl 3
            DayOfWeek.FRIDAY -> 1 shl 4
            DayOfWeek.SATURDAY -> 1 shl 5
            DayOfWeek.SUNDAY -> 1 shl 6
        }
    }
}