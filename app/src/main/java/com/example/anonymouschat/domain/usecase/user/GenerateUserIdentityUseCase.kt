package com.example.anonymouschat.domain.usecase.user

import com.example.anonymouschat.domain.model.User
import com.example.anonymouschat.domain.repository.UserRepository
import com.example.anonymouschat.domain.usecase.base.UseCaseNoParams
import com.example.anonymouschat.util.Result
import javax.inject.Inject


/** usecase for generating anonymous user
 * --First app launch (no existing identity)
 * --User wants to reset their identity
 */


class GenerateUserIdentityUseCase @Inject constructor(
    private val userRepository: UserRepository
) : UseCaseNoParams<User>(){
    override suspend fun execute(): Result<User> {
        // step 1: Generate new identity
        val generateResult = userRepository.registerUser()

        // step 2: if generation succeed, save it
        if(generateResult is Result.Success){
            val user = generateResult.data

            // save to local storage
            // step 3: return appropriate result
            return when(val saveResults = userRepository.saveUserIdentity(user)){
                is Result.Success -> Result.Success(user)
                is Result.Error -> Result.Error(saveResults.exception)
            }
        }

        return Result.Error((generateResult as Result.Error).exception)
    }

}