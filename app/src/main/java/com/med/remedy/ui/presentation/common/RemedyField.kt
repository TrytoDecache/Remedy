package com.med.remedy.ui.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.med.remedy.ui.theme.SpacialRed

@Composable
fun RemedyField(
    label: String,
    placeholder: String,
    state: TextFieldState,
    icon: @Composable (() -> Unit)? = null,
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        TextField(
            state = state,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            leadingIcon = icon,
            lineLimits = TextFieldLineLimits.SingleLine,
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                errorTextColor = SpacialRed,
                errorIndicatorColor = SpacialRed,
                errorLeadingIconColor = SpacialRed
            ),
        )
    }

}