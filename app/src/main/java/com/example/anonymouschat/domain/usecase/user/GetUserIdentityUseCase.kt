package com.example.anonymouschat.domain.usecase.user

import com.example.anonymouschat.domain.model.User
import com.example.anonymouschat.domain.repository.UserRepository
import com.example.anonymouschat.domain.usecase.base.UseCaseNoParams
import com.example.anonymouschat.util.Result
import javax.inject.Inject

/** UseCase for retrieving the current user's identity */

class GetUserIdentityUseCase @Inject constructor(
    private val userRepository: UserRepository
) : UseCaseNoParams<User>(){
    override suspend fun execute(): Result<User> {
        return userRepository.getUserIdentity()
    }

}