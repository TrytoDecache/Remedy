package com.med.remedy.ui.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.med.remedy.R
import com.med.remedy.ui.presentation.common.DestinationTopSection

@Composable
fun SettingsScreen(
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit
) {

    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
    val appVer = remember(context) { settingsViewModel.getAppVersion(context) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {

        SettingsContent(
            settings = settings,
            viewModel = settingsViewModel,
            appVersion = appVer,
            onPrivacyClick = onPrivacyClick,
            onTermsClick = onTermsClick,
        )

        DestinationTopSection(
            title = stringResource(R.string.settings_heading),
            description = stringResource(R.string.settings_desc)
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    settings: Settings,
    viewModel: SettingsViewModel,
    appVersion: String,
    onPrivacyClick: () -> Unit,
    onTermsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showVersion by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(
            top = 100.dp,
            bottom = 100.dp
        ),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        item {
            SettingsSection(title = "General") {

                AlarmSoundSetting(
                    selected = settings.alarmSound,
                    onSelected = viewModel::setAlarmSound
                )

                SettingSwitchRow(
                    title = "Vibration",
                    subtitle = "Vibrate while alarm is ringing",
                    checked = settings.vibrationEnabled,
                    onCheckedChange = viewModel::setVibration
                )
            }
        }

        item {
            SettingsSection(title = "About") {

                SettingClickableRow(
                    title = "Version",
                    subtitle = appVersion,
                    onClick = { showVersion = true }
                )

                SettingClickableRow(
                    title = "Privacy Policy",
                    onClick = onPrivacyClick

                )

                SettingClickableRow(
                    title = "Terms of Service",
                    onClick = onTermsClick
                )
            }
        }
    }

    if (showVersion) {
        AlertDialog(
            onDismissRequest = { showVersion = false },
            title = { Text("Version Info") },
            text = { Text("Version $appVersion") },
            confirmButton = {
                TextButton(onClick = { showVersion = false }) { Text("OK") }
            },
            dismissButton = {
                TextButton( onClick = { showVersion = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp, bottom = 10.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(0.5f)
        ) {
            Column {
                content()
            }
        }
    }
}