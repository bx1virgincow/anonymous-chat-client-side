package com.example.anonymouschat.domain.usecase.message

import com.example.anonymouschat.domain.repository.MessageRepository
import com.example.anonymouschat.domain.usecase.base.UseCase
import com.example.anonymouschat.util.Result
import javax.inject.Inject

/** mark messages as read usecase */

class MarkMessageAsReadUseCase @Inject constructor(
    private val messageRepository: MessageRepository
): UseCase<MarkMessageAsReadUseCase.Params, Unit>(){
    override suspend fun execute(params: Params): Result<Unit> {
        return messageRepository.markMessageAsRead(params.messageId)
    }

    data class Params(val messageId: String)


}