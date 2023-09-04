package com.jar89.playlistmaker.player.domain.api

import com.jar89.playlistmaker.player.ui.view_model.PlayerState

interface Player {

    fun createPlayer(completion: () -> Unit)

    fun play()

    fun pause()

    fun release()

    fun elapsedTime(): Int

    fun playerState(): PlayerState
}