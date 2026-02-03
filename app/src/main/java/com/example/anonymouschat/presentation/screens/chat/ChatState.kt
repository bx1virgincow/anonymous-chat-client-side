package com.example.anonymouschat.presentation.screens.chat

import com.example.anonymouschat.domain.model.Chat
import com.example.anonymouschat.domain.model.Message

data class ChatState(
    val chat: Chat? = null,
    val messages: List<Message> = emptyList(),
    val messageText: String = "",
    val isOtherUserTyping: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)