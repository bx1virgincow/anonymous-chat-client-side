package com.example.anonymouschat.util


/**
 * a generic wrapper for handling different states of data/operation
 */

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    // this represent whenever an operation/data is successful
    class Success<T>(data: T) : Resource<T>(data)

    // this represents an error state
    class Error<T>(message: String?, data: T? = null) : Resource<T>(data, message)

    // this represent in motion or loading state
    class Loading<T>(data: T? = null) : Resource<T>(data)

    /**
     * Methods to check if an operation is in various
     * states (Extension)
     */

    // for success
    fun <T> Resource<T>.isSuccess(): Boolean = this is Resource.Success

    // for error
    fun <T> Resource<T>.isError(): Boolean = this is Resource.Error

    // for loading
    fun <T> Resource<T>.isLoading(): Boolean = this is Resource.Loading

    // get data or null
    fun <T> Resource<T>.getDataOrNull(): T? = if (this is Resource.Success) data else null

}