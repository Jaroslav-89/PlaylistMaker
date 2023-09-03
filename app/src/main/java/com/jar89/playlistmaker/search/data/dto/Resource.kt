package com.jar89.playlistmaker.search.data.dto

sealed class Resource<T>(val data: T? = null, val errorType: ErrorType? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(type: ErrorType, data: T? = null) : Resource<T>(data, type)
}

enum class ErrorType {
    NO_INTERNET,
    SERVER_ERROR
}
