package com.example.anonymouschat.domain.usecase.connection

import com.example.anonymouschat.domain.repository.ChatRepository
import com.example.anonymouschat.domain.usecase.base.UseCaseNoParams
import com.example.anonymouschat.util.Result
import javax.inject.Inject

/** usecase for connecting to websocket server */

class ConnectWebSocketUseCase @Inject constructor(
    val chatRepository: ChatRepository
): UseCaseNoParams<Unit>(){
    override suspend fun execute(): Result<Unit> {
        return chatRepository.connect()
    }

}