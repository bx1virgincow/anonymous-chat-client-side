package com.example.anonymouschat.data.remote.websocket

import com.example.anonymouschat.domain.model.ConnectionStatus

/** internal websocket states */

sealed class WebSocketState {
    object Idle : WebSocketState()
    object Connecting : WebSocketState()
    object Connected : WebSocketState()
    data class Error(val message: String) : WebSocketState()
    object Disconnected : WebSocketState()
}

    /**
     * Map internal state to domain ConnectionStatus
     */
    fun WebSocketState.toConnectionStatus(): ConnectionStatus {
        return when (this) {
            is WebSocketState.Idle -> ConnectionStatus.Disconnected
            is WebSocketState.Connecting -> ConnectionStatus.Connecting
            is WebSocketState.Connected -> ConnectionStatus.Connected
            is WebSocketState.Error -> ConnectionStatus.Failed(this.message)
            is WebSocketState.Disconnected -> ConnectionStatus.Disconnected
        }

    }