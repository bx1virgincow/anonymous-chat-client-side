package com.example.anonymouschat.presentation.screens.start_chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anonymouschat.domain.usecase.chat.StartChatUseCase
import com.example.anonymouschat.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class StartChatViewModel @Inject constructor(
    private val startChatUseCase: StartChatUseCase
): ViewModel() {

    private val _state = MutableStateFlow(StartChatState())
    val state: StateFlow<StartChatState> = _state.asStateFlow()

    fun onEvent(event: StartChatEvent) {
        when(event){
            is StartChatEvent.OnShareableCodeChange -> {
                _state.value = _state.value.copy(shareableCode = event.code)
            }
            is StartChatEvent.OnStartChatClick -> startChat()
            is StartChatEvent.OnErrorDismiss -> {
                _state.value = _state.value.copy(error = null)
            }
        }
    }

    /** start chat */
    private fun startChat() {
        val shareableCode = _state.value.shareableCode.trim()

        if(shareableCode.isEmpty()){
            _state.value = _state.value.copy(error = "Please enter a shareable code")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading =  true,
                error = null
            )

            when(val results = startChatUseCase(StartChatUseCase.Params(shareableCode))){
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        chatStarted = true,
                        error = null
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = results.exception.message ?: "Failed to start chat"
                    )
                }

            }
        }
    }
}