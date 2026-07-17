package com.med.remedy.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.med.remedy.data.entity.EMedicine
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {

    @Insert
    suspend fun insert(medicine: EMedicine): Long

    @Update
    suspend fun update(medicine: EMedicine)

    @Delete
    suspend fun delete(medicine: EMedicine)

    @Query("SELECT * FROM medicines ORDER BY name ASC")
    fun getAll(): Flow<List<EMedicine>>

    @Query("SELECT * FROM medicines WHERE id = :id")
    suspend fun getById(id: Long): EMedicine?

    @Query("""
        DELETE FROM medicines
        WHERE id NOT IN (
            SELECT DISTINCT medicineId
            FROM reminders
        )
    """)
    suspend fun deleteOrphanMedicines()
}