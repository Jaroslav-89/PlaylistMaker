package com.jar89.playlistmaker.domain.entity

sealed interface ApiResponse<T> {
    data class Success<T> (val data: T): ApiResponse<T>
    data class Error<T> (val message: String): ApiResponse<T>
}