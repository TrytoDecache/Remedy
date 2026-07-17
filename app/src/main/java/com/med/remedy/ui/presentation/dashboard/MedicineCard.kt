package com.med.remedy.ui.presentation.dashboard

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.med.remedy.R
import com.med.remedy.data.model.ReminderWithStatus
import com.med.remedy.data.model.formattedDisplayTime
import com.med.remedy.data.model.timeLeft
import com.med.remedy.ui.presentation.reminder.PlusButton
import com.med.remedy.ui.theme.BaseDarkObsidian
import com.med.remedy.ui.theme.BaseLightObsidian
import com.med.remedy.ui.theme.BasicBlack
import com.med.remedy.ui.theme.BasicLightGray
import com.med.remedy.ui.theme.SpacialBlue
import com.med.remedy.ui.theme.StandardSlate
import kotlinx.coroutines.delay
import java.time.LocalTime
import kotlin.time.Duration.Companion.seconds

private val CardGradient = Brush.linearGradient(
    listOf(
        BaseDarkObsidian,
        BaseLightObsidian,
    )
)

private val CardShape = RoundedCornerShape(24.dp)

@Composable
fun MedicineCard(
    reminder: ReminderWithStatus,
    onTakenClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var now by remember { mutableStateOf(LocalTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = LocalTime.now()
            delay(1.seconds)
        }
    }

    val labelText = reminder.timeLeft(now)?.let {
        "%02d:%02d".format(it.minutes, it.seconds)
    } ?: "0s left"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 8.dp,
                shape = CardShape,
                clip = false,
                ambientColor = BasicBlack.copy(alpha = 0.20f),
                spotColor = BasicBlack.copy(alpha = 0.20f)
            )
            .clip(CardShape)
            .background(CardGradient)
            .border(1.dp, StandardSlate.copy(alpha = 0.5f), CardShape)
            .padding(20.dp)
            .animateContentSize(animationSpec = tween(durationMillis = 400))
    ) {

        Column {

            Row(
                verticalAlignment = Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${ stringResource(R.string.next_medicine).uppercase() } in $labelText",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = reminder.name,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 26.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = .12f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Medication,
                            contentDescription = null,
                            tint = SpacialBlue,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(Modifier.width(6.dp))

                        Text(
                            text = reminder.dosage,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = .05f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.medimg),
                        contentDescription = null,
                        modifier = Modifier.size(72.dp)
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = .06f)
            )

            Spacer(Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = .14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {

                        Text(
                            text = reminder.formattedDisplayTime(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(2.dp))

                        Text(
                            text = reminder.description,
                            color = BasicLightGray,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Button(
                    onClick = onTakenClick,
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 1.dp
                    ),
                    modifier = Modifier.height(46.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = stringResource(R.string.btn_taken),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun BlankMedicineCard(
    onAdd: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(CardShape)
            .background(CardGradient)
            .border(1.dp, StandardSlate, CardShape)
            .padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column (
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = stringResource(R.string.no_upcoming),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            PlusButton(onTap = onAdd)
        }
    }
}