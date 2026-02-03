package com.example.anonymouschat.domain.model

/** represents the websocket connection status
 */

sealed class ConnectionStatus {
    /** Not connected, or not connection attempt made */
    object Disconnected : ConnectionStatus()

    /** Attempting to connect to the server */
    object Connecting : ConnectionStatus()

    /** successfully connected */
    object Connected : ConnectionStatus()

    /** connection lost, attempt to reconnect */
    data class Reconnecting(val attemptNumber: Int) : ConnectionStatus()

    /** connection failed, not retrying */
    data class Failed(val reason: String) : ConnectionStatus()


    /** Extension functions for easier status checking
     * Similar to extension methods in Dart
     */
    fun ConnectionStatus.isConnected(): Boolean = this is ConnectionStatus.Connected
    fun ConnectionStatus.isDisconnected(): Boolean = this is ConnectionStatus.Disconnected
    fun ConnectionStatus.isConnecting(): Boolean = this is ConnectionStatus.Connecting
    fun ConnectionStatus.isFailed(): Boolean = this is ConnectionStatus.Failed

}