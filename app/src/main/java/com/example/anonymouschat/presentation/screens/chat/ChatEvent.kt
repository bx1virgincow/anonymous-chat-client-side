package com.example.anonymouschat.presentation.screens.chat

sealed class ChatEvent {
    data class OnMessageTextChange(val text: String) : ChatEvent()
    object OnSendMessage : ChatEvent()
    data class OnMessageRead(val messageId: String) : ChatEvent()
    object OnTypingStarted : ChatEvent()
    object OnTypingStopped : ChatEvent()
}