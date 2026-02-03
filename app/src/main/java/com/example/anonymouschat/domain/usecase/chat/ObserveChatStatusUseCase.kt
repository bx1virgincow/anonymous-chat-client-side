package com.example.anonymouschat.domain.usecase.chat

import com.example.anonymouschat.domain.model.Chat
import com.example.anonymouschat.domain.repository.ChatRepository
import com.example.anonymouschat.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveChatStatusUseCase @Inject constructor(
    private val chatRepository: ChatRepository
){
    data class Params(val chatId: String)
    operator fun invoke(params: Params): Flow<Result<Chat>>{
        return chatRepository.observeChat(params.chatId)
    }
}