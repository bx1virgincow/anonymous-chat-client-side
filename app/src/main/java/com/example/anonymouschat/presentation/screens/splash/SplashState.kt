package com.example.anonymouschat.presentation.screens.splash

import com.example.anonymouschat.domain.model.User

/** UI State for splash screen
 * This state represents what the UI displays at any
 * given moment
 */

sealed class SplashState {
    object Loading: SplashState()
    data class Success(val user: User) : SplashState()
    data class Error(val message: String): SplashState()
}