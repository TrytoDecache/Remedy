package com.med.remedy.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminder_occurrences",
    foreignKeys = [
        ForeignKey(
            entity = EReminder::class,
            parentColumns = ["id"],
            childColumns = ["reminderId"],
            onDelete = ForeignKey.CASCADE
        )],
    indices = [
        Index(value = ["reminderId", "date"], unique = true)
    ]
)
data class EReminderOccurrence(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reminderId: Long,
    val date: String,
    val reminderStatus: ReminderStatus = ReminderStatus.PENDING,
    val snoozedUntil: Long? = null
)

enum class ReminderStatus { PENDING, SNOOZED, TAKEN }
