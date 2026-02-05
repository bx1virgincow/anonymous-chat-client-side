package com.example.anonymouschat.domain.repository

import com.example.anonymouschat.domain.model.User
import com.example.anonymouschat.util.Result
import kotlinx.coroutines.flow.Flow

/** repository interface for user-related operations */

interface UserRepository{

    /**
     * Request user identity from server
     * Server will either return existing user or create new one
     */
    suspend fun registerUser(): Result<User>

    /** verify if user exists on server */
    suspend fun verifyUserOnServer(userId: String): Result<Boolean>

    /** get the current user's identity from local storage */
    suspend fun getUserIdentity(): Result<User>

    /** save identity to local storage */
    suspend fun saveUserIdentity(user: User): Result<Unit>

    /** delete user identity from local storage */
    suspend fun deleteUserIdentity(): Result<Unit>

    /** check if user identity exists in local storage */
    suspend fun hasUserIdentity(): Boolean

    /** observer user identity changes */
    fun observerUserIdentity() : Flow<Result<User>>
}