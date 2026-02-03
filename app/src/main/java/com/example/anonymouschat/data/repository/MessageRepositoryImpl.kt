package com.example.anonymouschat.data.repository

import android.util.Log
import com.example.anonymouschat.data.mapper.toDTO
import com.example.anonymouschat.data.mapper.toDomainModel
import com.example.anonymouschat.data.remote.dto.ChatMessageDTO
import com.example.anonymouschat.data.remote.dto.MessageResponseDTO
import com.example.anonymouschat.data.remote.websocket.StompMessageHandler
import com.example.anonymouschat.data.remote.websocket.WebSocketClient
import com.example.anonymouschat.domain.model.Message
import com.example.anonymouschat.domain.model.MessageState
import com.example.anonymouschat.domain.repository.MessageRepository
import com.example.anonymouschat.domain.repository.UserRepository
import com.example.anonymouschat.util.Constants
import com.example.anonymouschat.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.time.LocalDateTime
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val webSocketClient: WebSocketClient,
    private val userRepository: UserRepository,
    private val messageHandler: StompMessageHandler
) : MessageRepository {

    // in-memory message cache: chatId -> List<Messages>
    private val _messageCache = MutableStateFlow<Map<String, List<Message>>>(emptyMap())

    override suspend fun sendMessage(message: Message): Result<Message> {
        return try {
            // convert to DTO
            val messageDto = message.toDTO()
            val json = messageHandler.toJson(messageDto)

            Log.d("MessageRepository", "Sending message: $json")
            // send via websocket
            webSocketClient.send(Constants.SEND_MESSAGE_DESTINATION, json)

            // add to cache immediately (optimistic update)
            addMessageToCache(message)

            Result.Success(message)
        } catch (e: Exception) {
            Log.e("MessageRepository", "Failed to send message", e)
            Result.Error(e)
        }
    }

    override fun observeMessages(chatId: String): Flow<Result<List<Message>>> {
        // subscribe to incoming messages
        val incomingMessages = webSocketClient.subscribe(Constants.SUBSCRIBE_MESSAGES)
            .onEach { json ->
                Log.d("MessageRepository", "Receive message JSON: $json")
                handleIncomingMessage(json, chatId)
            }
        // Combine WebSocket messages with cache
        return combine(
            _messageCache,
            incomingMessages
        ) { cache, _ ->
            cache[chatId] ?: emptyList()
        }.map { messages ->
            Result.Success(messages)
        }.catch { e ->
            Log.e("MessageRepository", "Error observing messages", e)
            Result.Error(Exception(e))
        }
    }

    override suspend fun getMessages(chatId: String): Result<List<Message>> {
        val messages = _messageCache.value[chatId] ?: emptyList()
        return Result.Success(messages)
    }

    override suspend fun markMessageAsRead(messageId: String): Result<Unit> {
        return try {
            val readReceipt = mapOf("messageId" to messageId)
            val json = messageHandler.toJson(readReceipt)
            webSocketClient.send(Constants.SEND_READ_RECEIPT_DESTINATION, json)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun sendTypingIndicator(
        chatId: String,
        isTyping: Boolean
    ): Result<Unit> {
        return try {
            val typingData = mapOf("chatId" to chatId, "isTyping" to isTyping)
            val json = messageHandler.toJson(typingData)
            webSocketClient.send(Constants.SEND_TYPING_DESTINATION, json)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun observeTypingStatus(chatId: String): Flow<Boolean> {
        return webSocketClient.subscribe(Constants.SUBSCRIBE_TYPING)
            .map { json ->
                // Parse typing indicator
                // Simplified - you'd parse proper DTO
                json.contains("\"isTyping\":true")
            }
    }

    override suspend fun deleteExpiryMessages(chatId: String): Result<Int> {
        return try {
            val messages = _messageCache.value[chatId] ?: emptyList()
            val now = LocalDateTime.now()

            val (expired, active) = messages.partition { message ->
                message.expiresAt?.isBefore(now) == true
            }

            // Update cache with only active messages
            _messageCache.value += (chatId to active)

            Result.Success(expired.size)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteAllMessages(chatId: String): Result<Unit> {
        return try {
            _messageCache.value -= chatId
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Helper function
    private fun addMessageToCache(message: Message) {
        val chatId = message.chatId
        val currentMessages = _messageCache.value[chatId] ?: emptyList()
        _messageCache.value += (chatId to (currentMessages + message))
    }

    private suspend fun handleIncomingMessage(json: String, chatId: String) {
        try {
            // Try parsing as ChatMessageDTO first (incoming message)
            val chatMessageDTO = messageHandler.parse<ChatMessageDTO>(json)
            if (chatMessageDTO != null) {
                val currentUserResult = userRepository.getUserIdentity()
                if (currentUserResult is Result.Success) {
                    val currentUser = currentUserResult.data

                    val message = chatMessageDTO.toDomainModel(currentUser.userId)
                    if (message != null && message.chatId == chatId) {
                        addMessageToCache(message)
                        Log.d("MessageRepository", "Added incoming message to cache")
                    }
                }
                return
            }

            // Try parsing as MessageResponseDTO (confirmation/error)
            val responseDTO = messageHandler.parse<MessageResponseDTO>(json)
            if (responseDTO != null) {
                Log.d("MessageRepository", "Received message response: type=${responseDTO.type}")

                when (responseDTO.type) {
                    "MESSAGE_SENT" -> {
                        // Update message state to Sent
                        responseDTO.messageId?.let { msgId ->
                            updateMessageState(msgId, MessageState.Sent)
                        }
                    }
                    "ERROR" -> {
                        Log.e("MessageRepository", "Message error: ${responseDTO.message}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MessageRepository", "Failed to handle message: $json", e)
        }
    }

    private fun updateMessageState(messageId: String, state: MessageState) {
        _messageCache.value = _messageCache.value.mapValues { (_, messages) ->
            messages.map { message ->
                if (message.id == messageId) {
                    message.withState(state)
                } else {
                    message
                }
            }
        }
    }
}