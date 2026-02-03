package com.example.anonymouschat.domain.usecase.user

import com.example.anonymouschat.domain.model.User
import com.example.anonymouschat.domain.repository.UserRepository
import com.example.anonymouschat.domain.usecase.base.UseCaseNoParams
import com.example.anonymouschat.util.Result
import javax.inject.Inject

class RequestUserIdentityFromServer @Inject constructor(
    private val userRepository: UserRepository
):UseCaseNoParams<User>(){
    override suspend fun execute(): Result<User> {
       return userRepository.requestUserIdentityFromServer()
    }
}