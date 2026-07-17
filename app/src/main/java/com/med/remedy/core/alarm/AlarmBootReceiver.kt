package com.med.remedy.core.alarm
/*
The AlarmBootreceiver.kt contains the impl for the boot receiver that alarmService sends at time
 */

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// DEFINITION: The boot receiver : BroadcastReceiver() that receives boot completed response
class AlarmBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val appContext = context.applicationContext
                    val entryPoint = EntryPointAccessors.fromApplication(appContext, AlarmEntryPoint::class.java)
                    val repository = entryPoint.reminderRepository()
                    // Use flow helper or direct fetch
                    val activeReminders = repository.getEnabledReminders().first()
                    repository.refreshTodayOccurrences()

                    activeReminders.forEach { reminder ->
                        entryPoint.alarmScheduler().schedule(
                            reminderId = reminder.reminderId,
                            medicineName = reminder.name,
                            dosage = reminder.dosage,
                            hour = reminder.hour,
                            minute = reminder.minute,
                            repeatMask = reminder.repeatMask
                        )
                    }
                } catch (e: Exception) {
                    Log.e("Alarm Boot Receiver", "Failed to restore alarms", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}