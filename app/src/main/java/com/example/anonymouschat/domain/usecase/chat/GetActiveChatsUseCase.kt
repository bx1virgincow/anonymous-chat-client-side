package com.example.anonymouschat.domain.usecase.chat

import com.example.anonymouschat.domain.model.Chat
import com.example.anonymouschat.domain.repository.ChatRepository
import com.example.anonymouschat.domain.usecase.base.UseCaseNoParams
import com.example.anonymouschat.util.Result
import javax.inject.Inject

/** usecase to get active chats */

class GetActiveChatsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
): UseCaseNoParams<List<Chat>>(){
    override suspend fun execute(): Result<List<Chat>> {
       return chatRepository.getActiveChats()
    }
}