package com.med.remedy.core.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object WorkScheduler {

    fun scheduleMidnightRefresh(context: Context) {
        val now = LocalDateTime.now()
        val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay()

        val initDelay = Duration.between(now, nextMidnight).toMillis()

        val request = PeriodicWorkRequestBuilder<MidnightWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(
                initDelay,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "midnight_refresh_reminder_occurrence",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }
}