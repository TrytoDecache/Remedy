package com.med.remedy.ui.presentation.doc

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.med.remedy.ui.presentation.common.TopSectionBackButton

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit
) {
    // Wrap in a scrollable column so the user can swipe through the policy details
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopSectionBackButton(
            title = "Privacy Policy",
            description = "Consideration about your privacy",
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            iconContentDescription = null,
            iconOnTap = onBack
        )

        // Privacy Policy Content Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "At Remedy, we care deeply about protecting your health details. This policy outlines how your information is handled within the application.",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section 1
            Text(
                text = "1. Local-First Data Storage",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "All data regarding your medications, schedules, logs, and logs status are stored exclusively on your local device. We do not host your prescription information on external servers, and your personal medical footprint remains entirely in your control.",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Section 2
            Text(
                text = "2. No Third-Party Selling",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Because we do not sync or gather your private medication logs to a central cloud architecture, we do not, and cannot, sell, trade, or distribute your healthcare behaviors or medication histories to insurance companies, advertising agencies, or third-party networks.",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Section 3
            Text(
                text = "3. Device Permissions Used",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Remedy requires exact alarm scheduling and push notification permissions to sound reminders on time. These permissions are strictly functional and are not utilized to run analytics or monitor background behavior beyond the scope of triggering your medicine alerts.",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Section 4
            Text(
                text = "4. Data Deletion",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "You retain absolute ownership over your logs. Clearing the storage settings or uninstalling Remedy directly from your Android operating system system-level environment permanently wipes out all configurations and scheduling items instantly.",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}