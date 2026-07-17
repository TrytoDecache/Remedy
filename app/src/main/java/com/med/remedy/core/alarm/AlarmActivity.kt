package com.med.remedy.core.alarm

/*
The AlarmActivity.kt contains the implementation for the screen that is spawned during the
alarm ringing when phone turn off or app closed etc.
 */

import android.app.KeyguardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.Snooze
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import com.med.remedy.ui.theme.BasicBlack
import com.med.remedy.ui.theme.BasicWhite
import com.med.remedy.ui.theme.RemedyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

// DEFINITION: Implementation for pop up Alarm Screen
@AndroidEntryPoint
class AlarmActivity : ComponentActivity() {

    @Inject
    lateinit var alarmActionHandler: AlarmActionHandler

    private var reminderId by mutableLongStateOf(-1L)
    private var medicineName by mutableStateOf("")
    private var dosage by mutableStateOf("")

    // DEFINITION: onCreate parent function for AlarmActivity class
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        keyguardManager.requestDismissKeyguard(this, null)
        setShowWhenLocked(true) // Screen on if locked
        setTurnScreenOn(true) // Screen on


        readExtras(intent)
        observeResolvedReminders()

        setContent {
            RemedyTheme {

                // The core composable for alarm activity pop up screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BasicBlack)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        modifier = Modifier.size(96.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Medication,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(28.dp))

                    Text(
                        text = "Remedy Reminder",
                        style = MaterialTheme.typography.headlineSmall,
                        color = BasicWhite
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = medicineName,
                        style = MaterialTheme.typography.headlineMedium,
                        color = BasicWhite,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = dosage,
                            modifier = Modifier.padding(
                                horizontal = 18.dp,
                                vertical = 8.dp
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(Modifier.height(40.dp))

                    Text(
                        text = "It's time to take your medicine",
                        color = BasicWhite.copy(alpha = 0.75f),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(40.dp))

                    Button(
                        onClick = { onTakenClicked() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Rounded.Check,
                            contentDescription = null
                        )

                        Spacer(Modifier.width(8.dp))

                        Text("Taken")
                    }

                    Spacer(Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { onSnoozeClicked()},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Rounded.Snooze,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            "Snooze for 10 min",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    // Dispatches to all
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        readExtras(intent)
    }

    // Auto close this activity
    private fun observeResolvedReminders() {
        lifecycleScope.launch {
            alarmActionHandler.resolvedReminderId.collect { resolvedId ->
                if (resolvedId == reminderId) {
                    finish()
                }
            }
        }
    }


    // Collecting intents then expose
    private fun readExtras(intent: Intent?) {
        reminderId = intent?.getLongExtra("reminder_id", -1L) ?: -1L
        medicineName = intent?.getStringExtra("medicine_name").orEmpty()
        dosage = intent?.getStringExtra("dosage").orEmpty()

        if (reminderId == -1L) {
            finish()
        }
    }

    // If taken button clicked then stop alarm and dismiss screen
    private fun onTakenClicked() {
        val idAtClickTime = reminderId

        AlarmPlayer.stop()
        NotificationManagerCompat.from(this).cancel(reminderId.toInt())
        Log.d("AlarmActivity", "Taken clicked for ID: $reminderId")

        lifecycleScope.launch(Dispatchers.IO) {
            alarmActionHandler.handleComplete(idAtClickTime)
        }
        finish()
    }

    // Snooze button handle impl for pop up alarm screen
    private fun onSnoozeClicked() {
        val idAtClickTime = reminderId

        AlarmPlayer.stop()
        NotificationManagerCompat.from(this).cancel(reminderId.toInt())
        Log.d("AlarmActivity", "Snooze clicked for ID: $reminderId")

        lifecycleScope.launch(Dispatchers.IO) {
            alarmActionHandler.handleSnooze(idAtClickTime)
        }
        finish()
    }
}