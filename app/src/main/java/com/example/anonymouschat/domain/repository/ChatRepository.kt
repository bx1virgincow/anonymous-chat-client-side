package com.example.anonymouschat.domain.repository

import com.example.anonymouschat.domain.model.Chat
import com.example.anonymouschat.domain.model.ConnectionStatus
import com.example.anonymouschat.util.Result
import kotlinx.coroutines.flow.Flow

/** repository interface for chat-related operation
 * that handles starting new chats, getting list of active
 * chats, managing chat states and web socket connection status
 */

interface ChatRepository{

    /** connect to websocket */
    suspend fun connect(): Result<Unit>

    /** disconnect from websocket server */
    suspend fun disconnect(): Result<Unit>

    /** observer websocket connection status */
    fun observeConnectionStatus(): Flow<ConnectionStatus>

    /** start new chat with another user */
    suspend fun startChat(targetShareable: String): Result<Chat>

    /** get all active chats */
    suspend fun getActiveChats(): Result<List<Chat>>

    /** get a specific chat by ID */
    suspend fun getChatById(chatId: String): Result<Chat>

    /** delete chat */
    suspend fun deleteChat(chatId: String): Result<Unit>

    /** observe active chats */
    fun observeActiveChats(): Flow<Result<List<Chat>>>

    /** observe a specific chat */
    fun observeChat(chatId: String): Flow<Result<Chat>>

    /** update chat unread chat */
    suspend fun updateUnreadCount(chatId: String, count: Int): Result<Unit>

}