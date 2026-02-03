package com.example.anonymouschat.data.remote.websocket

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/** interface for websocket client */

interface WebSocketClient{
    // current connection state
    val connectionState: StateFlow<WebSocketState>

    // connect websocket
    suspend fun connect()

    // disconnect
    suspend fun disconnect()

    // subscribe to destination and receive message
    fun subscribe(destination: String): Flow<String>

    // send message to destination
    suspend fun send(destination: String, body: String)

    // check if currently connected
    fun isConnected(): Boolean
}