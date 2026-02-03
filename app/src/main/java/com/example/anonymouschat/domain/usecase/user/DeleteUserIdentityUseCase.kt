package com.example.anonymouschat.domain.usecase.user

import com.example.anonymouschat.domain.repository.UserRepository
import com.example.anonymouschat.domain.usecase.base.UseCaseNoParams
import com.example.anonymouschat.util.Result
import javax.inject.Inject

/** delete user usecase */

class DeleteUserIdentityUseCase @Inject constructor(
    private val userRepository: UserRepository
): UseCaseNoParams<Unit>(){
    override suspend fun execute(): Result<Unit> {
        return userRepository.deleteUserIdentity()
    }
}