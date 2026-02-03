package com.example.anonymouschat.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val senderId: String,
    val receiverId: String,
    val content: String,
    val chatId: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime? = null,
    val state: MessageState = MessageState.Sending,
    val isSentByMe: Boolean
) {
    /** check if message expired */
    fun isExpired(): Boolean {
        return expiresAt?.let { it.isBefore(LocalDateTime.now()) } ?: false
    }

    /** calculate seconds remaining until expiry */
    fun getSecondsUntilExpiry(): Int? {
        return expiresAt?.let { expiry ->
            val now = LocalDateTime.now()
            if (expiry.isAfter(now)) {
                java.time.Duration.between(now, expiry).seconds.toInt()
            } else {
                0
            }
        }
    }

    /** create a copy with updated state */
    fun withState(newState: MessageState): Message{
        return copy(state = newState)
    }
}