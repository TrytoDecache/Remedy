package com.med.remedy.ui.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.med.remedy.R
import com.med.remedy.data.entity.ReminderStatus
import com.med.remedy.data.model.formattedDisplayTime
import com.med.remedy.ui.presentation.common.DestinationTopSection
import com.med.remedy.ui.presentation.common.RemedyProgressBar
import com.med.remedy.ui.theme.SpacialSubtleRed
import com.med.remedy.ui.theme.SpacialYellow
import java.time.LocalTime

private val DashboardContentPadding = PaddingValues(top = 120.dp, bottom = 120.dp)

@Composable
fun DashboardScreen(
    onViewClick: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val dashboardvm: DashboardViewModel = hiltViewModel()
    val nextMedicine by dashboardvm.nextMedicine.collectAsState()
    val upcoming by dashboardvm.upcomingMedicines.collectAsStateWithLifecycle()
    val onceRem by dashboardvm.onceReminder.collectAsStateWithLifecycle()
    val state by dashboardvm.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        val greetingData = remember { getGreetingAndQuote() }
        val headingTitle = greetingData.first
        val headingDescription = stringResource(id = greetingData.second)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = DashboardContentPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item(key = "once_card") {
                if (onceRem != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                            .clip(RoundedCornerShape(30))
                            .border(1.dp, SpacialSubtleRed, RoundedCornerShape(30))
                            .background(color = Color.Black)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${onceRem!!.name} • ${onceRem!!.dosage}", // Safe to access directly now
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                                Text(
//                                    text = "at: $formattedHour:$formattedMinute",
                                    text = onceRem!!.formattedDisplayTime(),
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            // Status Badge
                            val status = onceRem!!.reminderStatus
                            Text(
                                text = status.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (status) {
                                    ReminderStatus.TAKEN -> Color.Green
                                    ReminderStatus.SNOOZED -> SpacialYellow
                                    else -> MaterialTheme.colorScheme.primary
                                },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.White.copy(alpha = 0.1f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            item(key = "medicine_card") {
                if (nextMedicine != null) {
                    MedicineCard(
                        reminder = nextMedicine!!,
                        onTakenClick = {
                            dashboardvm.updateStatus(nextMedicine!!.reminderId, ReminderStatus.TAKEN)
                        }
                    )
                } else {
                    BlankMedicineCard(onAdd = onAddClick)
                }
            }

            item {
                RemedyProgressBar(state.progress)
            }

            item(key = "medicine_schedule") {
                MedicineSchedule(
                    medicines = upcoming,
                    onViewClick = onViewClick,
                )
            }
        }

        DestinationTopSection(
            title = headingTitle,
            description = headingDescription,
        )
    }
}

fun getGreetingAndQuote(): Pair<String, Int> {
    return when (LocalTime.now().hour) {
        in 5..11 -> "Fresh morning!" to R.string.morning_quote
        in 12..14 -> "Stay Hydrated!" to R.string.noon_quote
        in 15..16 -> "Stay Energized!" to R.string.afternoon_quote
        in 17..19 -> "Wind down well!" to R.string.evening_quote
        else -> "Sleep in time!" to R.string.night_quote
    }
}