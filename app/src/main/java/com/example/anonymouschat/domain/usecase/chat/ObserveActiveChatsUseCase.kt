package com.example.anonymouschat.domain.usecase.chat

import com.example.anonymouschat.domain.model.Chat
import com.example.anonymouschat.domain.repository.ChatRepository
import com.example.anonymouschat.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveActiveChatsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(): Flow<Result<List<Chat>>> {
        return chatRepository.observeActiveChats()
    }
}