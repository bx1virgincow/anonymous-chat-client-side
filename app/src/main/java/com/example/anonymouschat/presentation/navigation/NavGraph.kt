package com.example.anonymouschat.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.anonymouschat.presentation.screens.chat.ChatScreen
import com.example.anonymouschat.presentation.screens.home.HomeScreen
import com.example.anonymouschat.presentation.screens.splash.SplashScreen
import com.example.anonymouschat.presentation.screens.start_chat.StartChatScreen

/**
 * Main navigation graph for the app
 *
 * Defines all navigation routes and how to navigate between them
 *
 * @param navController Navigation controller
 */
@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash Screen
        composable(route = Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    // Clear back stack and navigate to home
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Home Screen
        composable(route = Screen.Home.route) {
            HomeScreen(
                onNavigateToStartChat = {
                    navController.navigate(Screen.StartChat.route)
                },
                onNavigateToChat = { chatId ->
                    navController.navigate(Screen.Chat.createRoute(chatId))
                }
            )
        }

        // Start Chat Screen
        composable(route = Screen.StartChat.route) {
            StartChatScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToChat = { chatId ->
                    // Navigate to chat and remove StartChat from stack
                    navController.navigate(Screen.Chat.createRoute(chatId)) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        // Chat Screen (with chatId parameter)
        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("chatId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable

            ChatScreen(
                chatId = chatId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}