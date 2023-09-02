package com.jar89.playlistmaker.player.domain.api

import com.jar89.playlistmaker.player.domain.model.PlayerState

interface PlayerInteractor {

    fun play()

    fun pause()

    fun release()

    fun getElapsedTime(): Int

    fun getPlayerState(): PlayerState
}