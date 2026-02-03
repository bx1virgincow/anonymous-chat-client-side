package com.example.anonymouschat.domain.usecase.message

import com.example.anonymouschat.domain.model.Message
import com.example.anonymouschat.domain.repository.MessageRepository
import com.example.anonymouschat.domain.usecase.base.UseCase
import com.example.anonymouschat.util.Result
import javax.inject.Inject

/** receive message usecase */

class ReceiveMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository
): UseCase<ReceiveMessageUseCase.Params, List<Message>>(){
    override suspend fun execute(params: ReceiveMessageUseCase.Params): Result<List<Message>> {
        val chatId = params.chatId

        if(chatId.isBlank()){
            return Result.Error(
                Exception("Chat Id cannot be empty")
            )
        }
        return messageRepository.getMessages(params.chatId)
    }

    data class Params(val chatId: String)
}