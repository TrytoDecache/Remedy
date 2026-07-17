package com.med.remedy.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.med.remedy.data.ReminderRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope

@HiltWorker
class MidnightWorker
@AssistedInject
constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repository: ReminderRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = coroutineScope {
        try {
            repository.refreshTodayOccurrences()
        } catch (e: Exception) {
            Result.failure()
        }
        Result.success()
    }
}