package com.example.anonymouschat.domain.usecase.user

import com.example.anonymouschat.domain.model.User
import com.example.anonymouschat.domain.repository.UserRepository
import com.example.anonymouschat.domain.usecase.base.UseCase
import com.example.anonymouschat.util.Result
import javax.inject.Inject

/** usecase for saving user identity to local storage */

class SaveUserIdentityUseCase @Inject constructor(
    private val userRepository: UserRepository
): UseCase<SaveUserIdentityUseCase.Params, Unit>(){

    /** parameters for this usecase */
    data class Params(val user: User)

    override suspend fun execute(params: SaveUserIdentityUseCase.Params): Result<Unit> {
        val user = params.user

        if(!user.isValid()){
            return Result.Error(
                Exception("Invalid user: missing required field")
            )
        }
        // user is valid
        return userRepository.saveUserIdentity(user)
    }

}

