package com.med.remedy.ui.presentation.create

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.med.remedy.R
import com.med.remedy.data.entity.RepeatDays
import com.med.remedy.ui.presentation.common.RemedyField
import com.med.remedy.ui.presentation.common.RemedyRepeat
import com.med.remedy.ui.presentation.common.RemedyTimeField
import com.med.remedy.ui.presentation.common.TopSectionBackButton
import com.med.remedy.ui.theme.BasicBlack
import com.med.remedy.ui.theme.BasicWhite
import com.med.remedy.ui.theme.NullColor
import com.med.remedy.ui.theme.StandardMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReminderScreen(
    reminderId: Long,
    onBack: () -> Unit
) {
    val crvm: CreateReminderViewModel = hiltViewModel()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        crvm.event.collect { event ->
            when (event) {
                is CreateReminderEvent.Success -> { onBack() }
                is CreateReminderEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    LaunchedEffect(reminderId) {
        if (reminderId != -1L) {
            crvm.loadReminder(reminderId)
        } else {
            crvm.discardChanges()
        }
    }

    var showTimePicker by remember { mutableStateOf(false) }
    var showRepeatSheet by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(
        initialHour = crvm.hour,
        initialMinute = crvm.minute,
        is24Hour = false
    )

    LaunchedEffect(crvm.hour, crvm.minute) {
        timePickerState.hour = crvm.hour
        timePickerState.minute = crvm.minute
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            contentPadding = PaddingValues(top = 100.dp, bottom = 100.dp)
        ) {

            item {
                RemedyField(
                    label = stringResource(R.string.label_medicine),
                    placeholder = stringResource(R.string.placeholder_medicine),
                    state = crvm.medicineName,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }

            item {
                RemedyField(
                    label = stringResource(R.string.label_dosage),
                    placeholder = stringResource(R.string.placeholder_dosage),
                    state = crvm.dosage,
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.pill_icon),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }

            item {
                RemedyField(
                    label = stringResource(R.string.label_desc),
                    placeholder = stringResource(R.string.placeholder_desc),
                    state = crvm.description,
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Notes,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }

            item {
                RemedyTimeField(
                    time = crvm.formattedTime,
                    onClick = { focusManager.clearFocus(); showTimePicker = true })
            }

            item {
                RemedyRepeat(repeatText = crvm.repeatText, onClick = { showRepeatSheet = true })
            }

            item {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .height(50.dp)
                        .width(80.dp),
                    onClick = {
                        if (reminderId == -1L) {
                            crvm.saveReminder()
                        } else {
                            crvm.updateReminder(reminderId)
                        }
                    }
                ) {
                    Text(
                        text = if (reminderId == -1L) stringResource(R.string.btn_save) else stringResource(R.string.btn_update),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        TopSectionBackButton(
            title =
                if (reminderId == -1L)
                    stringResource(R.string.heading_create)
                else
                    stringResource(R.string.heading_edit) ,
            description =
                if (reminderId == -1L)
                    stringResource(R.string.heading_create_desc)
                else stringResource(R.string.heading_edit_desc),
            icon = Icons.AutoMirrored.Default.ArrowBack,
            iconContentDescription = null,
            iconOnTap = {
                crvm.discardChanges()
                onBack()
            }
        )

        // time picker composable ------------------

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            // ---------- Scrim ----------
            AnimatedVisibility(
                visible = showTimePicker,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BasicBlack.copy(alpha = 0.35f))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            showTimePicker = false
                        }
                )
            }

            // ---------- Time Picker ----------
            AnimatedVisibility(
                visible = showTimePicker,
                modifier = Modifier.align(Alignment.BottomCenter),
                enter = slideInVertically(
                    initialOffsetY = { it }
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { it }
                ) + fadeOut()
            ) {
                if (showTimePicker) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                        color = MaterialTheme.colorScheme.background,
                        tonalElevation = 8.dp
                    ) {

                        Column(
                            modifier = Modifier
                                .padding(20.dp)
                                .navigationBarsPadding()
                        ) {

                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .width(40.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(MaterialTheme.colorScheme.outlineVariant)
                            )

                            Spacer(Modifier.height(20.dp))

                            Text(
                                text = stringResource(R.string.w_set_time),
                                style = MaterialTheme.typography.titleLarge
                            )

                            Spacer(Modifier.height(12.dp))

                            TimePicker(
                                state = timePickerState,
                                modifier = Modifier.fillMaxWidth(),
                                colors = TimePickerDefaults.colors(
                                    clockDialColor = MaterialTheme.colorScheme.surfaceVariant,
                                    selectorColor = MaterialTheme.colorScheme.primary,
                                    containerColor = MaterialTheme.colorScheme.surface,

                                    periodSelectorBorderColor = MaterialTheme.colorScheme.outline,
                                    periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                                    periodSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,

                                    periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                                    periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,

                                    timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                                    timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,

                                    timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                                    timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurface,

                                    clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                                    clockDialUnselectedContentColor = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            Spacer(Modifier.height(20.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                FilledTonalButton(
                                    colors = ButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(0.4f),
                                        contentColor = BasicWhite,
                                        disabledContainerColor = StandardMuted,
                                        disabledContentColor = NullColor
                                    ),
                                    onClick = { showTimePicker = false }
                                ) { Text(text = stringResource(R.string.btn_cancel)) }
                                FilledTonalButton(
                                    onClick = {
                                        crvm.updateTime(timePickerState.hour, timePickerState.minute)
                                        showTimePicker = false
                                    },
                                    colors = ButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(0.4f),
                                        contentColor = BasicWhite,
                                        disabledContainerColor = StandardMuted,
                                        disabledContentColor = NullColor
                                    ),
                                ) { Text(text = stringResource(R.string.btn_done)) }
                            }
                        }
                    }
                }
            }
        }

        // Repeat dialog composable ------------------
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            // Dim background
            AnimatedVisibility(
                visible = showRepeatSheet,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BasicBlack.copy(alpha = 0.35f))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            showRepeatSheet = false
                        }
                )
            }
            // Bottom sheet
            AnimatedVisibility(
                visible = showRepeatSheet,
                modifier = Modifier.align(Alignment.BottomCenter),
                enter = slideInVertically(
                    initialOffsetY = { it }
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { it }
                ) + fadeOut()
            ) {
                if (showRepeatSheet) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                        color = MaterialTheme.colorScheme.background,
                        tonalElevation = 8.dp
                    ) {

                        Column(
                            modifier = Modifier
                                .padding(20.dp)
                                .navigationBarsPadding()
                        ) {

                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .width(40.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(MaterialTheme.colorScheme.onSurface)
                            )

                            Spacer(Modifier.height(20.dp))

                            Text(
                                text = stringResource(R.string.w_repeat),
                                style = MaterialTheme.typography.titleLarge
                            )

                            Spacer(Modifier.height(20.dp))

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                val days = listOf(
                                    "Mon" to RepeatDays.MONDAY,
                                    "Tue" to RepeatDays.TUESDAY,
                                    "Wed" to RepeatDays.WEDNESDAY,
                                    "Thu" to RepeatDays.THURSDAY,
                                    "Fri" to RepeatDays.FRIDAY,
                                    "Sat" to RepeatDays.SATURDAY,
                                    "Sun" to RepeatDays.SUNDAY
                                )

                                days.forEach { (name, flag) ->

                                    FilterChip(
                                        selected = crvm.repeatMask and flag != 0,
                                        onClick = {
                                            val newMask =
                                                if (crvm.repeatMask and flag != 0)
                                                    crvm.repeatMask and flag.inv()
                                                else
                                                    crvm.repeatMask or flag

                                            crvm.updateRepeat(newMask)
                                        },
                                        label = {
                                            Text(name)
                                        },
                                        colors = SelectableChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = BasicWhite,
                                            selectedLeadingIconColor = NullColor,
                                            selectedTrailingIconColor = NullColor,
                                            containerColor = MaterialTheme.colorScheme.surface,
                                            labelColor = BasicWhite,
                                            leadingIconColor = NullColor,
                                            trailingIconColor = NullColor,
                                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                            disabledLabelColor = NullColor,
                                            disabledLeadingIconColor = NullColor,
                                            disabledTrailingIconColor = NullColor,
                                            disabledSelectedContainerColor = NullColor,
                                        )
                                    )
                                }
                            }
                            Spacer(Modifier.height(14.dp))
                        }
                    }
                }
            }
        }
    }
}
