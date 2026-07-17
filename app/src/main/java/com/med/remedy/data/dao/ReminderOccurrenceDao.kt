package com.med.remedy.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.med.remedy.data.entity.EReminderOccurrence
import com.med.remedy.data.entity.ReminderStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderOccurrenceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(occurrence: EReminderOccurrence)

    @Query("""SELECT EXISTS(SELECT 1 FROM reminder_occurrences WHERE reminderId = :reminderId AND date = :today AND reminderStatus = 'TAKEN')""")
    fun isCompletedToday(reminderId: Long, today: String): Flow<Boolean>

    @Query("DELETE FROM reminder_occurrences")
    suspend fun clearAll()

    @Query("""SELECT COUNT(*) FROM reminder_occurrences WHERE date = :today AND reminderStatus = 'TAKEN'""")
    fun getCompletedTodayCount(today: String): Flow<Int>

    @Query("""SELECT COUNT(*) FROM reminder_occurrences WHERE date = :today""")
    fun getTodayReminderCount(today: String): Flow<Int>

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM reminder_occurrences
            WHERE reminderId = :reminderId
            AND date = :today
        )
    """
    )
    suspend fun occurrenceExists(
        reminderId: Long,
        today: String
    ): Boolean

    @Query(
        """
    SELECT reminderId
    FROM reminder_occurrences
    WHERE date = :today
    """
    )
    suspend fun getTodayReminderIds(today: String): List<Long>

    @Query(
        """
        DELETE FROM reminder_occurrences
        WHERE reminderId = :reminderId
        AND date = :today
    """
    )
    suspend fun deleteOccurrence(reminderId: Long, today: String)

    @Query("""
    UPDATE reminder_occurrences
    SET reminderStatus = :status,
        snoozedUntil = :snoozedUntil
    WHERE reminderId = :reminderId
    AND date = :today
""")
    suspend fun updateStatus(
        reminderId: Long,
        today: String,
        status: ReminderStatus,
        snoozedUntil: Long?
    )
}