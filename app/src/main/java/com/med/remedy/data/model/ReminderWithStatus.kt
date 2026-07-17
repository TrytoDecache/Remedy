package com.med.remedy.data.model

import com.med.remedy.data.entity.ReminderStatus

data class ReminderWithStatus(
    val reminderId: Long,
    val medicineId: Long,
    val name: String,
    val dosage: String,
    val description: String,
    val hour: Int,
    val minute: Int,
    val repeatMask: Int,
    val isEnabled: Boolean,
    val reminderStatus: ReminderStatus,
    val snoozedUntil: Long?
)

