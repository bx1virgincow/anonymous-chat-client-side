package com.example.anonymouschat.domain.model


sealed class MessageState{
    /** message is being sent to server */
    object Sending: MessageState()

    /** message sent state */
    object Sent: MessageState()

    /** message delivered */
    object Delivered: MessageState()

    /** Read */
    object Read: MessageState()

    /** Expiring messages, countdown active */
    data class Expiring(val secondsRemaining: Int): MessageState()

    /** expired and deleted messages */
    object Expired: MessageState()

    /** message failed to send */
    data class Failed(val error: String): MessageState()

}