package com.jar89.playlistmaker.player.domain.api

import com.jar89.playlistmaker.player.domain.model.PlayerState

interface Player {
    fun createPlayer(trackUrl: String?, completion: () -> Unit)

    fun play()

    fun pause()

    fun release()

    fun playerState(): PlayerState
}