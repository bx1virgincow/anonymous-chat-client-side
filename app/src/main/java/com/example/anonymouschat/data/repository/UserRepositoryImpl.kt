package com.example.anonymouschat.data.repository

import android.util.Log
import com.example.anonymouschat.data.local.datastore.UserPreferences
import com.example.anonymouschat.data.mapper.toDomainModel
import com.example.anonymouschat.data.remote.dto.UserIdentityDTO
import com.example.anonymouschat.data.remote.websocket.StompMessageHandler
import com.example.anonymouschat.data.remote.websocket.WebSocketClient
import com.example.anonymouschat.domain.model.User
import com.example.anonymouschat.domain.repository.UserRepository
import com.example.anonymouschat.util.Constants
import com.example.anonymouschat.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userPreferences: UserPreferences,
    private val webSocketClient: WebSocketClient,
    private val messageHandler: StompMessageHandler
) : UserRepository {

    override suspend fun requestUserIdentityFromServer(): Result<User> {
        return try {
            Log.d("UserRepository", "Requesting user identity from server...")

            // Wait for connection
            val connectResult = connectAndWait()
            if (connectResult is Result.Error) {
                Log.e("UserRepository", "Connection failed: ${connectResult.exception.message}")
                return connectResult
            }

            Log.d("UserRepository", "WebSocket connected, subscribing to ${Constants.SUBSCRIBE_USER_INFO}")

            // Subscribe FIRST
            val responseFlow = webSocketClient.subscribe(Constants.SUBSCRIBE_USER_INFO)

            Log.d("UserRepository", "Sending request to ${Constants.GET_USER_INFO_DESTINATION}")

            // Send request
            webSocketClient.send(Constants.GET_USER_INFO_DESTINATION, "{}")

            Log.d("UserRepository", "Waiting for server response...")

            // Wait for response with timeout
            val response = withTimeout(10_000) {
                responseFlow.first()
            }

            Log.d("UserRepository", "Received response: $response")

            // Parse response
            val userDTO = messageHandler.parse<UserIdentityDTO>(response)
            if (userDTO == null) {
                Log.e("UserRepository", "Failed to parse user DTO from response")
                return Result.Error(Exception("Failed to parse user info"))
            }

            Log.d("UserRepository", "Parsed user: ${userDTO.displayName}")

            // Convert to domain model and save locally
            val user = userDTO.toDomainModel()
            userPreferences.saveUser(user)

            Log.d("UserRepository", "User saved locally: ${user.fullShareable}")

            Result.Success(user)
        } catch (e: Exception) {
            Log.e("UserRepository", "Failed to get user from server", e)
            Result.Error(e)
        }
    }

    override suspend fun getUserIdentity(): Result<User> {
        return try {
            val user = userPreferences.getUser()
            if (user != null) {
                Result.Success(user)
            } else {
                Result.Error(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun saveUserIdentity(user: User): Result<Unit> {
        return try {
            userPreferences.saveUser(user)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteUserIdentity(): Result<Unit> {
        return try {
            userPreferences.deleteUser()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun hasUserIdentity(): Boolean {
        return userPreferences.hasUser()
    }

    override fun observerUserIdentity(): Flow<Result<User>> {
        return userPreferences.observeUser().map { user ->
            if (user != null) {
                Result.Success(user)
            } else {
                Result.Error(Exception("User not found"))
            }
        }
    }

    private suspend fun connectAndWait(timeoutMs: Long = 10000): Result<Unit> {
        return try {
            if (!webSocketClient.isConnected()) {
                Log.d("UserRepository", "WebSocket not connected, connecting...")
                webSocketClient.connect()
            }

            val startTime = System.currentTimeMillis()
            while (!webSocketClient.isConnected()) {
                if (System.currentTimeMillis() - startTime > timeoutMs) {
                    Log.e("UserRepository", "Timeout waiting for WebSocket connection")
                    return Result.Error(Exception("WebSocket failed to connect in time"))
                }
                kotlinx.coroutines.delay(100)
            }

            Log.d("UserRepository", "WebSocket connection confirmed")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "Connection error", e)
            Result.Error(e)
        }
    }
}