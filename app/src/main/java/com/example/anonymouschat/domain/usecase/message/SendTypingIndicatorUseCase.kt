package com.example.anonymouschat.domain.usecase.message

import com.example.anonymouschat.domain.repository.MessageRepository
import com.example.anonymouschat.domain.usecase.base.UseCase
import com.example.anonymouschat.util.Result
import javax.inject.Inject

class SendTypingIndicatorUseCase @Inject constructor(
    private val messageRepository: MessageRepository
): UseCase<SendTypingIndicatorUseCase.Params, Unit>() {
    override suspend fun execute(params: Params): Result<Unit> {
        return messageRepository.sendTypingIndicator(params.chatId, params.isTyping)
    }

    data class Params(val chatId: String, val isTyping: Boolean)


}