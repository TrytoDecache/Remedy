package com.med.remedy.ui.presentation.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore by preferencesDataStore("app_settings")

@Singleton
class SettingsManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private object Keys {
        val AlarmSound = stringPreferencesKey("alarm_sound")
        val VibrationEnabled = booleanPreferencesKey("vibration_enabled")
    }

    val settings: Flow<Settings> =
        context.settingsDataStore.data.map { preference ->
            Settings(
                alarmSound = preference[Keys.AlarmSound] ?: "Calm",
                vibrationEnabled = preference[Keys.VibrationEnabled] ?: true
            )
        }

    suspend fun setAlarmSound(sound: String) {
        context.settingsDataStore.edit { preference ->
            preference[Keys.AlarmSound] = sound
        }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preference ->
            preference[Keys.VibrationEnabled] = enabled
        }
    }
}