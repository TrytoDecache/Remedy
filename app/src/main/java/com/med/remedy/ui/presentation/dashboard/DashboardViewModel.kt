package com.med.remedy.ui.presentation.dashboard

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.med.remedy.core.alarm.AlarmPlayer
import com.med.remedy.core.alarm.AlarmScheduler
import com.med.remedy.data.ReminderRepository
import com.med.remedy.data.entity.ReminderStatus
import com.med.remedy.data.entity.RepeatDays
import com.med.remedy.data.model.DashboardProgress
import com.med.remedy.data.model.ReminderWithStatus
import com.med.remedy.data.model.nextTriggerMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val alarmScheduler: AlarmScheduler,
    @ApplicationContext context: Context
) : ViewModel() {

    private val notificationManagerCompat = NotificationManagerCompat.from(context)

    private fun getTodayMask(): Int {
        return when (LocalDate.now().dayOfWeek) {
            DayOfWeek.MONDAY -> RepeatDays.MONDAY
            DayOfWeek.TUESDAY -> RepeatDays.TUESDAY
            DayOfWeek.WEDNESDAY -> RepeatDays.WEDNESDAY
            DayOfWeek.THURSDAY -> RepeatDays.THURSDAY
            DayOfWeek.FRIDAY -> RepeatDays.FRIDAY
            DayOfWeek.SATURDAY -> RepeatDays.SATURDAY
            DayOfWeek.SUNDAY -> RepeatDays.SUNDAY
        }
    }

    val nextMedicine = repository.getEnabledReminders()
        .map { reminders ->
            val todayMask = getTodayMask()
            val now = System.currentTimeMillis()

            reminders
                .filter {
                    Log.d(
                        "TEST",
                        "${it.name} | ${it.reminderStatus} | ${it.snoozedUntil} | next=${it.nextTriggerMillis()}"
                    )
                    it.reminderStatus != ReminderStatus.TAKEN
                }
                .filter { (it.repeatMask and todayMask) != 0 }
                .filter { it.nextTriggerMillis() >= now }
                .minByOrNull { it.nextTriggerMillis() }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val upcomingMedicines = repository.getEnabledReminders()
        .map { reminders ->
            val todayMask = getTodayMask()

            reminders
                .filter { (it.repeatMask and todayMask) != 0 }
                .sortedWith(
                    compareBy( { it.hour }, { it.minute }, { it.reminderId } )
                )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun updateStatus(reminderId: Long, reminderStatus: ReminderStatus) {
        viewModelScope.launch {
            AlarmPlayer.stop()
            notificationManagerCompat.cancel(reminderId.toInt())
            alarmScheduler.cancel(reminderId)
            repository.updateStatus(
                reminderId,
                reminderStatus,
            )
        }
    }

    val uiState: StateFlow<DashboardProgress> = repository.getDashboardProgress()
        .stateIn(
            scope = viewModelScope,
            // Keeps the stream alive for 5 seconds after UI unbinds (handles screen rotations smoothly)
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardProgress(total = 0, completed = 0)
        )

    val onceReminder: StateFlow<ReminderWithStatus?> = repository.getOnceAlarm()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}