package com.jar89.playlistmaker.domain

import com.jar89.playlistmaker.domain.entity.ConsumerData

interface Consumer<T> {
    fun consume(data: ConsumerData<T>)
}