package com.med.remedy.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.med.remedy.R

val RemedyFontFamily = FontFamily(
    Font(R.font.dmsans_bold, weight = FontWeight.Bold),
    Font(R.font.dmsans_light, FontWeight.Light),
    Font(R.font.dmsans_medium, FontWeight.Medium),
    Font(R.font.dmsans_regular, FontWeight.Normal)
)

// Set of Material typography styles to start with
val Typography = Typography(
    // 1. Giant Alarm Screen Clock Display
    displayLarge = TextStyle(
        fontFamily = RemedyFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp
    ),

    // 2. Screen Headers (e.g., "Add Medication", "Today's Schedule")
    headlineLarge = TextStyle(
        fontFamily = RemedyFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),

    // 3. Main Medication Names in Cards (e.g., "Amoxicillin")
    titleLarge = TextStyle(
        fontFamily = RemedyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),

    // 4. Sub-headings inside cards (e.g., "Timings", "Instructions")
    titleMedium = TextStyle(
        fontFamily = RemedyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),

    // 5. Main Readable Text (Dosage information, list items)
    bodyLarge = TextStyle(
        fontFamily = RemedyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),

    // 6. Secondary / Muted Text (e.g., "Take with food", "Remaining pills: 20")
    bodyMedium = TextStyle(
        fontFamily = RemedyFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),

    // 7. Small Meta Data (e.g., Pill status tags like "Taken", "Skipped")
    labelMedium = TextStyle(
        fontFamily = RemedyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)