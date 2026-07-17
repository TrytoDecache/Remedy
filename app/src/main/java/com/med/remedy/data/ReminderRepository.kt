package com.med.remedy.data

import android.util.Log
import com.med.remedy.core.alarm.AlarmScheduler
import com.med.remedy.data.dao.MedicineDao
import com.med.remedy.data.dao.ReminderDao
import com.med.remedy.data.dao.ReminderOccurrenceDao
import com.med.remedy.data.entity.EMedicine
import com.med.remedy.data.entity.EReminder
import com.med.remedy.data.entity.EReminderOccurrence
import com.med.remedy.data.entity.ReminderStatus
import com.med.remedy.data.entity.shouldTakeToday
import com.med.remedy.data.model.DashboardProgress
import com.med.remedy.data.model.ReminderWithStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class ReminderRepository @Inject constructor(
    private val medicineDao: MedicineDao,
    private val reminderDao: ReminderDao,
    private val reminderOccurrenceDao: ReminderOccurrenceDao,
    private val alarmScheduler: AlarmScheduler
) {
    private val currentDateFlow = flow {
        while (true) {
            emit(LocalDate.now().toString())
            delay(60000.milliseconds)
        }
    }


    // ---------- Medicine ----------
    suspend fun getMedicineById(id: Long) = medicineDao.getById(id)
    suspend fun insertMedicine(medicine: EMedicine): Long = medicineDao.insert(medicine)
    suspend fun updateMedicine(medicine: EMedicine) = medicineDao.update(medicine)

    // ---------- Reminder ----------
    fun getAllWithMedicine() = reminderDao.getAllWithMedicine()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getEnabledReminders(): Flow<List<ReminderWithStatus>> {

        return currentDateFlow.flatMapLatest { today ->
            reminderDao.getEnabledReminders(today)
        }
    }

    suspend fun getReminderById(id: Long): EReminder? = reminderDao.getById(id)

    suspend fun insertReminder(reminder: EReminder): Long {
        val reminderId = reminderDao.insert(reminder)
        refreshTodayOccurrences()

        val reminder = reminderDao.getReminderWithMedicine(reminderId)

        alarmScheduler.schedule(
            reminderId = reminderId,
            medicineName = reminder.name,
            dosage = reminder.dosage,
            hour = reminder.hour,
            minute = reminder.minute,
            repeatMask = reminder.repeatMask
        )

        return reminderId
    }

    suspend fun updateReminder(reminder: EReminder) {
        val reminderWithMedicine = reminderDao.getReminderWithMedicine(reminder.id)
        val oldReminder = reminderDao.getById(reminder.id) ?: return
        reminderDao.update(reminder)
        Log.w("Update Reminder", "This function run")

        if (
            oldReminder.hour != reminder.hour ||
            oldReminder.minute != reminder.minute ||
            oldReminder.repeatMask != reminder.repeatMask
        ) {
            reminderOccurrenceDao.deleteOccurrence(
                reminder.id,
                LocalDate.now().toString()
            )
        }

        refreshTodayOccurrences()
        cleanupOrphanMedicines()
        alarmScheduler.reschedule(
            reminderId = reminder.id,
            medicineName = reminderWithMedicine.name,
            dosage = reminderWithMedicine.dosage,
            hour = reminder.hour,
            minute = reminder.minute,
            repeatMask = reminder.repeatMask,
        )
    }

    suspend fun deleteReminder(reminder: EReminder) {
        reminderDao.delete(reminder)
        refreshTodayOccurrences()
        alarmScheduler.cancel(reminder.id,)
        cleanupOrphanMedicines()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getDashboardProgress(): Flow<DashboardProgress> {
        return currentDateFlow.flatMapLatest { today ->
            combine(
                reminderOccurrenceDao.getTodayReminderCount(today),
                reminderOccurrenceDao.getCompletedTodayCount(today)
            ) { totalActive, completedToday ->
                DashboardProgress(
                    total = totalActive,
                    completed = completedToday
                )
            }
        }
    }

    suspend fun updateStatus(
        reminderId: Long,
        reminderStatus: ReminderStatus,
        snoozedUntil: Long? = null
    ) {
        reminderOccurrenceDao.updateStatus(
            reminderId = reminderId,
            today = LocalDate.now().toString(),
            status = reminderStatus,
            snoozedUntil = snoozedUntil
        )

        if (reminderStatus == ReminderStatus.TAKEN) {
            val reminder = reminderDao.getById(reminderId) ?: return

            if (reminder.repeatMask == 0) {
                deleteReminder(reminder)
            }
        }
    }

    suspend fun refreshTodayOccurrences() {

        val today = LocalDate.now()
        val todayString = today.toString()
        val todayDay = today.dayOfWeek

        val reminders = reminderDao.getEnabledReminders()

        val todayIds = reminderOccurrenceDao
            .getTodayReminderIds(todayString)
            .toHashSet()

        val validReminderIds = reminders
            .filter { shouldTakeToday(it.repeatMask, todayDay) }
            .map { it.id }
            .toHashSet()

        reminders.forEach { reminder ->

            if (
                reminder.id in validReminderIds && reminder.id !in todayIds
            ) {

                reminderOccurrenceDao.insert(
                    EReminderOccurrence(
                        reminderId = reminder.id,
                        date = todayString,
                        reminderStatus = ReminderStatus.PENDING
                    )
                )
            }
        }

        todayIds.forEach { reminderId ->

            if (reminderId !in validReminderIds) {
                reminderOccurrenceDao.deleteOccurrence(
                    reminderId,
                    todayString
                )
            }
        }
    }

    private suspend fun cleanupOrphanMedicines() {
        medicineDao.deleteOrphanMedicines()
    }

    fun getOnceAlarm(): Flow<ReminderWithStatus?> {
        return getEnabledReminders()
                .map { list ->
                    list.filter { it.repeatMask == 0 }
                        .minByOrNull { (it.hour * 60) + it.minute }
                }
    }
}