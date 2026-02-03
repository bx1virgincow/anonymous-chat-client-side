package com.example.anonymouschat.domain.repository

import com.example.anonymouschat.domain.model.Message
import com.example.anonymouschat.util.Result
import kotlinx.coroutines.flow.Flow

/** repository interface for message-related operation
 * for handling sending message,receiving messages,
 * messages state updates, read receipts, typing indicator
 */

interface MessageRepository {
    /** send a message to another user */
    suspend fun sendMessage(message: Message): Result<Message>

    /** observe incoming messages for a specific chat */
    fun observeMessages(chatId: String): Flow<Result<List<Message>>>

    /** get all messages for a specific chat */
    suspend fun getMessages(chatId: String): Result<List<Message>>

    /** mark a messages as read */
    suspend fun markMessageAsRead(messageId: String): Result<Unit>

    /** sending typing indicator */
    suspend fun sendTypingIndicator(chatId: String, isTyping: Boolean): Result<Unit>

    /** observe typing status for a chat */
    fun observeTypingStatus(chatId: String): Flow<Boolean>

    /** delete expired messages */
    suspend fun deleteExpiryMessages(chatId: String): Result<Int>

    /** delete all messages in a chat */
    suspend fun deleteAllMessages(chatId: String): Result<Unit>
}