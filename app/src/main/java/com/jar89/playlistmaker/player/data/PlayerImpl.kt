package com.jar89.playlistmaker.player.data

import android.media.MediaPlayer
import com.jar89.playlistmaker.player.domain.api.Player
import com.jar89.playlistmaker.player.domain.model.PlayerState

class PlayerImpl(trackUrl: String?) : Player {

    private var playerState = PlayerState.STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()

    init {
        preparePlayer(trackUrl)
    }

    private fun preparePlayer(url: String?) {
        if (url != null) {
            with(mediaPlayer) {
                try {
                    setDataSource(url)
                    prepareAsync()
                } catch (e: Exception) {
                    playerState = PlayerState.STATE_ERROR
                }
                setOnPreparedListener {
                    playerState = PlayerState.STATE_PREPARED
                }
                setOnCompletionListener {
                    playerState = PlayerState.STATE_PREPARED
                }
            }
        } else {
            playerState = PlayerState.STATE_ERROR
        }
    }

    override fun play() {
        mediaPlayer.start()
        playerState = PlayerState.STATE_PLAYING
    }

    override fun pause() {
        mediaPlayer.pause()
        playerState = PlayerState.STATE_PAUSED
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun elapsedTime(): Int {
        return mediaPlayer.currentPosition
    }

    override fun playerState(): PlayerState {
        return playerState
    }
}