package com.med.remedy.ui.presentation.create

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.med.remedy.data.ReminderRepository
import com.med.remedy.data.entity.EMedicine
import com.med.remedy.data.entity.EReminder
import com.med.remedy.data.entity.RepeatDays
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CreateReminderViewModel @Inject constructor(
    private val repository: ReminderRepository
) : ViewModel() {

    private val _event = MutableSharedFlow<CreateReminderEvent>()
    private var loadJob: Job? = null

    val event = _event.asSharedFlow()

    val medicineName = TextFieldState()
    val dosage = TextFieldState()
    val description = TextFieldState()
    var hour by mutableIntStateOf(8)
        private set

    var minute by mutableIntStateOf(30)
        private set

    private val _formattedTime by derivedStateOf {
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }

        val amPm = if (hour >= 12) "PM" else "AM"
        String.format(Locale.ROOT, "%02d:%02d %s", displayHour, minute, amPm)
    }
    val formattedTime: String get() = _formattedTime

    var repeatMask by mutableIntStateOf(0)
        private set

    private val _repeatText = derivedStateOf { repeatMask.toRepeatText() }
    val repeatText: String get() = _repeatText.value

    fun updateTime(hour: Int, minute: Int) {
        this.hour = hour
        this.minute = minute
    }

    fun updateRepeat(mask: Int) {
        repeatMask = mask
    }

    fun loadReminder(reminderId: Long) {

        if (reminderId == -1L) {
            clearFields()
            return
        }

        loadJob?.cancel()

        loadJob = viewModelScope.launch {

            val reminder = repository.getReminderById(reminderId) ?: return@launch
            val medicine = repository.getMedicineById(reminder.medicineId) ?: return@launch

            medicineName.edit { replace(0, length, medicine.name) }
                dosage.edit { replace(0, length, medicine.dosage) }
            description.edit { replace(0, length, medicine.description) }

            hour = reminder.hour
            minute = reminder.minute
            repeatMask = reminder.repeatMask
        }
    }

    fun saveReminder() {

        viewModelScope.launch {

            val error = validate()

            if (error != null) {
                _event.emit(CreateReminderEvent.Error(error))
                return@launch
            }

            try {
                val medicineId = repository.insertMedicine(
                    EMedicine(
                        name = medicineName.text.toString().trim(),
                        dosage = dosage.text.toString().trim(),
                        description = description.text.toString().trim()
                    )
                )

                repository.insertReminder(
                    EReminder(
                        medicineId = medicineId,
                        hour = hour,
                        minute = minute,
                        repeatMask = repeatMask,
                        isEnabled = true
                    )
                )

                clearFields()
                _event.emit(CreateReminderEvent.Success)

            } catch(_: Exception) {
                _event.emit(
                    CreateReminderEvent.Error("Failed to save reminder.")
                )
            }

            // alarm manager implementation here...
        }
    }

    fun updateReminder(reminderId: Long) {

        viewModelScope.launch {

            val error = validate()

            if (error != null) {
                _event.emit(CreateReminderEvent.Error(error))
                return@launch
            }

            try {
                val reminder = repository.getReminderById(reminderId)

                if (reminder == null) {
                    _event.emit(CreateReminderEvent.Error("Reminder not found in database"))
                    return@launch
                }

                repository.updateMedicine(
                    EMedicine(
                        id = reminder.medicineId,
                        name = medicineName.text.toString().trim(),
                        dosage = dosage.text.toString().trim(),
                        description = description.text.toString().trim()
                    )
                )

                repository.updateReminder(
                    EReminder(
                        id = reminderId,
                        medicineId = reminder.medicineId,
                        hour = hour,
                        minute = minute,
                        repeatMask = repeatMask,
                        isEnabled = reminder.isEnabled
                    )
                )

                clearFields()
                _event.emit(CreateReminderEvent.Success)

            } catch (_: Exception) {
                _event.emit(
                    CreateReminderEvent.Error("Failed to edit current reminder")
                )
            }

        }

    }

    fun discardChanges() = clearFields()

    private fun clearFields() {
        medicineName.clearText()
        dosage.clearText()
        description.clearText()
        hour = 8
        minute = 30
        repeatMask = 0
    }

    private fun validate(): String? {

        if (medicineName.text.isBlank())
            return "Medicine name is required."

        if (dosage.text.isBlank())
            return "Dosage is required."

        if (description.text.isBlank())
            return "Description is required."

        return null
    }


}

private fun Int.toRepeatText(): String {

    if (this == 0) return "Once"

    val everyday =
        RepeatDays.MONDAY or
                RepeatDays.TUESDAY or
                RepeatDays.WEDNESDAY or
                RepeatDays.THURSDAY or
                RepeatDays.FRIDAY or
                RepeatDays.SATURDAY or
                RepeatDays.SUNDAY

    if (this == everyday) return "Everyday"

    val weekdays =
        RepeatDays.MONDAY or
                RepeatDays.TUESDAY or
                RepeatDays.WEDNESDAY or
                RepeatDays.THURSDAY or
                RepeatDays.FRIDAY

    if (this == weekdays) return "Weekdays"

    val weekends =
        RepeatDays.SATURDAY or
                RepeatDays.SUNDAY

    if (this == weekends) return "Weekends"

    val days = buildList {
        if (this@toRepeatText and RepeatDays.MONDAY != 0) add("Mon")
        if (this@toRepeatText and RepeatDays.TUESDAY != 0) add("Tue")
        if (this@toRepeatText and RepeatDays.WEDNESDAY != 0) add("Wed")
        if (this@toRepeatText and RepeatDays.THURSDAY != 0) add("Thu")
        if (this@toRepeatText and RepeatDays.FRIDAY != 0) add("Fri")
        if (this@toRepeatText and RepeatDays.SATURDAY != 0) add("Sat")
        if (this@toRepeatText and RepeatDays.SUNDAY != 0) add("Sun")
    }

    return days.joinToString(", ")
}