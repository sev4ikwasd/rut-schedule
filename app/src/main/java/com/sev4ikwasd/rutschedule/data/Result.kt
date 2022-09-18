package com.sev4ikwasd.rutschedule.data

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

sealed class CacheableResult<out R> {
    data class SuccessfullyUpdated<out T>(val data: T) : CacheableResult<T>()
    data class NotUpdated<out T>(val data: T) : CacheableResult<T>()
    data class Error(val exception: Exception) : CacheableResult<Nothing>()
}

fun <T> Result<T>.toCacheableResult(): CacheableResult<T> {
    return when (this) {
        is Result.Success -> CacheableResult.SuccessfullyUpdated(data)
        is Result.Error -> CacheableResult.Error(exception)
    }
}