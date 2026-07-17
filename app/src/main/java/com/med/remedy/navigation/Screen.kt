package com.med.remedy.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen : NavKey {

    val label: String

    @Serializable
    data object Dashboard: Screen {
        override val label = "Dashboard"
    }

    @Serializable
    data object Reminder: Screen {
        override val label = "Reminder"
    }

    @Serializable
    data object Settings: Screen {
        override val label = "Settings"
    }

    @Serializable
    data class CreateReminder(
        val reminderId: Long = -1L
    ): Screen {
        override val label = "Create Reminder"
    }

    @Serializable
    data object TermsAndConditions: Screen {
        override val label = "Terms And Conditions"
    }

    @Serializable
    data object PrivacyPolicy: Screen {
        override val label = "Privacy Policy"
    }
}