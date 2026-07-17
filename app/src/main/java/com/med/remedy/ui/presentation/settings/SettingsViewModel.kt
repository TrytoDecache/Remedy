package com.med.remedy.ui.presentation.settings

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {
    val settings = settingsManager.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Settings()
    )

    fun setAlarmSound(sound: String) {
        viewModelScope.launch {
            settingsManager.setAlarmSound(sound)
        }
    }

    fun setVibration(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setVibrationEnabled(enabled)
        }
    }

    @Suppress("DEPRECATION")
    fun getAppVersion(context: Context): String {
        return try {
            val packageManager = context.packageManager
            val packageName = context.packageName

            val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))

            packageInfo.versionName ?: "Unknown"
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }
}