package com.example.anonymouschat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.anonymouschat.domain.model.ConnectionStatus
import com.example.anonymouschat.presentation.theme.ErrorRed
import com.example.anonymouschat.presentation.theme.OnlineGreen

@Composable
fun ConnectionStatusBar(
    connectionStatus: ConnectionStatus,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (connectionStatus) {
        is ConnectionStatus.Connected -> "Connected" to OnlineGreen
        is ConnectionStatus.Connecting -> "Connecting..." to Color.Gray
        is ConnectionStatus.Disconnected -> "Disconnected" to ErrorRed
        is ConnectionStatus.Reconnecting -> "Reconnecting (${connectionStatus.attemptNumber})..." to Color.Gray
        is ConnectionStatus.Failed -> "Connection failed: ${connectionStatus.reason}" to ErrorRed
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall
        )
    }
}