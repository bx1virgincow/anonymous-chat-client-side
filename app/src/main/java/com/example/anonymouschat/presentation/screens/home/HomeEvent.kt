package com.example.anonymouschat.presentation.screens.home

/** event that can happen on Home screen: user actions */

sealed class HomeEvent {
    object OnStartChatClick : HomeEvent()
    data class OnChatClick(val chatId: String): HomeEvent()
    data class OnDeleteChat(val chatId: String): HomeEvent()
    object OnRefresh : HomeEvent()
}