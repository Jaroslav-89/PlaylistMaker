package com.jar89.playlistmaker.domain.impl

import com.jar89.playlistmaker.Creator
import com.jar89.playlistmaker.domain.api.MediaPlayerInteractor
import com.jar89.playlistmaker.domain.api.MediaPlayerRepository
import com.jar89.playlistmaker.domain.entity.MediaPlayerState

class MediaPlayerInteractorImpl(private val mediaPlayerRepository: MediaPlayerRepository) : MediaPlayerInteractor {

    override fun play() {
        mediaPlayerRepository.play()
    }

    override fun pause() {
        mediaPlayerRepository.pause()
    }

    override fun destroy() {
        mediaPlayerRepository.destroy()
    }

    override fun createMediaPlayer(url: String?, completion: () -> Unit) {
        mediaPlayerRepository.preparePlayer(url, completion)
    }

    override fun getElapsedTime(): Int {
        return mediaPlayerRepository.elapsedTime()
    }

    override fun getPlayerState(): MediaPlayerState {
        return mediaPlayerRepository.playerState()
    }
}