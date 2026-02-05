package com.example.anonymouschat.util

// application data constants

object Constants{
    // base url for the chat server
    const val WEBSOCKET_URL = "ws://10.0.2.2:8080/ws"
    // api base url
    const val API_BASE_URL = "http://10.0.2.2:8080/api/users"
    // connection timeout
    const val CONNECTION_TIMEOUT_MS = 10_000L
    // reconnect time
    const val RECONNECT_DELAY_MS = 5_000L
    // maximum reconnection attempts
    const val MAX_RECONNECT_ATTEMPTS = 3
    // stomp endpoints
    const val SEND_MESSAGE_DESTINATION = "/app/chat.send"
    const val START_CHAT_DESTINATION = "/app/chat.start"
    const val GET_USER_INFO_DESTINATION = "/app/user.info"
    const val SEND_TYPING_DESTINATION = "/app/chat.typing"
    const val SEND_READ_RECEIPT_DESTINATION = "/app/chat.read"

    // Destinations to SUBSCRIBE to (server → client)
    const val SUBSCRIBE_MESSAGES = "/user/queue/messages"
    const val SUBSCRIBE_CHATS = "/user/queue/chats"
    const val SUBSCRIBE_USER_INFO = "/user/queue/info"
    const val SUBSCRIBE_TYPING = "/user/queue/typing"
    const val SUBSCRIBE_RECEIPTS = "/user/queue/receipts"

    // Add new destination
    const val USER_CONNECT_DESTINATION = "/app/user.connect"
    const val SUBSCRIBE_CONNECTION = "/user/queue/connection"

    // chat message configuration
    const val DEFAULT_MESSAGE_EXPIRY_SECONDS = 60

    // maximum length of message characters including space
    const val MAX_MESSAGE_LENGTH = 500

    // datastore preferences key for storing user identity
    const val USER_PREFS_NAME = "user_preferences"
    const val KEY_USER_ID = "user_id"
    const val KEY_DISPLAY_NAME = "display_name"
    const val KEY_SHARE_CODE = "share_code"
    const val KEY_FULL_SHAREABLE = "full_shareable"

    // share code format
    val SHARE_CODE_PATTERN = Regex("^[A-Za-z]+[0-9]*#[A-Z0-9]{5}$")

    // ui configuration
    const val TYPING_INDICATOR_DELAY_MS = 1_000L

    // animation durations
    const val ANIMATION_DURATION_SHORT_MS = 200
    const val ANIMATION_DURATION_MEDIUM_MS = 400
    const val ANIMATION_DURATION_LONG_MS = 600



}