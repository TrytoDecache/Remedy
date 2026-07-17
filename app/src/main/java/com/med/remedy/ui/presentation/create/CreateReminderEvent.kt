package com.med.remedy.ui.presentation.create

sealed interface CreateReminderEvent {
    data object Success : CreateReminderEvent
    data class Error(val message: String): CreateReminderEvent
}