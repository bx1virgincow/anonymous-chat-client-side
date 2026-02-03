package com.example.anonymouschat.presentation.screens.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anonymouschat.domain.model.Message
import com.example.anonymouschat.domain.model.MessageState
import com.example.anonymouschat.domain.usecase.chat.GetActiveChatsUseCase
import com.example.anonymouschat.domain.usecase.message.MarkMessageAsReadUseCase
import com.example.anonymouschat.domain.usecase.message.ObserveMessagesUseCase
import com.example.anonymouschat.domain.usecase.message.ObserveTypingStatusUseCase
import com.example.anonymouschat.domain.usecase.message.SendMessageUseCase
import com.example.anonymouschat.domain.usecase.message.SendTypingIndicatorUseCase
import com.example.anonymouschat.domain.usecase.user.GetUserIdentityUseCase
import com.example.anonymouschat.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getUserIdentityUseCase: GetUserIdentityUseCase,
    private val getActiveChatsUseCase: GetActiveChatsUseCase,  // Changed
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val markMessageAsReadUseCase: MarkMessageAsReadUseCase,
    private val sendTypingIndicatorUseCase: SendTypingIndicatorUseCase,
    private val observeTypingStatusUseCase: ObserveTypingStatusUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Get chatId from navigation arguments
    private val chatId: String = checkNotNull(savedStateHandle["chatId"])

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    init {
        loadChat()  // Changed from observeChat
        observeMessages()
        observeTypingStatus()
    }

    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.OnMessageTextChange -> {
                _state.value = _state.value.copy(messageText = event.text)
                handleTyping(event.text)
            }
            is ChatEvent.OnSendMessage -> sendMessage()
            is ChatEvent.OnMessageRead -> markMessageAsRead(event.messageId)
            is ChatEvent.OnTypingStarted -> sendTypingIndicator(true)
            is ChatEvent.OnTypingStopped -> sendTypingIndicator(false)
        }
    }

    // Changed: Use getActiveChatsUseCase and find the specific chat
    private fun loadChat() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            when (val result = getActiveChatsUseCase()) {
                is Result.Success -> {
                    val chat = result.data.find { it.chatId == chatId }
                    _state.value = _state.value.copy(
                        chat = chat,
                        isLoading = false,
                        error = if (chat == null) "Chat not found" else null
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

    private fun observeMessages() {
        viewModelScope.launch {
            observeMessagesUseCase(ObserveMessagesUseCase.Params(chatId))
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _state.value = _state.value.copy(messages = result.data)
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

    private fun observeTypingStatus() {
        viewModelScope.launch {
            observeTypingStatusUseCase(ObserveTypingStatusUseCase.Params(chatId))
                .collect { isTyping ->
                    _state.value = _state.value.copy(isOtherUserTyping = isTyping)
                }
        }
    }

    private fun sendMessage() {
        val messageText = _state.value.messageText.trim()
        if (messageText.isEmpty()) return

        viewModelScope.launch {
            val userResult = getUserIdentityUseCase()
            if (userResult is Result.Success) {
                val user = userResult.data
                val chat = _state.value.chat ?: return@launch

                val message = Message(
                    senderId = user.userId,
                    receiverId = chat.otherUserId,
                    content = messageText,
                    chatId = chatId,
                    timestamp = LocalDateTime.now(),
                    state = MessageState.Sending,
                    isSentByMe = true
                )

                when (sendMessageUseCase(SendMessageUseCase.Params(message))) {
                    is Result.Success -> {
                        _state.value = _state.value.copy(messageText = "")
                        sendTypingIndicator(false)
                    }
                    is Result.Error -> {
                        _state.value = _state.value.copy(
                            error = "Failed to send message"
                        )
                    }
                }
            }
        }
    }

    private fun markMessageAsRead(messageId: String) {
        viewModelScope.launch {
            markMessageAsReadUseCase(MarkMessageAsReadUseCase.Params(messageId))
        }
    }

    @OptIn(FlowPreview::class)
    private fun handleTyping(text: String) {
        viewModelScope.launch {
            if (text.isNotEmpty()) {
                sendTypingIndicator(true)
                // Auto-send "stopped typing" after 1 second of no typing
                kotlinx.coroutines.delay(1000)
                if (_state.value.messageText == text) {
                    sendTypingIndicator(false)
                }
            }
        }
    }

    private fun sendTypingIndicator(isTyping: Boolean) {
        viewModelScope.launch {
            sendTypingIndicatorUseCase(
                SendTypingIndicatorUseCase.Params(chatId, isTyping)
            )
        }
    }
}