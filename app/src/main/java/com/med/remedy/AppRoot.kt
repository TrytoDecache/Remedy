package com.med.remedy

// this currently for navigation using nav3

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.med.remedy.navigation.BottomSection
import com.med.remedy.navigation.Screen
import com.med.remedy.navigation.addUnique
import com.med.remedy.ui.presentation.create.CreateReminderScreen
import com.med.remedy.ui.presentation.dashboard.DashboardScreen
import com.med.remedy.ui.presentation.doc.PrivacyPolicyScreen
import com.med.remedy.ui.presentation.doc.TermsScreen
import com.med.remedy.ui.presentation.reminder.ReminderScreen
import com.med.remedy.ui.presentation.settings.SettingsScreen


enum class AnimationDirection { LEFT, RIGHT, UP, DOWN }

@Composable
fun AppRoot() {
    val backStack = rememberNavBackStack(Screen.Dashboard)
    val currentScreen = backStack.last()
    val aniDur = 220
    var animationDirection by remember { mutableStateOf(AnimationDirection.LEFT) }
    val shouldShowBottomNavigation = remember(currentScreen) {
        currentScreen in listOf(
            Screen.Dashboard,
            Screen.Reminder,
            Screen.Settings
        )
    }

    val contentTransform = remember(animationDirection) {
        when (animationDirection) {
            AnimationDirection.LEFT -> {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(aniDur)
                ) togetherWith slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(aniDur)
                )
            }

            AnimationDirection.RIGHT -> {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(aniDur)
                ) togetherWith slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(aniDur)
                )
            }

            AnimationDirection.UP -> {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(aniDur)
                ) togetherWith slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(aniDur)
                )
            }

            AnimationDirection.DOWN -> {
                slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(aniDur)
                ) togetherWith slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(aniDur)
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = {
                animationDirection = when(currentScreen) {
                    is Screen.CreateReminder,
                    is Screen.TermsAndConditions,
                    is Screen.PrivacyPolicy -> { AnimationDirection.UP }
                    else -> AnimationDirection.RIGHT
                }
                backStack.removeLastOrNull()
            },
            entryProvider = entryProvider {
                entry<Screen.Dashboard> {
                    DashboardScreen(
                        onViewClick = {
                            animationDirection = AnimationDirection.LEFT
                            backStack.add(Screen.Reminder)
                        },
                        onAddClick = {
                            animationDirection = AnimationDirection.DOWN
                            backStack.add(Screen.CreateReminder())
                        }
                    )
                }

                entry<Screen.Reminder> {
                    ReminderScreen(
                        onCreateReminder = { reminderId ->
                            animationDirection = AnimationDirection.DOWN
                            backStack.addUnique(Screen.CreateReminder(reminderId))
                        }
                    )
                }

                entry<Screen.Settings> {
                    SettingsScreen(
                        onTermsClick = {
                            animationDirection = AnimationDirection.DOWN
                            backStack.add(Screen.TermsAndConditions)
                        },
                        onPrivacyClick = {
                            animationDirection = AnimationDirection.DOWN
                            backStack.add(Screen.PrivacyPolicy)
                        }
                    )
                }

                entry<Screen.CreateReminder> { route ->
                    CreateReminderScreen(
                        reminderId = route.reminderId,
                        onBack = {
                            animationDirection = AnimationDirection.UP
                            backStack.removeLastOrNull()
                        }
                    )
                }

                entry<Screen.TermsAndConditions> {
                    TermsScreen(
                        onBack = {
                            animationDirection = AnimationDirection.UP
                            backStack.removeLastOrNull()
                        }
                    )
                }
                entry<Screen.PrivacyPolicy> {
                    PrivacyPolicyScreen(
                        onBack = {
                            animationDirection = AnimationDirection.UP
                            backStack.removeLastOrNull()
                        }
                    )
                }
            },
            transitionSpec = { contentTransform },
            popTransitionSpec = { contentTransform },
            predictivePopTransitionSpec = { contentTransform }
        )

        if (shouldShowBottomNavigation) {
            BottomSection(
                currentRoute = currentScreen as Screen?,
                onTabSelected = { screen ->
                    if (screen == currentScreen) return@BottomSection

                    val tabs = listOf(Screen.Dashboard, Screen.Reminder, Screen.Settings)

                    val currentIndex = tabs.indexOf(currentScreen)
                    val targetIndex = tabs.indexOf(screen)

                    animationDirection =
                        if (targetIndex > currentIndex)
                            AnimationDirection.LEFT
                        else
                            AnimationDirection.RIGHT

                    backStack.add(screen)
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}