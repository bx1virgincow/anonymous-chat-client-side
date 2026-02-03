package com.example.anonymouschat.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Generic message response from server
 * Used for MESSAGE_SENT, ERROR, etc.
 */
@Serializable
data class MessageResponseDTO(
    @SerialName("type")
    val type: String,

    @SerialName("messageId")
    val messageId: String? = null,

    @SerialName("timestamp")
    val timestamp: String? = null,

    @SerialName("delivered")
    val delivered: Boolean? = null,

    @SerialName("message")
    val message: String? = null,  // For error messages

    @SerialName("error")
    val error: String? = null
)