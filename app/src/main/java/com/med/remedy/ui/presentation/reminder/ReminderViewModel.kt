package com.med.remedy.ui.presentation.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.med.remedy.core.alarm.AlarmScheduler
import com.med.remedy.data.ReminderRepository
import com.med.remedy.data.entity.EReminder
import com.med.remedy.data.model.ReminderWithMedicine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    val reminders = repository.getAllWithMedicine()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleReminder(
        reminder: ReminderWithMedicine,
        enabled: Boolean
    ) {
        viewModelScope.launch {

            repository.updateReminder(
                EReminder(
                    id = reminder.reminderId,
                    medicineId = reminder.medicineId,
                    hour = reminder.hour,
                    minute = reminder.minute,
                    repeatMask = reminder.repeatMask,
                    isEnabled = enabled,
                )
            )

            if (enabled) {
                alarmScheduler.schedule(
                    reminder.reminderId,
                    reminder.name,
                    reminder.dosage,
                    reminder.hour,
                    reminder.minute,
                    reminder.repeatMask
                )
            } else {
                alarmScheduler.cancel(reminder.reminderId)
            }
        }
    }

    fun deleteReminder(reminder: ReminderWithMedicine) {
        viewModelScope.launch {
            repository.deleteReminder(
                EReminder(
                    id = reminder.reminderId,
                    medicineId = reminder.medicineId,
                    hour = reminder.hour,
                    minute = reminder.minute,
                    repeatMask = reminder.repeatMask,
                    isEnabled = reminder.isEnabled
                )
            )
        }
    }
}