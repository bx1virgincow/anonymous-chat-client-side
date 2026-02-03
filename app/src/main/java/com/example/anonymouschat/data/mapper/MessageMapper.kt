package com.example.anonymouschat.data.mapper

import com.example.anonymouschat.data.remote.dto.ChatMessageDTO
import com.example.anonymouschat.domain.model.Message
import com.example.anonymouschat.domain.model.MessageState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun ChatMessageDTO.toDomainModel(currentUserId: String): Message? {
    return Message(
        id = this.messageId ?: return null,
        senderId = this.senderId ?: return null,
        receiverId = this.receiverId ?: return null,
        content = this.content ?: return  null,
        chatId = this.chatId ?: return null,
        timestamp = parseTimestamp(this.timestamp),
        expiresAt = this.expiresIn?.let {
            LocalDateTime.now().plusSeconds(it)
        },
        state = MessageState.Delivered,
        isSentByMe = this.senderId == currentUserId
    )
}

fun Message.toDTO(): ChatMessageDTO {
    return ChatMessageDTO(
        type = "MESSAGE",
        receiverId = this.receiverId,
        content = this.content,
        messageId = this.id,
        senderId = this.senderId,
        chatId = this.chatId
    )
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