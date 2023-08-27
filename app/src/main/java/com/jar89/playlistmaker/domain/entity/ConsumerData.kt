package com.jar89.playlistmaker.domain.entity

sealed interface ConsumerData<T> {
    data class Data<T>(val value: T) : ConsumerData<T>
    data class Error<T>(val message: String) : ConsumerData<T>
}