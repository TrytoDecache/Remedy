@file:SuppressLint("FullScreenIntentPolicy")
package com.med.remedy.core.alarm

/*
The AlarmReceiver is the core receiver for the alarm
 */

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.med.remedy.R
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// DEFINITION: the core impl for Alarm Receiver
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val reminderId = intent.getLongExtra("reminder_id", -1L)
        val medicineName = intent.getStringExtra("medicine_name").orEmpty()
        val dosage = intent.getStringExtra("dosage").orEmpty()
        val hour = intent.getIntExtra("hour", 0)
        val minute = intent.getIntExtra("minute", 0)
        val repeatMask = intent.getIntExtra("repeat_mask", 0)

        if (reminderId == -1L) return

        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            AlarmEntryPoint::class.java
        )

        val notifier = entryPoint.alarmNotification()
        val actionHandler = entryPoint.alarmActionHandler()

        when (action) {
            AlarmNotification.ACTION_TAKEN -> {
                AlarmPlayer.stop()
                notifier.cancel(reminderId)
                CoroutineScope(Dispatchers.IO).launch {
                    actionHandler.handleComplete(reminderId)
                }
            }

            AlarmNotification.ACTION_SNOOZE -> {
                AlarmPlayer.stop()
                notifier.cancel(reminderId)
                CoroutineScope(Dispatchers.IO).launch {
                    actionHandler.handleSnooze(reminderId)
                }
            }

            else -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val settings = entryPoint.settingsManager().settings.first()
                    AlarmPlayer.play(
                        context,
                        reminderId,
                        AlarmSound.getResId(settings.alarmSound),
                        settings.vibrationEnabled
                    )
                }

                notifier.notify(
                    reminderId = reminderId,
                    medicineName = medicineName,
                    dosage = dosage,
                    hour = hour,
                    minute = minute,
                    repeatMask = repeatMask
                )

                if (repeatMask != 0) {
                    try {
                        entryPoint
                            .alarmScheduler()
                            .schedule(reminderId, medicineName, dosage, hour, minute, repeatMask)
                    } catch (_: Exception) { }
                }
            }
        }
    }
}

object AlarmSound {
    fun getResId(sound: String): Int {
        return when (sound) {
            "Calm" -> R.raw.ringtone_one
            "Digital" -> R.raw.ringtone_two
            "Marimba" -> R.raw.ringtone_three
            "Nature" -> R.raw.ringtone_four
            else -> R.raw.ringtone_two
        }
    }
}