package com.med.remedy.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.med.remedy.data.dao.MedicineDao
import com.med.remedy.data.dao.ReminderDao
import com.med.remedy.data.dao.ReminderOccurrenceDao
import com.med.remedy.data.entity.EMedicine
import com.med.remedy.data.entity.EReminder
import com.med.remedy.data.entity.EReminderOccurrence
import com.med.remedy.data.entity.ReminderStatus

@Database(
    entities = [EMedicine::class, EReminder::class, EReminderOccurrence::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(ReminderStatusConverter::class)
abstract class RemedyDatabase : RoomDatabase() {

    abstract fun medicineDao(): MedicineDao
    abstract fun reminderDao(): ReminderDao
    abstract  fun reminderOccurrenceDao() : ReminderOccurrenceDao
}

class ReminderStatusConverter {
    @TypeConverter
    fun fromStatus(status: ReminderStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(value: String): ReminderStatus {
        return ReminderStatus.valueOf(value)
    }
}