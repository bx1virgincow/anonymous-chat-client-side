package com.example.anonymouschat.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequestDTO(
    @SerialName("targetShareCode")
    val targetShareCode: String
)