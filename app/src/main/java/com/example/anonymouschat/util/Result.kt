package com.example.anonymouschat.util

/**
 * A simple sealed class for representing operation results
 */
sealed class Result<out T> {
    /**
     * Represents a successful operation
     * Contains the resulting data
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Represents a failed operation
     * Contains the exception that caused the failure
     */
    data class Error(val exception: Exception) : Result<Nothing>()
}

/**
 * Extension functions for easier Result handling
 */
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

/**
 * Execute block only if Result is Error
 */
inline fun <T> Result<T>.onError(action: (Exception) -> Unit): Result<T> {
    if (this is Result.Error) action(exception)
    return this
}

/**
 * Transform Success data to another type
 * Errors pass through unchanged
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> Result.Error(exception)
    }
}