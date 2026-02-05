package com.example.anonymouschat.presentation.screens.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anonymouschat.domain.usecase.connection.ConnectWebSocketUseCase
import com.example.anonymouschat.domain.usecase.user.GetUserIdentityUseCase
import com.example.anonymouschat.domain.usecase.user.RegisterUserUseCase
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
    private val connectWebSocketUseCase: ConnectWebSocketUseCase,
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
    val state: StateFlow<SplashState> = _state.asStateFlow()

    init {
        initializeApp()
    }

    /** start app */
    private fun initializeApp() {
        viewModelScope.launch {
            /** check if user exist */
            val localResult = getUserIdentityUseCase()

            val user = when (localResult) {
                is Result.Success -> {
                    Log.d("SplashViewModel", "Local user found: ${localResult.data.fullShareable}")
                    localResult.data
                }

                is Result.Error -> {
                    Log.d("SplashViewModel", "No local user, registering...")
                    val registerResult = registerUserUseCase()

                    when (registerResult) {
                        is Result.Success -> registerResult.data
                        is Result.Error -> {
                            _state.value = SplashState.Error(
                                registerResult.exception.message ?: "Failed to register"
                            )
                            return@launch
                        }
                    }
                }
            }

            /** connect to websocket */
            Log.d("SplashViewModel", "Connecting websocket with userId: ${user.userId}")
            when (val connectResult =
                connectWebSocketUseCase(ConnectWebSocketUseCase.Params(user.userId))) {
                is Result.Success -> {
                    Log.d("SplashViewModel", "WebSocket connected successfully")

                    _state.value = SplashState.Success(user)
                }

                is Result.Error -> {
                    Log.w(
                        "SplashViewModel",
                        "WebSocket connection failed, navigating anyway",
                        connectResult.exception
                    )

                    _state.value = SplashState.Success(user)
                }
            }
        }
    }
}