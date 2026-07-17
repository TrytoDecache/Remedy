package com.med.remedy.ui.presentation.dashboard

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.med.remedy.R
import com.med.remedy.data.entity.ReminderStatus
import com.med.remedy.data.model.ReminderWithStatus
import com.med.remedy.data.model.formattedDisplayTime
import com.med.remedy.ui.theme.BasicLightGray
import com.med.remedy.ui.theme.BasicWhite
import com.med.remedy.ui.theme.SpacialGreen
import com.med.remedy.ui.theme.SpacialSubtleAsh
import com.med.remedy.ui.theme.SpacialYellow

@Composable
fun MedicineSchedule(
    medicines: List<ReminderWithStatus>,
    onViewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.upcoming_medicines),
                    fontSize = 16.sp,
                    color = BasicWhite,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(R.string.view_all),
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(onClick = onViewClick)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (medicines.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_upcoming),
                    color = BasicLightGray,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    medicines.forEach { medicine ->
                        MedicineItem(
                            name = medicine.name,
                            formattedTime = medicine.formattedDisplayTime(),
                            status = medicine.reminderStatus
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MedicineItem(
    name: String,
    formattedTime: String,
    status: ReminderStatus
) {
    val (statusText, statusColor) = when (status) {
        ReminderStatus.PENDING -> {
            stringResource(R.string.pending) to MaterialTheme.colorScheme.primary
        }

        ReminderStatus.SNOOZED -> {
            stringResource(R.string.snoozed) to SpacialYellow
        }

        ReminderStatus.TAKEN -> {
            stringResource(R.string.taken) to SpacialGreen
        }
    }

    Box(
        modifier = Modifier
            .height(70.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(25.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .animateContentSize(animationSpec = tween(durationMillis = 400)),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .width(85.dp)
                    .height(30.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WbSunny,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = formattedTime,
                        fontSize = 12.sp,
                        color = BasicWhite
                    )
                }
            }

            Spacer(Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    color = BasicWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(25.dp))
                    .background(SpacialSubtleAsh)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp
                )
            }
        }
    }
}