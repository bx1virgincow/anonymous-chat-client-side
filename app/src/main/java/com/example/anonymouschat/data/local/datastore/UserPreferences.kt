package com.example.anonymouschat.data.local.datastore

import com.example.anonymouschat.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Interface for user preferences storage
 */
interface UserPreferences {

    /**
     * Save user identity
     */
    suspend fun saveUser(user: User)

    /**
     * Get user identity
     */
    suspend fun getUser(): User?

    /**
     * Observe user changes
     */
    fun observeUser(): Flow<User?>

    /**
     * Delete user identity
     */
    suspend fun deleteUser()

    /**
     * Check if user exists
     */
    suspend fun hasUser(): Boolean
}