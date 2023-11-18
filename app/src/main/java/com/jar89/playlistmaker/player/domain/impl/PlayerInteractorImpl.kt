package com.jar89.playlistmaker.player.domain.impl

import com.jar89.playlistmaker.player.domain.api.Player
import com.jar89.playlistmaker.player.domain.api.PlayerInteractor
import com.jar89.playlistmaker.player.domain.model.GeneralPlayerState

class PlayerInteractorImpl(private val player: Player) : PlayerInteractor {
    override fun createPlayer(trackUrl: String?, completion: () -> Unit) {
        player.createPlayer(trackUrl, completion)
    }

    override fun play() {
        player.play()
    }

    override fun pause() {
        player.pause()
    }

    override fun release() {
        player.release()
    }

    override fun getPlayerState(): GeneralPlayerState {
        return player.playerState()
    }
}