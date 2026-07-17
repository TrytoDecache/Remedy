package com.med.remedy.ui.presentation.reminder

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun PlusButton(
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isRotated by remember { mutableStateOf(false) }
    var shouldNavigate by remember { mutableStateOf(false) }
    val animationDuration = 150

    LaunchedEffect(shouldNavigate) {
        if (shouldNavigate) {
            delay(animationDuration.toLong().milliseconds)
            onTap()
            delay(animationDuration.toLong().milliseconds)
            shouldNavigate = false
            isRotated = false
        }
    }

    val rotationAngle by animateFloatAsState(
        targetValue = if (isRotated) 90f else 0f,
        animationSpec = tween(durationMillis = animationDuration),
        label = "plus_button_rotation"
    )

    Box(
        modifier = modifier
            .size(55.dp)
            .clip(CircleShape)
            .background(color = MaterialTheme.colorScheme.primary)
            .clickable{
                if (!shouldNavigate) {
                    isRotated = true
                    shouldNavigate = true
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .size(38.dp)
                .graphicsLayer(rotationZ = rotationAngle)
        )
    }
}