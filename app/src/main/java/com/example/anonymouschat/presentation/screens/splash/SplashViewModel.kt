package com.example.anonymouschat.presentation.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anonymouschat.domain.usecase.connection.ConnectWebSocketUseCase
import com.example.anonymouschat.domain.usecase.user.GenerateUserIdentityUseCase
import com.example.anonymouschat.domain.usecase.user.GetUserIdentityUseCase
import com.example.anonymouschat.domain.usecase.user.RequestUserIdentityFromServer
import com.example.anonymouschat.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** ViewModel for splash screen
 * --Checks if user exists
 * --Generate user if needed
 * --Connect to websocket
 */

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getUserIdentityUseCase: GetUserIdentityUseCase,
    private val requestUserIdentityFromServer: RequestUserIdentityFromServer,
    private val generateUserIdentityUseCase: GenerateUserIdentityUseCase,
    private val connectWebSocketUseCase: ConnectWebSocketUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
    val state: StateFlow<SplashState> = _state.asStateFlow()

    init {
        initializeApp()
    }

    /** start app */
    private fun initializeApp() {
        viewModelScope.launch {
            /** get or create user */
            val userResult = requestUserIdentityFromServer()
            val user = when (userResult) {
                is Result.Success -> userResult.data
                is Result.Error -> {
                    _state.value = SplashState.Error(userResult.exception.message ?: "Failed to get user")
                    return@launch
                }
            }


            /** step 2: connec to websocket */
            when (connectWebSocketUseCase()) {
                is Result.Success -> {
                    _state.value = SplashState.Success(user)
                }

                is Result.Error -> {
                    /** still navigate user to home even if connection
                     * fails, connection will retry automatically
                     */
                    _state.value = SplashState.Success(user)
                }
            }
        }
    }
}