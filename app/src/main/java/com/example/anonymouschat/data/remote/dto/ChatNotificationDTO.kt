package com.example.anonymouschat.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatNotificationDTO(
    @SerialName("type")
    val type: String,

    @SerialName("chatId")
    val chatId: String? = null,

    @SerialName("withUserId")
    val withUserId: String? = null,

    @SerialName("withUserName")
    val withUserName: String? = null,

    @SerialName("withShareable")
    val withShareable: String? = null,

    @SerialName("fromUserId")
    val fromUserId: String? = null,

    @SerialName("fromUserName")
    val fromUserName: String? = null,

    @SerialName("fromShareable")
    val fromShareable: String? = null,

    @SerialName("message")
    val message: String? = null,

    @SerialName("timestamp")
    val timestamp: String? = null
)