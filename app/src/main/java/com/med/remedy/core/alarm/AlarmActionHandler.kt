package com.med.remedy.core.alarm
/*
This AlarmActionHandler.kt file contains implementation for two functions those are responsible for
handling the Complete [ TAKEN ] and Snooze [ SNOOZE ] action and update the room using Repository
functions.
*/

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.med.remedy.data.ReminderRepository
import com.med.remedy.data.entity.ReminderStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton


// DEFINITION: To handle the notification actions
@Singleton
class AlarmActionHandler @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val repository: ReminderRepository,
    private val alarmScheduler: AlarmScheduler
) {
    private val _resolvedReminderId = MutableSharedFlow<Long>(extraBufferCapacity = 1)
    val resolvedReminderId = _resolvedReminderId.asSharedFlow()

    // DEFINITION: Handle Complete action in notification
    suspend fun handleComplete(reminderId: Long) {
        Log.d("Alarm Handler", "Mark as taken for ID: $reminderId")

        AlarmPlayer.stop()
        NotificationManagerCompat.from(context).cancel(reminderId.toInt())
        repository.updateStatus(reminderId, ReminderStatus.TAKEN, null)
        _resolvedReminderId.emit(reminderId)
    }

    // DEFINITION: Handle snoozing state of alarm in notification
    suspend fun handleSnooze(reminderId: Long) {
        Log.d("Alarm Handler", "Alarm snoozed for ID: $reminderId")

        AlarmPlayer.stop()
        NotificationManagerCompat.from(context).cancel(reminderId.toInt())
        repository.updateStatus(
            reminderId,
            ReminderStatus.SNOOZED,
            snoozedUntil = System.currentTimeMillis() + 10 * 60_000L
        )
        alarmScheduler.snooze(reminderId)
        _resolvedReminderId.emit(reminderId)
    }
}