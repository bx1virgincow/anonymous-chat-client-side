package com.example.anonymouschat.domain.model

import java.time.LocalDateTime

/** chat conversation between two users model */

data class Chat(
    val chatId: String,
    val otherUserId: String,
    val otherUserName: String,
    val otherUserShareable: String,
    val isOnline: Boolean = false,
    val lastSeenAt: LocalDateTime? = null,
    val lastMessage: String? = null,
    val unreadCount: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now()
){
    /** check if there are unread messages */
    fun hasUnreadMessages(): Boolean = unreadCount > 0

    /** get display text for last seen status
     * Last seen 5 minutes ago
     */
    fun getLastSeenText(): String {
        return if(isOnline){
            "Online"
        }else{
            lastSeenAt?.let { lastSeen ->
                val now = LocalDateTime.now()
                val minutesAgo = java.time.Duration.between(lastSeen, now).toMinutes()

                when{
                    minutesAgo < 1 -> "Just now"
                    minutesAgo < 60 -> "$minutesAgo minutes ago"
                    minutesAgo < 1440 -> "${minutesAgo / 60} hours ago"
                    else -> "${minutesAgo / 1440} days ago"
                }
            }?: "Offline"
        }
    }
}