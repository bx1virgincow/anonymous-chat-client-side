package com.example.anonymouschat.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageDTO(
    @SerialName("type")
    val type: String? = null,

    @SerialName("receiverId")
    val receiverId: String? = null,  // Made nullable

    @SerialName("content")
    val content: String? = null,  // Made nullable

    @SerialName("messageId")
    val messageId: String? = null,

    @SerialName("senderId")
    val senderId: String? = null,

    @SerialName("senderName")
    val senderName: String? = null,

    @SerialName("chatId")
    val chatId: String? = null,

    @SerialName("timestamp")
    val timestamp: String? = null,

    @SerialName("expiresIn")
    val expiresIn: Long? = null
)