package com.med.remedy.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.med.remedy.ui.theme.BasicBlack
import com.med.remedy.ui.theme.Transparency


data class NavigationItem(
    val screen: Screen,
    val icon: ImageVector
)

val navTabs = listOf(
    NavigationItem(Screen.Dashboard, Icons.Default.Dashboard),
    NavigationItem(Screen.Reminder, Icons.Default.Notifications),
    NavigationItem(Screen.Settings, Icons.Default.Settings)
)

@Composable
fun BottomSection(
    currentRoute: Screen?,
    onTabSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 40.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(28.dp),
                    clip = false,
                    ambientColor = BasicBlack.copy(alpha = 0.25f),
                    spotColor = BasicBlack.copy(alpha = 0.25f)
                )
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            navTabs.forEach { tab ->
                val onclick = remember(tab.screen, onTabSelected) { { onTabSelected(tab.screen) } }
                key(tab.screen) {
                    FloatingTabItem(
                        icon = tab.icon,
                        label = tab.screen.label,
                        isSelected = currentRoute == tab.screen,
                        onClick = onclick
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatingTabItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val primary = MaterialTheme.colorScheme.primary

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) primary.copy(alpha = 0.14f) else Transparency,
        animationSpec = tween(220),
        label = "tabBackground"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        animationSpec = tween(220),
        label = "tabContent"
    )
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.94f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tabScale"
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = primary),
                onClick = onClick
            )
            .padding(horizontal = if (isSelected) 16.dp else 14.dp, vertical = 10.dp)
            .scale(scale),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )

        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn(tween(200)) + expandHorizontally(tween(220)),
            exit = fadeOut(tween(150)) + shrinkHorizontally(tween(180))
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
                maxLines = 1
            )
        }
    }
}