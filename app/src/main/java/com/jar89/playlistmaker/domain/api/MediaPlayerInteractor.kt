package com.jar89.playlistmaker.domain.api

import com.jar89.playlistmaker.domain.entity.MediaPlayerState

interface MediaPlayerInteractor {

    fun play()

    fun pause()

    fun destroy()

    fun createMediaPlayer(url: String?, completion: () -> Unit)

    fun getElapsedTime(): Int

    fun getPlayerState(): MediaPlayerState
}