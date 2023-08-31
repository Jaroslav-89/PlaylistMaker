package com.jar89.playlistmaker.player.domain.impl

import androidx.lifecycle.LiveData
import com.jar89.playlistmaker.player.domain.api.PlayerInteractor
import com.jar89.playlistmaker.player.domain.api.Player
import com.jar89.playlistmaker.player.domain.model.PlayerState

class PlayerInteractorImpl(private val player: Player) :
    PlayerInteractor {

    override fun play() {
        player.play()
    }

    override fun pause() {
        player.pause()
    }

    override fun release() {
        player.release()
    }

    override fun getElapsedTime(): Int {
        return player.elapsedTime()
    }

    override fun getPlayerState(): LiveData<PlayerState> {
        return player.playerState()
    }
}