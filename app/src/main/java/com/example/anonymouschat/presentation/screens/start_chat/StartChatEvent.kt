package com.example.anonymouschat.presentation.screens.start_chat

sealed class StartChatEvent {
    data class OnShareableCodeChange(val code: String) : StartChatEvent()
    object OnStartChatClick : StartChatEvent()
    object OnErrorDismiss : StartChatEvent()
}