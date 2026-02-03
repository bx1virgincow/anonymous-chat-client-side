package com.example.anonymouschat.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**DTO for user identity from server */

@Serializable
data class UserIdentityDTO(
    @SerialName("userId")
    val userId: String,
    @SerialName("displayName")
    val displayName: String,
    @SerialName("shareCode")
    val shareCode: String,
    @SerialName("fullShareable")
    val fullShareable: String,
    @SerialName("isNewUser")
    val isNewUser: Boolean = false
)