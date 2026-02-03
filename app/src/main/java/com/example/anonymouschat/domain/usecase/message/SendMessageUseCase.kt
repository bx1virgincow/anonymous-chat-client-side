package com.example.anonymouschat.domain.usecase.message

import com.example.anonymouschat.domain.model.Message
import com.example.anonymouschat.domain.repository.MessageRepository
import com.example.anonymouschat.domain.usecase.base.UseCase
import com.example.anonymouschat.util.Constants
import com.example.anonymouschat.util.Result
import javax.inject.Inject


/** send message usecase */

class SendMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) : UseCase<SendMessageUseCase.Params, Message>() {
    override suspend fun execute(params: Params): Result<Message> {
        val message = params.message

        // message cannot be empty
        if (message.content.isBlank()) {
            return Result.Error(Exception("Message cannot be empty"))
        }

        // message cannot exceed max length
        if (message.content.length > Constants.MAX_MESSAGE_LENGTH) {
            return Result.Error(
                Exception("Message too long. Maximum ${Constants.MAX_MESSAGE_LENGTH} characters")
            )
        }
        // message is valid
        return messageRepository.sendMessage(message)
    }
    data class Params(val message: Message)
}