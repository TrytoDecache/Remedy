package com.med.remedy.data.entity

import androidx.room.Entity
import java.time.DayOfWeek
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = EMedicine::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("medicineId")]
)
data class EReminder(

    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medicineId: Long,
    val hour: Int,
    val minute: Int,
    val repeatMask: Int,
    val reminderDate: String? = null,
    val isEnabled: Boolean = true
)

object RepeatDays {
    const val MONDAY    = 1 shl 0
    const val TUESDAY   = 1 shl 1
    const val WEDNESDAY = 1 shl 2
    const val THURSDAY  = 1 shl 3
    const val FRIDAY    = 1 shl 4
    const val SATURDAY  = 1 shl 5
    const val SUNDAY    = 1 shl 6
}


fun shouldTakeToday(
    repeatMask: Int,
    dayOfWeek: DayOfWeek
): Boolean {

    val bit = when (dayOfWeek) {
        DayOfWeek.MONDAY -> 1 shl 0
        DayOfWeek.TUESDAY -> 1 shl 1
        DayOfWeek.WEDNESDAY -> 1 shl 2
        DayOfWeek.THURSDAY -> 1 shl 3
        DayOfWeek.FRIDAY -> 1 shl 4
        DayOfWeek.SATURDAY -> 1 shl 5
        DayOfWeek.SUNDAY -> 1 shl 6
    }

    return (repeatMask and bit) != 0
}