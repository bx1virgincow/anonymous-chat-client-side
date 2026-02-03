package com.example.anonymouschat.data.remote.websocket

import android.util.Log
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

/** helper to parse STOMP message bodies */
class StompMessageHandler {

    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    /** parse json string to object */
    inline fun <reified T> parse(jsonString: String): T? {
        return try {
            json.decodeFromString<T>(jsonString)  // Use 'json' instance, not 'Json'
        } catch (e: Exception) {  // Fix: was 'else' should be 'catch'
            Log.e("StompMessageHandler", "Failed to parse: $jsonString", e)
            null
        }
    }

    /** convert object to json string */
    inline fun <reified T> toJson(obj: T): String {
        return json.encodeToString(obj)  // Simplified - no need for serializer()
    }
}