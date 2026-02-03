package com.example.anonymouschat.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anonymouschat.domain.usecase.chat.DeleteChatUseCase
import com.example.anonymouschat.domain.usecase.chat.ObserveActiveChatsUseCase
import com.example.anonymouschat.domain.usecase.connection.ObserveConnectionStatusUseCase
import com.example.anonymouschat.domain.usecase.user.GetUserIdentityUseCase
import com.example.anonymouschat.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserIdentityUseCase: GetUserIdentityUseCase,
    private val observeActiveChatsUseCase: ObserveActiveChatsUseCase,
    private val deleteChatUseCase: DeleteChatUseCase,
    private val observeConnectionStatusUseCase: ObserveConnectionStatusUseCase
): ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadUserIdentity()
        observeChats()
        observeConnectionStatus()
    }

    fun onEvent(event: HomeEvent) {
        when(event) {
            is HomeEvent.OnStartChatClick -> {
                // navigation handled in UI
            }
            is HomeEvent.OnChatClick -> {
                // navigation handled in UI
            }
            is HomeEvent.OnDeleteChat -> deleteChat(event.chatId)
            is HomeEvent.OnRefresh -> {
                loadUserIdentity()
                observeChats()
            }
        }
    }

    /** load user identity */
    private fun loadUserIdentity() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            when(val result = getUserIdentityUseCase()){
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        user = result.data,
                        isLoading = false,
                        error = null
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.exception.message
                    )
                }
            }
        }
    }

    /** observe chats */
    private  fun observeChats() {
        viewModelScope.launch {
            observeActiveChatsUseCase()
                .collect { result ->
                    when(result){
                        is Result.Success -> {
                            _state.value = _state.value.copy(
                                chats = result.data,
                                error = null
                            )
                        }
                        is Result.Error -> {
                            _state.value = _state.value.copy(
                                error = result.exception.message
                            )
                        }
                    }
                }
        }
    }

    /** observe connection status */
    private fun observeConnectionStatus() {
        viewModelScope.launch {
            observeConnectionStatusUseCase().collect { status ->
                _state.value = _state.value.copy(
                    connectionStatus = status
                )
            }
        }
    }

    /** delete chat */
    private fun deleteChat(chatId: String) {
        viewModelScope.launch {
            deleteChatUseCase(DeleteChatUseCase.Params(chatId))
        }
    }
}