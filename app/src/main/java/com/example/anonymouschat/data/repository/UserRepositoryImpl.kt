package com.example.anonymouschat.data.repository

import android.util.Log
import com.example.anonymouschat.data.local.datastore.UserPreferences
import com.example.anonymouschat.data.mapper.toDomainModel
import com.example.anonymouschat.data.remote.api.UserApiService
import com.example.anonymouschat.domain.model.User
import com.example.anonymouschat.domain.repository.UserRepository
import com.example.anonymouschat.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userPreferences: UserPreferences,
    private val userApiService: UserApiService
) : UserRepository {

    override suspend fun registerUser(): Result<User> {
        return try {
            Log.d("UserRepository", "Registering new user via REST API...")

            val result = userApiService.registerUser()



            if (result is Result.Success) {
                val userDTO = result.data
                val user = userDTO.toDomainModel()

                // Save locally
                userPreferences.saveUser(user)

                Log.d("UserRepository", "User registered: ${user.fullShareable}")
                Result.Success(user)
            } else if (result is Result.Error){
                Result.Error(result.exception)
            }else{
                Result.Error(Exception("Failed to register"))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Registration error", e)
            Result.Error(e)
        }
    }

    override suspend fun verifyUserOnServer(userId: String): Result<Boolean> {
        return try {
            val result = userApiService.verifyUser(userId)

            if (result is Result.Success) {
                Result.Success(result.data)
            } else if (result is Result.Error){
                Result.Error(result.exception)
            }else{
                Result.Error(Exception("Failed to verify user"))
            }
        } catch (e: Exception) {
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
}