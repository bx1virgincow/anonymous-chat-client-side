package com.example.anonymouschat.data.remote.websocket

import android.util.Log
import com.example.anonymouschat.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.*
import okio.ByteString
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/** websocket client implementation using OkHttp */

@Singleton
class WebSocketClientImpl @Inject constructor(
    private val okHttpClient: OkHttpClient
): WebSocketClient{

    private val TAG = "WebSocketClient"
    private var webSocket: WebSocket? = null
    private var sessionId: String? = null

    // state
    private val _connectionState = MutableStateFlow<WebSocketState>(WebSocketState.Idle)
    override val connectionState: StateFlow<WebSocketState> = _connectionState

    // subscription: destination -> Flow emitter
    private val subscription = ConcurrentHashMap<String, MutableList<suspend (String) -> Unit>>()

    override suspend fun connect() {
        if(isConnected()){
            Log.d(TAG, "Already connected")
            return
        }

        _connectionState.value = WebSocketState.Connecting

        val request = Request.Builder()
            .url(Constants.WEBSOCKET_URL)
            .build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener(){

            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "Websocket opened")
                sendConnectFrame(webSocket)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Message received: $text")
                handleIncomingMessage(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d(TAG, "Binary message received")
                handleIncomingMessage(bytes.utf8())
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closing: $reason")
                webSocket.close(1000, null)
                _connectionState.value = WebSocketState.Disconnected
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket failure", t)
                _connectionState.value = WebSocketState.Error(
                    t.message ?: "Connection failed"
                )
            }
        })
    }

    override suspend fun disconnect() {
        webSocket?.let {
            sendDisconnectFrame(it)
            it.close(1000, "Client disconnected")
        }
        webSocket = null
        sessionId = null
        subscription.clear()
        _connectionState.value = WebSocketState.Disconnected
    }

    override  fun subscribe(destination: String): Flow<String> = callbackFlow {
        // add subscription
        val emitter: suspend (String) -> Unit= {message ->
            trySend(message)
        }

        subscription.getOrPut(destination){mutableListOf()}.add(emitter)

        // send STOMP subscribe frame
        webSocket?.let { ws->
            val subscribeFrame = buildSubscribeFrame(destination)
            ws.send(subscribeFrame)
            Log.d(TAG, "Subscribed to: $destination")
        }

        awaitClose{
            subscription[destination]?.remove(emitter)
            Log.d(TAG, "Unsubscribed from: $destination")
        }
    }

    override suspend fun send(destination: String, body: String) {
        webSocket?.let { ws ->
            val sendFrame = buildSendFrame(destination, body)
            val sent = ws.send(sendFrame)
            Log.d(TAG, "Message sent to $destination: $sent")
        } ?: Log.e(TAG, "Cannot send - WebSocket not connected")
    }

    override fun isConnected(): Boolean {
        return _connectionState.value is WebSocketState.Connected
    }

    // ========== STOMP Protocol Implementation ==========


    private fun sendConnectFrame(ws: WebSocket) {
        val connectFrame = buildString {
            append("CONNECT\n")
            append("accept-version:1.1,1.2\n")
            append("heart-beat:0,0\n")
            append("\n")
            append("\u0000") // NULL character
        }
        ws.send(connectFrame)
        Log.d(TAG, "CONNECT frame sent")
    }

    /**
     * Build STOMP SUBSCRIBE frame
     *
     * Format:
     * SUBSCRIBE
     * id:sub-0
     * destination:/user/queue/messages
     *
     * ^@
     */
    private fun buildSubscribeFrame(destination: String): String {
        val subscriptionId = "sub-${UUID.randomUUID()}"
        return buildString {
            append("SUBSCRIBE\n")
            append("id:$subscriptionId\n")
            append("destination:$destination\n")
            append("\n")
            append("\u0000")
        }
    }

    /**
     * Build STOMP SEND frame
     *
     * Format:
     * SEND
     * destination:/app/chat.send
     * content-type:application/json
     * content-length:123
     *
     * {"message":"hello"}^@
     */
    private fun buildSendFrame(destination: String, body: String): String {
        return buildString {
            append("SEND\n")
            append("destination:$destination\n")
            append("content-type:application/json\n")
            append("content-length:${body.length}\n")
            append("\n")
            append(body)
            append("\u0000")
        }
    }

    /**
     * Build STOMP DISCONNECT frame
     */
    private fun sendDisconnectFrame(ws: WebSocket) {
        val disconnectFrame = buildString {
            append("DISCONNECT\n")
            append("\n")
            append("\u0000")
        }
        ws.send(disconnectFrame)
        Log.d(TAG, "DISCONNECT frame sent")
    }

    /**
     * Handle incoming STOMP messages
     * Parse the frame and route to appropriate subscribers
     */
    private fun handleIncomingMessage(message: String) {
        val lines = message.split("\n")

        if (lines.isEmpty()) return

        val command = lines[0]

        when (command) {
            "CONNECTED" -> {
                // Extract session ID if provided
                sessionId = extractHeader(lines, "session")
                _connectionState.value = WebSocketState.Connected
                Log.d(TAG, "STOMP CONNECTED, session: $sessionId")
            }

            "MESSAGE" -> {
                // Extract destination and body
                val destination = extractHeader(lines, "destination")
                val body = extractBody(message)

                // Route to subscribers
                destination?.let { dest ->
                    subscription[dest]?.forEach { emitter ->
                        kotlinx.coroutines.runBlocking {
                            emitter(body)
                        }
                    }
                }
            }

            "ERROR" -> {
                val errorMessage = extractBody(message)
                _connectionState.value = WebSocketState.Error(errorMessage)
                Log.e(TAG, "STOMP ERROR: $errorMessage")
            }
        }
    }

    /**
     * Extract header value from STOMP frame
     */
    private fun extractHeader(lines: List<String>, headerName: String): String? {
        return lines.firstOrNull { it.startsWith("$headerName:") }
            ?.substringAfter(":")
            ?.trim()
    }

    /**
     * Extract body from STOMP frame
     * Body comes after empty line, before NULL character
     */
    private fun extractBody(message: String): String {
        val bodyStart = message.indexOf("\n\n")
        if (bodyStart == -1) return ""

        val body = message.substring(bodyStart + 2)
        // Remove NULL character
        return body.replace("\u0000", "").trim()
    }
}