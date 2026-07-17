package com.med.remedy.data.model

data class ReminderWithMedicine(
    val reminderId: Long,
    val medicineId: Long,
    val name: String,
    val dosage: String,
    val description: String,
    val hour: Int,
    val minute: Int,
    val repeatMask: Int,
    val isEnabled: Boolean
)
