package com.example.anonymouschat.domain.usecase.chat

import com.example.anonymouschat.domain.repository.ChatRepository
import com.example.anonymouschat.domain.repository.MessageRepository
import com.example.anonymouschat.domain.usecase.base.UseCase
import com.example.anonymouschat.util.Result
import javax.inject.Inject

/** delete chat usecase */

class DeleteChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository
): UseCase<DeleteChatUseCase.Params, Unit>(){
    override suspend fun execute(params: Params): Result<Unit> {

        // delete all messages
        val deleteMessagesResults = messageRepository.deleteAllMessages(params.chatId)

        if(deleteMessagesResults is Result.Error){
            return deleteMessagesResults
        }

        // delete the chat
        return chatRepository.deleteChat(params.chatId)
    }

    data class Params(val chatId: String)
}