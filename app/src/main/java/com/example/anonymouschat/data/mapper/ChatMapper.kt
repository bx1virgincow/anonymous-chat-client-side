package com.example.anonymouschat.data.mapper

import com.example.anonymouschat.data.remote.dto.ChatNotificationDTO
import com.example.anonymouschat.domain.model.Chat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun ChatNotificationDTO.toDomainModel(currentUserId: String): Chat? {
    // Different DTOs for CHAT_STARTED vs CHAT_REQUEST
    return when (this.type) {
        "CHAT_STARTED" -> {
            Chat(
                chatId = this.chatId ?: return null,
                otherUserId = this.withUserId ?: return null,
                otherUserName = this.withUserName ?: return null,
                otherUserShareable = this.withShareable ?: return null,
                isOnline = true,
                createdAt = parseTimestamp(this.timestamp)
            )
        }
        "CHAT_REQUEST" -> {
            Chat(
                chatId = this.chatId ?: return null,
                otherUserId = this.fromUserId ?: return null,
                otherUserName = this.fromUserName ?: return null,
                otherUserShareable = this.fromShareable ?: return null,
                isOnline = true,
                createdAt = parseTimestamp(this.timestamp)
            )
        }
        else -> null
    }
}

private fun parseTimestamp(timestamp: String?): LocalDateTime {
    return timestamp?.let {
        try {
            LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME)
        } catch (e: Exception) {
            LocalDateTime.now()
        }
    } ?: LocalDateTime.now()
}