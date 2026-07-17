package com.med.remedy.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RemedyColorScheme = darkColorScheme(
    primary = StandardRed,
    onPrimary = BasicWhite,
    secondary = BasicWhite,
    onSecondary = BaseDarkObsidian,
    tertiary = BaseLightObsidian,
    onTertiary = BasicWhite,
    background = BaseDarkObsidian,
    onBackground = BasicWhite,
    surface = StandardSurfaceDark,
    onSurface = BasicWhite,
    surfaceVariant = StandardSlate,
    onSurfaceVariant = StandardMuted
)

@Composable
fun RemedyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RemedyColorScheme,
        typography = Typography,
        content = content
    )
}