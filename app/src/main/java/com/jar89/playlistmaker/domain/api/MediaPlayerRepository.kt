package com.jar89.playlistmaker.domain.api

import com.jar89.playlistmaker.domain.entity.MediaPlayerState

interface MediaPlayerRepository {

    fun play()

    fun pause()

    fun destroy()

    fun preparePlayer(url: String?, completion: () -> Unit)

    fun elapsedTime(): Int

    fun playerState(): MediaPlayerState
}