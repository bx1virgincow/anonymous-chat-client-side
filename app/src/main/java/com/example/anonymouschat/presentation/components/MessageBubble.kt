package com.example.anonymouschat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.anonymouschat.domain.model.Message
import com.example.anonymouschat.domain.model.MessageState
import com.example.anonymouschat.presentation.theme.ReceivedMessageBackground
import com.example.anonymouschat.presentation.theme.SentMessageBackground
import java.time.format.DateTimeFormatter

@Composable
fun MessageBubble(
    message: Message,
    modifier: Modifier = Modifier
) {
    val alignment = if (message.isSentByMe) Alignment.End else Alignment.Start
    val backgroundColor = if (message.isSentByMe) SentMessageBackground else ReceivedMessageBackground
    val shape = if (message.isSentByMe) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(backgroundColor, shape)
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = message.timestamp.format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Message state indicator
                    when (message.state) {
                        is MessageState.Sending -> Text("⏳", style = MaterialTheme.typography.bodySmall)
                        is MessageState.Sent -> Text("✓", style = MaterialTheme.typography.bodySmall)
                        is MessageState.Delivered -> Text("✓✓", style = MaterialTheme.typography.bodySmall)
                        is MessageState.Read -> Text("✓✓", style = MaterialTheme.typography.bodySmall, color = Color.Blue)
                        is MessageState.Expiring -> {
                            val seconds = (message.state as MessageState.Expiring).secondsRemaining
                            Text("🕐 ${seconds}s", style = MaterialTheme.typography.bodySmall, color = Color.Red)
                        }
                        is MessageState.Failed -> Text("❌", style = MaterialTheme.typography.bodySmall)
                        else -> {}
                    }
                }
            }
        }
    }
}