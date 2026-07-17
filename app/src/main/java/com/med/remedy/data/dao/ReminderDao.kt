package com.med.remedy.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.med.remedy.data.entity.EReminder
import com.med.remedy.data.model.ReminderWithMedicine
import com.med.remedy.data.model.ReminderWithStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Insert
    suspend fun insert(reminder: EReminder): Long

    @Update
    suspend fun update(reminder: EReminder)

    @Delete
    suspend fun delete(reminder: EReminder)

    @Query("SELECT * FROM reminders")
    fun getAll(): Flow<List<EReminder>>

    @Query("SELECT * FROM reminders WHERE medicineId = :medicineId")
    fun getByMedicine(medicineId: Long): Flow<List<EReminder>>

    @Query("""
        SELECT
            r.id AS reminderId,
            r.medicineId,
            m.name,
            m.dosage,
            m.description,
            r.hour,
            r.minute,
            r.repeatMask,
            r.isEnabled,
            
            COALESCE(ro.reminderStatus, 'PENDING') AS reminderStatus,
            ro.snoozedUntil AS snoozedUntil
        
        FROM reminders r
        
        INNER JOIN medicines m
        ON r.medicineId = m.id
        
        LEFT JOIN reminder_occurrences ro
        ON ro.reminderId = r.id
        AND ro.date = :today
        
        WHERE r.isEnabled = 1
        
        ORDER BY r.hour, r.minute
    """)
    fun getEnabledReminders(today: String): Flow<List<ReminderWithStatus>>

    @Query("""
        SELECT *
        FROM reminders
        WHERE isEnabled = 1
    """)
    suspend fun getEnabledReminders(): List<EReminder>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getById(id: Long): EReminder?

    @Query("""
        SELECT r.id AS reminderId, r.medicineId, 
            m.name, m.dosage, m.description,
            r.hour, r.minute, r.repeatMask, r.isEnabled
        FROM reminders r
        INNER JOIN medicines m
        ON r.medicineId = m.id
        ORDER BY r.hour, r.minute""")
    fun getAllWithMedicine(): Flow<List<ReminderWithMedicine>>

    @Query("""SELECT COUNT(*) FROM reminders WHERE isEnabled = 1""")
    fun getEnabledRemindersCount(): Flow<Int>

    @Query("""
    SELECT
        r.id AS reminderId,
        r.medicineId,
        m.name,
        m.dosage,
        m.description,
        r.hour,
        r.minute,
        r.repeatMask,
        r.isEnabled
    FROM reminders r
    INNER JOIN medicines m
        ON r.medicineId = m.id
    WHERE r.id = :reminderId
""")
    suspend fun getReminderWithMedicine(reminderId: Long): ReminderWithMedicine
}