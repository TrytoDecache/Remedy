package com.med.remedy.ui.presentation.reminder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.med.remedy.data.entity.RepeatDays
import com.med.remedy.data.model.ReminderWithMedicine
import com.med.remedy.data.model.formattedTime
import com.med.remedy.data.model.hasDay
import com.med.remedy.ui.theme.BasicWhite

private val WeekDaysLabels = listOf("S", "M", "T", "W", "T", "F", "S")
private val WeekDayBits = listOf(
    RepeatDays.SUNDAY, RepeatDays.MONDAY, RepeatDays.TUESDAY, RepeatDays.WEDNESDAY,
    RepeatDays.THURSDAY, RepeatDays.FRIDAY, RepeatDays.SATURDAY
)

@Composable
fun IndividualReminder(
    reminder: ReminderWithMedicine,
    onToggleChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val contentAlpha = if (reminder.isEnabled) 1f else 0.5f

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            // Header: icon + name/dosage + switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(BasicWhite.copy(alpha = contentAlpha)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Medication,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = contentAlpha),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reminder.formattedTime(),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = contentAlpha),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = "${reminder.name} - ${reminder.dosage}",
//                        text = "${reminder.name} • ${reminder.dosage}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.width(8.dp))

                Switch(
                    checked = reminder.isEnabled,
                    onCheckedChange = onToggleChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(Modifier.height(14.dp))

            // Footer: day chips + time badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    WeekDaysLabels.forEachIndexed { index, day ->

                        val bit = WeekDayBits[index]
                        val isActive = reminder.hasDay(bit)

                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isActive)
                                        MaterialTheme.colorScheme.primary.copy(alpha = contentAlpha)
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = contentAlpha * 0.6f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isActive)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha)
                            )
                        }
                    }
                }
            }
        }
    }
}