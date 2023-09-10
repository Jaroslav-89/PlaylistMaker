package com.jar89.playlistmaker.player.domain.impl

import com.jar89.playlistmaker.player.domain.api.PlayerInteractor
import com.jar89.playlistmaker.player.domain.api.Player
import com.jar89.playlistmaker.player.ui.view_model.PlayerState

class PlayerInteractorImpl(private val player: Player) :
    PlayerInteractor {
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

    override fun getElapsedTime(): Int {
        return player.elapsedTime()
    }

    override fun getPlayerState(): PlayerState {
        return player.playerState()
    }
}