package com.example.anonymouschat.domain.usecase.message

import com.example.anonymouschat.domain.model.Message
import com.example.anonymouschat.domain.repository.MessageRepository
import com.example.anonymouschat.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** observer messages usecase */

class ObserveMessagesUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    operator fun invoke(params: Params): Flow<Result<List<Message>>> {
        return messageRepository.observeMessages(params.chatId)
    }

    data class Params(val chatId: String)
}