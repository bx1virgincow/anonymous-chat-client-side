package com.example.anonymouschat.domain.usecase.base

import com.example.anonymouschat.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** base class for UseCases that return a
 * single result
 */

abstract class UseCase<in Params, out ReturnType>(

    /** Coroutine dispatcher for running the use case */
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
){
    /** abstract method to be implemented by concrete
     * usecase
     * Contains the actual business logic
     */

    protected abstract suspend fun execute(params: Params): Result<ReturnType>

    /** invoke operator - allow calling UseCase like a function */
    suspend operator fun invoke(params: Params): Result<ReturnType>{
        return withContext(dispatcher){
            execute(params)
        }
    }
}

/** Special UseCase for operations that do not need input parameters
 *
 */

abstract class UseCaseNoParams<out ReturnType>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
){
    protected abstract suspend fun execute(): Result<ReturnType>

    suspend operator fun invoke(): Result<ReturnType>{
        return withContext(dispatcher){
            execute()
        }
    }
}