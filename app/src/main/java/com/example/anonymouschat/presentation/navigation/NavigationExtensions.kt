package com.example.anonymouschat.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

/**
 * Extension functions for easier navigation
 */

/**
 * Navigate and clear back stack
 */
fun NavController.navigateAndClearBackStack(route: String) {
    navigate(route) {
        popUpTo(0) { inclusive = true }
    }
}

/**
 * Navigate and pop up to a specific destination
 */
fun NavController.navigateAndPopUpTo(
    route: String,
    popUpToRoute: String,
    inclusive: Boolean = false
) {
    navigate(route) {
        popUpTo(popUpToRoute) { this.inclusive = inclusive }
    }
}

/**
 * Safe navigation - only navigate if not already on that destination
 */
fun NavController.navigateSafe(route: String, builder: NavOptionsBuilder.() -> Unit = {}) {
    if (currentDestination?.route != route) {
        navigate(route, builder)
    }
}