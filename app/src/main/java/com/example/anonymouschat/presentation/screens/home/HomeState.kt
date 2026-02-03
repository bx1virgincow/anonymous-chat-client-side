package com.example.anonymouschat.presentation.screens.home

import com.example.anonymouschat.domain.model.Chat
import com.example.anonymouschat.domain.model.ConnectionStatus
import com.example.anonymouschat.domain.model.User


/** state for home screen */

data class HomeState(
    val user: User? = null,
    val chats: List<Chat> = emptyList(),
    val connectionStatus: ConnectionStatus = ConnectionStatus.Disconnected,
    val isLoading: Boolean = false,
    val error: String? = null
)