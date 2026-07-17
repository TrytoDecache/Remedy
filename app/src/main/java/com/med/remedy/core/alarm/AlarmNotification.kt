@file:Suppress("FullScreenIntentPolicy")
package com.med.remedy.core.alarm

/*
The AlarmNotification.kt is the heart for sending the notifications to the notification bars.
It contains : class AlarmNotification,  object AlarmNotificationObj
 */

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.med.remedy.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

// DEFINITION: class impl for App Notifications for alarm
@Singleton
class AlarmNotification @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    // DEFINITION: main function for notification when alarm rings.
    fun notify(
        reminderId: Long,
        medicineName: String,
        dosage: String,
        hour: Int,
        minute: Int,
        repeatMask: Int
    ) {
        val combinedTitle = "$medicineName • $dosage"
        val fullScreenIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("reminder_id", reminderId)
            putExtra("medicine_name", medicineName)
            putExtra("dosage", dosage)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            reminderId.toInt(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val takenPendingIntent = actionPendingIntent(
            reminderId = reminderId,
            requestCode = reminderId.toInt() * 10 + 1,
            action = ACTION_TAKEN
        )
        val snoozePendingIntent = actionPendingIntent(
            reminderId = reminderId,
            requestCode = reminderId.toInt() * 10 + 2,
            action = ACTION_SNOOZE,
            medicineName = medicineName,
            dosage = dosage,
            hour = hour,
            minute = minute,
            repeatMask = repeatMask
        )
        val notification = NotificationCompat.Builder(context, AlarmNotificationObj.CHANNEL_ID)
            .setSmallIcon(R.drawable.outline_pill_notify)
            .setContentTitle(combinedTitle)
            .setContentText("Time to take your medicine.")
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setSound(null)
            .setAutoCancel(false)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .addAction(R.drawable.baseline_done, "Taken", takenPendingIntent)
            .addAction(R.drawable.outline_snooze, "Snooze 10 min", snoozePendingIntent)
            .build()

        notification.flags = notification.flags or Notification.FLAG_INSISTENT

        if (
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) { return }
        NotificationManagerCompat.from(context).notify(reminderId.toInt(), notification)
    }

    // DEFINITION: to cancel the notification after action
    fun cancel(reminderId: Long) {
        NotificationManagerCompat.from(context).cancel(reminderId.toInt())
    }

    // DEFINITION: sets the notification pending intent
    private fun actionPendingIntent(
        reminderId: Long,
        requestCode: Int,
        action: String,
        medicineName: String? = null,
        dosage: String? = null,
        hour: Int? = null,
        minute: Int? = null,
        repeatMask: Int? = null
    ): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            this.action = action
            putExtra("reminder_id", reminderId)
            medicineName?.let { putExtra("medicine_name", it) }
            dosage?.let { putExtra("dosage", it) }
            hour?.let { putExtra("hour", it) }
            minute?.let { putExtra("minute", it) }
            repeatMask?.let { putExtra("repeat_mask", it) }
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    // DEFINITION: companion to register in AndroidManifest.xml as intent filter
    companion object {
        const val ACTION_TAKEN = "com.med.remedy.ACTION_TAKEN"
        const val ACTION_SNOOZE = "com.med.remedy.ACTION_SNOOZE"
    }
}


// DEFINITION: creates the notification channel
object AlarmNotificationObj {
    const val CHANNEL_ID = "medicine_reminder"

    // DEFINITION: impl for channel creation [ Application -> onCreate() ]
    fun createChannel(context: Context) {
        val name = "Medicine Reminder"
        val descriptionText = "Medicine Reminder Alarms"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val manager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            vibrationPattern = longArrayOf(0, 500, 250, 500)
            enableVibration(true)
            setBypassDnd(true)
            setSound(null, null)
        }

        manager.createNotificationChannel(channel)
    }
}