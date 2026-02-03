package com.example.anonymouschat.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Countdown timer component for message expiry
 *
 * Shows a visual countdown with progress bar
 *
 * @param totalSeconds Total time in seconds
 * @param onExpired Callback when countdown reaches 0
 */
@Composable
fun ExpiryCountdown(
    totalSeconds: Int,
    onExpired: () -> Unit,
    modifier: Modifier = Modifier
) {
    var secondsRemaining by remember { mutableStateOf(totalSeconds) }

    // Countdown effect
    LaunchedEffect(key1 = totalSeconds) {
        while (secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining--
        }
        onExpired()
    }

    val progress = secondsRemaining.toFloat() / totalSeconds.toFloat()
    val progressColor = when {
        secondsRemaining > 30 -> Color.Green
        secondsRemaining > 10 -> Color.Yellow
        else -> Color.Red
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Message expires in:",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Text(
                text = "${secondsRemaining}s",
                style = MaterialTheme.typography.bodyMedium,
                color = progressColor
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = progressColor,
            trackColor = Color.Gray.copy(alpha = 0.2f)
        )
    }
}

/**
 * Alternative: Simple text countdown without progress bar
 */
@Composable
fun SimpleExpiryCountdown(
    secondsRemaining: Int,
    modifier: Modifier = Modifier
) {
    val textColor = when {
        secondsRemaining > 30 -> Color.Gray
        secondsRemaining > 10 -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.error
    }

    Text(
        text = "Expires in ${secondsRemaining}s",
        style = MaterialTheme.typography.bodySmall,
        color = textColor,
        modifier = modifier
    )
}