package com.example.anonymouschat.presentation.screens.home

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anonymouschat.presentation.components.*

/**
 * Home screen - main screen showing user info and chat list
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToStartChat: () -> Unit,
    onNavigateToChat: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Anonymous Chat") }
                )

                // Connection status bar
                ConnectionStatusBar(connectionStatus = state.connectionStatus)
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(HomeEvent.OnStartChatClick); onNavigateToStartChat() }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Start new chat"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // User code card
            state.user?.let { user ->
                UserCodeCard(
                    user = user,
                    onShareClick = {
                        // Share user code
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Chat with me anonymously! Use this code: ${user.fullShareable}"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share your code"))
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Chat list header
            Text(
                text = "Active Chats",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Chat list
            if (state.isLoading) {
                LoadingIndicator()
            } else if (state.chats.isEmpty()) {
                EmptyState(
                    message = "No chats yet",
                    subMessage = "Tap + to start a new chat"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.chats) { chat ->
                        ChatListItem(
                            chat = chat,
                            onClick = {
                                viewModel.onEvent(HomeEvent.OnChatClick(chat.chatId))
                                onNavigateToChat(chat.chatId)
                            }
                        )
                    }
                }
            }

            // Error message
            state.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}