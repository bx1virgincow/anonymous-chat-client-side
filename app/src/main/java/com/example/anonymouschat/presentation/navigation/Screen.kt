package com.example.anonymouschat.presentation.navigation

/**
 * Sealed class representing all navigation destinations
 *
 * Each screen has a route (string identifier)
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object StartChat : Screen("start_chat")
    object Chat : Screen("chat/{chatId}") {
        /**
         * Create route with actual chatId parameter
         */
        fun createRoute(chatId: String): String {
            return "chat/$chatId"
        }
    }
}