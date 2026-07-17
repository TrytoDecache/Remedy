package com.med.remedy.ui.presentation.reminder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.med.remedy.R
import com.med.remedy.data.model.ReminderWithMedicine
import com.med.remedy.ui.presentation.common.DestinationTopSection
import com.med.remedy.ui.theme.BasicWhite
import com.med.remedy.ui.theme.SpacialGreen
import com.med.remedy.ui.theme.SpacialRed
import com.med.remedy.ui.theme.Transparency

@Composable
fun ReminderScreen(
    onCreateReminder: (Long) -> Unit
) {

    val rvm: ReminderViewModel = hiltViewModel()
    val reminders by rvm.reminders.collectAsStateWithLifecycle(initialValue = emptyList())

    var reminderToDelete by remember { mutableStateOf<ReminderWithMedicine?>(null) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 140.dp, bottom = 180.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (reminders.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_reminder_text),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                item {
                    Text(
                        text = stringResource(R.string.scheduled_medicines),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                items(reminders, key = { it.reminderId }) { reminder ->

                    @Suppress("DEPRECATION")
                    val dismissState = rememberSwipeToDismissBoxState(
                        initialValue = SwipeToDismissBoxValue.Settled,
                        positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold,
                        confirmValueChange = { value ->
                            when (value) {
                                SwipeToDismissBoxValue.StartToEnd -> {
                                    onCreateReminder(reminder.reminderId)
                                    false
                                }

                                SwipeToDismissBoxValue.EndToStart -> {
                                    reminderToDelete = reminder
                                    true
                                }

                                SwipeToDismissBoxValue.Settled -> true
                            }
                        }
                    )

                    LaunchedEffect(reminderToDelete) {
                        if (
                            reminderToDelete?.reminderId != reminder.reminderId &&
                            dismissState.currentValue != SwipeToDismissBoxValue.Settled
                        ) {
                            dismissState.reset()
                        }
                    }

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val direction = dismissState.dismissDirection

                            val backgroundBrush = remember(direction) {
                                when (direction) {
                                    SwipeToDismissBoxValue.StartToEnd -> Brush.horizontalGradient(
                                        colors = listOf(SpacialGreen, SpacialGreen.copy(0.2f))
                                    )
                                    SwipeToDismissBoxValue.EndToStart -> Brush.horizontalGradient(
                                        colors = listOf(SpacialRed.copy(alpha = 0.2f), SpacialRed)
                                    )
                                    else -> Brush.linearGradient(listOf(Transparency, Transparency))
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(25.dp))
                                    .background(backgroundBrush)
                                    .padding(horizontal = 24.dp),
                                contentAlignment = when (direction) {
                                    SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                                    SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                                    else -> Alignment.Center
                                }
                            ) {
                                Icon(
                                    imageVector = if (direction == SwipeToDismissBoxValue.StartToEnd) Icons.Default.Edit else Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = BasicWhite
                                )
                            }
                        }
                    ) {
                        IndividualReminder(
                            reminder = reminder,
                            onToggleChange = { enabled -> rvm.toggleReminder(reminder, enabled) },
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                }
            }

        }

        if (reminderToDelete != null) {
            AlertDialog(
                onDismissRequest = { reminderToDelete = null },
                title = { Text(text = stringResource(R.string.del_reminder)) },
                text = { Text(text = stringResource(R.string.warning_for_del)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            rvm.deleteReminder(reminderToDelete!!)
                            reminderToDelete = null
                        }
                    ) { Text(text = stringResource(R.string.btn_delete)) }
                },
                dismissButton = { TextButton(onClick = { reminderToDelete = null }) { Text(text = stringResource(R.string.btn_cancel)) } }
            )
        }

        DestinationTopSection(
            title = stringResource(R.string.reminders),
            description = stringResource(R.string.reminders_desc)
        )

        PlusButton(
            onTap = { onCreateReminder(-1L) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 90.dp)
        )
    }
}