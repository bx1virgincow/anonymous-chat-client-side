package com.example.anonymouschat.domain.usecase.connection

import com.example.anonymouschat.domain.model.ConnectionStatus
import com.example.anonymouschat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** observe connection
 * --When to use
 * 1. in UI to show connection indicator
 * 2. To trigger reconnection logic
 * 3. To disable/enable chat features based on connection
 */

class ObserveConnectionStatusUseCase @Inject constructor(
    val chatRepository: ChatRepository
) {
    operator fun invoke(): Flow<ConnectionStatus> {
        return chatRepository.observeConnectionStatus()
    }

    /** usage
     * observeConnectionUseCase().collect{status ->
     * when(status){
     * is ConnectionStatus.Connected -> showOnlineUI
     *
     * }}
     * and so on
     */

}