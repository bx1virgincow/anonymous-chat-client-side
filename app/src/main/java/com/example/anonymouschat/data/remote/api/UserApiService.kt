package com.example.anonymouschat.data.remote.api

import com.example.anonymouschat.data.remote.dto.UserIdentityDTO
import com.example.anonymouschat.util.Constants
import com.example.anonymouschat.util.Result
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserApiService @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /** register a new user */
    suspend fun registerUser(): Result<UserIdentityDTO> {
        return try {
            val request = Request.Builder()
                .url("${Constants.API_BASE_URL}/register")
                .post(okhttp3.RequestBody.create(null, ""))
                .build()
            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val body =
                    response.body?.string() ?: return Result.Error(Exception("Empty response"))
                val userDTO = json.decodeFromString<UserIdentityDTO>(body)
                return Result.Success(userDTO)
            } else {
                Result.Error(Exception("Registration failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /** verify if user exists */
    suspend fun verifyUser(userId: String): Result<Boolean> {
        return try {
            val request = Request.Builder()
                .url("${Constants.API_BASE_URL}/verify/$userId")
                .get()
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val body = response.body?.string() ?: return Result.Success(false)
                val exists = body.contains("\"exists\":true")
                Result.Success(exists)
            } else {
                Result.Success(false)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}