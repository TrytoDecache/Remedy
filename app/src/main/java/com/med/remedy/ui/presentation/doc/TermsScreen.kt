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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.med.remedy.ui.presentation.common.TopSectionBackButton

@Composable
fun TermsScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopSectionBackButton(
            title = "Terms and Conditions",
            description = "About the t&c of Remedy",
            icon = Icons.AutoMirrored.Rounded.ArrowBack,
            iconContentDescription = null,
            iconOnTap = onBack,
        )

        // 2. Terms & Conditions Content Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Welcome to Remedy. By downloading and using this application, you agree to comply with and be bound by the following terms.",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section 1
            Text(
                text = "1. Medical Disclaimer",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Remedy is a digital tool designed to help you track your medication schedule. It is NOT a medical device, and it does NOT provide medical advice, diagnosis, or treatment. Always consult with your doctor or healthcare professional before altering your prescription routine.",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Section 2
            Text(
                text = "2. User Responsibility",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "You are solely responsible for inputting accurate medication names, dosages, and interval timings. Remedy relies entirely on the data you provide. Ensure your device notifications and alarm permissions are fully enabled to allow schedules to run properly.",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Section 3
            Text(
                text = "3. Limitation of Liability",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Remedy, its developers, and affiliates are not liable for any missed doses, incorrect tracking, device malfunctions, battery optimizations closing the background processes, or health outcomes resulting from the use or inability to use this app.",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Section 4
            Text(
                text = "4. Privacy and Data",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your health data and reminder details are stored safely on your physical device. We recommend securing your device with biometric or passcode locks to keep your schedule private from unauthorized access.",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}