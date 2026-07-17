package com.med.remedy

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.med.remedy.core.worker.WorkScheduler
import com.med.remedy.ui.theme.RemedyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val npl = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        Log.d("Permission", "Permission Granted = $granted")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        splash.setOnExitAnimationListener { splashView ->
                splashView.iconView.animate()
                    .scaleX(1.3f)
                    .scaleY(1.3f)
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction {
                        splashView.remove()
                    }
                    .start()
        }
        super.onCreate(savedInstanceState)

        requestAlarmPermissions(this, packageName)
        requestNotificationPermission(this, npl)
        requestFullScreenIntentPermission(this, packageName)

        WorkScheduler.scheduleMidnightRefresh(this)
        enableEdgeToEdge()

        setContent {
            RemedyTheme {
                AppRoot()
            }
        }
    }
}

@SuppressLint("BatteryLife")
private fun requestAlarmPermissions(context: Context, packageName: String) {
    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    if (!pm.isIgnoringBatteryOptimizations(packageName)) {

        val intent = Intent(
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
        ).apply {
            data = "package:$packageName".toUri()
        }

        context.startActivity(intent)
    }

    // Android 12 +
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (!alarmManager.canScheduleExactAlarms()) {

        val intent = Intent(
            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
        ).apply {
            data = "package:$packageName".toUri()
        }
        context.startActivity(intent)
    }
}

private fun requestNotificationPermission(
    context: Context,
    notificationPermissionLauncher: ActivityResultLauncher<String>
) {
    if (
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        notificationPermissionLauncher.launch(
            Manifest.permission.POST_NOTIFICATIONS
        )
    }
}

private fun requestFullScreenIntentPermission(
    context: Context,
    packageName: String
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        return
    }

    val notificationManager =
        context.getSystemService(NotificationManager::class.java)

    if (!notificationManager.canUseFullScreenIntent()) {
        val intent = Intent(
            Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT
        ).apply {
            data = "package:$packageName".toUri()
        }
        context.startActivity(intent)
    }
}