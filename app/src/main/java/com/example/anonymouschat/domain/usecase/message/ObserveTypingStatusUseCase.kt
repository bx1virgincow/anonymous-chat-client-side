package com.example.anonymouschat.domain.usecase.message

import com.example.anonymouschat.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTypingStatusUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    data class Params(val chatId: String)

    operator fun invoke(params: Params): Flow<Boolean>{
        return messageRepository.observeTypingStatus(params.chatId)
    }
}