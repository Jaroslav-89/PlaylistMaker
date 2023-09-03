package com.jar89.playlistmaker.player.domain.api

import com.jar89.playlistmaker.player.ui.view_model.PlayerState

interface PlayerInteractor {

    fun createPlayer(completion: ()->Unit)

    fun play()

    fun pause()

    fun release()

    fun getElapsedTime(): Int

    fun getPlayerState(): PlayerState
}