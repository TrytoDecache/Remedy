package com.med.remedy.core.alarm
/*
This AlarmEntryPoint.kt provides essential classes and act as singleton
 */

import com.med.remedy.data.ReminderRepository
import com.med.remedy.ui.presentation.settings.SettingsManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


// DEFINITION: Alarm entrypoint which connects itself with others and do centralization
@EntryPoint
@InstallIn(SingletonComponent::class)
interface AlarmEntryPoint {
    fun alarmScheduler() : AlarmScheduler
    fun reminderRepository() : ReminderRepository
    fun alarmActionHandler(): AlarmActionHandler
    fun alarmNotification() : AlarmNotification
    fun settingsManager() : SettingsManager
}