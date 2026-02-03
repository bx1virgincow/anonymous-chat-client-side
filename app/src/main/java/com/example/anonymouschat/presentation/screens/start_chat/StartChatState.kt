package com.example.anonymouschat.presentation.screens.start_chat

data class StartChatState(
    val shareableCode: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val chatStarted: Boolean = false
)