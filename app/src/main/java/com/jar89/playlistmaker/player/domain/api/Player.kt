package com.jar89.playlistmaker.player.domain.api

import com.jar89.playlistmaker.player.domain.model.PlayerState

interface Player {

    fun play()

    fun pause()

    fun release()

    fun elapsedTime(): Int

    fun playerState(): PlayerState
}