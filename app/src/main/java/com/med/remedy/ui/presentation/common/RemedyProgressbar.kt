package com.med.remedy.ui.presentation.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.med.remedy.ui.theme.SpacialSubtleRed

@Composable
fun RemedyProgressBar(currentProgress: Float = 0.0f) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val density = LocalDensity.current

        val strokeWidthPx = with(density) { 6.dp.toPx() }       // Thicker wavy line
        val trackStrokeWidthPx = with(density) { 4.dp.toPx() }  // Slightly thinner track

        val animatedProgress by animateFloatAsState(
            targetValue = currentProgress,
            animationSpec = tween(
                durationMillis =  500,
                easing = FastOutSlowInEasing
            )
        )
        LinearWavyProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxWidth().size(14.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = SpacialSubtleRed,
            stroke = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
            trackStroke = Stroke(width = trackStrokeWidthPx, cap = StrokeCap.Round),
            gapSize = 0.dp,
            stopSize = 0.dp,
        )
    }
}