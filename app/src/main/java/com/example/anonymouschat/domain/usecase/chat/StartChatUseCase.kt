package com.example.anonymouschat.domain.usecase.chat

import com.example.anonymouschat.domain.model.Chat
import com.example.anonymouschat.domain.repository.ChatRepository
import com.example.anonymouschat.domain.usecase.base.UseCase
import com.example.anonymouschat.util.Constants
import com.example.anonymouschat.util.Result
import javax.inject.Inject

/** start chat usecase */

class StartChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) : UseCase<StartChatUseCase.Params, Chat>() {
    override suspend fun execute(params: Params): Result<Chat> {
        val shareable = params.targetShareable.trim()

        if (!Constants.SHARE_CODE_PATTERN.matches(shareable)) {
            return Result.Error(
                Exception("Invalid shareable format. Expected: DisplayName#CODE")
            )
        }
        return chatRepository.startChat(shareable)
    }

    data class Params(val targetShareable: String)


}