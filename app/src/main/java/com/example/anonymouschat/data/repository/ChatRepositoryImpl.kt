package com.example.anonymouschat.data.repository

import android.util.Log
import com.example.anonymouschat.data.mapper.toDomainModel
import com.example.anonymouschat.data.remote.dto.ChatNotificationDTO
import com.example.anonymouschat.data.remote.dto.ChatRequestDTO
import com.example.anonymouschat.data.remote.websocket.StompMessageHandler
import com.example.anonymouschat.data.remote.websocket.WebSocketClient
import com.example.anonymouschat.data.remote.websocket.toConnectionStatus
import com.example.anonymouschat.domain.model.Chat
import com.example.anonymouschat.domain.model.ConnectionStatus
import com.example.anonymouschat.domain.repository.ChatRepository
import com.example.anonymouschat.domain.repository.UserRepository
import com.example.anonymouschat.util.Constants
import com.example.anonymouschat.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val webSocketClient: WebSocketClient,
    private val userRepository: UserRepository,
    private val messageHandler: StompMessageHandler
) : ChatRepository {

    private val TAG = "ChatRepository"

    /** in-memory cache of active chats */
    private val _activeChats = MutableStateFlow<List<Chat>>(emptyList())

    override suspend fun connect(userId: String?): Result<Unit> {
        return try {
            Log.d(TAG, "Connecting with userId: $userId")

            // Step 1: Connect WebSocket with userId in HTTP header
            webSocketClient.connect(userId)

            // Step 2: Wait for STOMP connection
            var attempts = 0
            while (!webSocketClient.isConnected() && attempts < 30) {
                kotlinx.coroutines.delay(100)
                attempts++
            }

            if (!webSocketClient.isConnected()) {
                return Result.Error(Exception("WebSocket connection timeout"))
            }

            Log.d(TAG, "WebSocket connected, now authenticating user...")

            // Step 3: Authenticate user (send /app/user.connect)
            // This is a fallback in case the HTTP header approach didn't work
            if (userId != null) {
                val authResult = webSocketClient.authenticateUser(userId)

                if (authResult.isFailure) {
                    Log.w(TAG, "User authentication via /app/user.connect failed: ${authResult.exceptionOrNull()?.message}")
                    // Don't fail here - the HTTP header approach might have worked
                } else {
                    Log.d(TAG, "User authenticated successfully via /app/user.connect")
                }
            }

            Log.d(TAG, "Connected successfully")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Connection failed", e)
            Result.Error(e)
        }
    }

    override suspend fun disconnect(): Result<Unit> {
        return try {
            webSocketClient.disconnect()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun observeConnectionStatus(): Flow<ConnectionStatus> {
        return webSocketClient.connectionState.map { it.toConnectionStatus() }
    }

    override suspend fun startChat(targetShareable: String): Result<Chat> {
        return try {
            // get current user
            val currentUserResults = userRepository.getUserIdentity()

            if (currentUserResults is Result.Error) {
                return Result.Error(currentUserResults.exception)
            }
            val currentUser = (currentUserResults as Result.Success).data

            // create request DTO
            val request = ChatRequestDTO(targetShareable)
            val requestJson = messageHandler.toJson(request)

            //send to server
            webSocketClient.send(Constants.START_CHAT_DESTINATION, requestJson)

            // subscribe to chat responses and wait for results
            val chatResult = webSocketClient
                .subscribe(Constants.SUBSCRIBE_CHATS)
                .map { json ->
                    messageHandler.parse<ChatNotificationDTO>(json)
                        ?.toDomainModel(currentUser.userId)
                }
                .filterNotNull()
                .first()

            // add to active chats
            _activeChats.value += chatResult
            Result.Success(chatResult)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getActiveChats(): Result<List<Chat>> {
        return Result.Success(_activeChats.value)
    }

    override suspend fun getChatById(chatId: String): Result<Chat> {
        val chat = _activeChats.value.find { it.chatId == chatId }
        return if (chat != null) {
            Result.Success(chat)
        } else {
            Result.Error(Exception("Chat not found"))
        }
    }

    override suspend fun deleteChat(chatId: String): Result<Unit> {
        return try {
            _activeChats.value = _activeChats.value.filter { it.chatId != chatId }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun observeActiveChats(): Flow<Result<List<Chat>>> {
        return _activeChats.map { Result.Success(it) }
    }

    override fun observeChat(chatId: String): Flow<Result<Chat>> {
        return _activeChats.map { chats ->
            val chat = chats.find { it.chatId == chatId }
            if (chat != null) {
                Result.Success(chat)
            } else {
                Result.Error(Exception("Chat not found"))
            }
        }
    }

    override suspend fun updateUnreadCount(
        chatId: String,
        count: Int
    ): Result<Unit> {
        return try {
            _activeChats.value = _activeChats.value.map { chat ->
                if (chat.chatId == chatId) {
                    chat.copy(unreadCount = count)
                } else {
                    chat
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}