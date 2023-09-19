package com.jar89.playlistmaker.player.data

import android.media.MediaPlayer
import com.jar89.playlistmaker.player.domain.api.Player
import com.jar89.playlistmaker.player.ui.view_model.PlayerState

class PlayerImpl(private val mediaPlayer: MediaPlayer) : Player {

    private var playerState = PlayerState.STATE_DEFAULT
    override fun createPlayer(trackUrl: String?, completion: () -> Unit) {
        if (trackUrl != null) {
            with(mediaPlayer) {
                try {
                    setDataSource(trackUrl)
                    prepareAsync()
                } catch (e: Exception) {
                    playerState = PlayerState.STATE_ERROR
                }
                setOnPreparedListener {
                    playerState = PlayerState.STATE_PREPARED
                    completion()
                }
                setOnCompletionListener {
                    playerState = PlayerState.STATE_PREPARED
                    completion()
                }
            }
        } else {
            playerState = PlayerState.STATE_ERROR
            completion()
        }
    }

    override fun play() {
        mediaPlayer.start()
        playerState = PlayerState.STATE_PLAYING
    }

    override fun pause() {
        if (playerState == PlayerState.STATE_PLAYING) {
            mediaPlayer.pause()
            playerState = PlayerState.STATE_PAUSED
        } else {
            release()
        }
    }

    override fun release() {
        mediaPlayer.release()
        playerState = PlayerState.STATE_DEFAULT
    }

    override fun elapsedTime(): Int {
        return mediaPlayer.currentPosition
    }

    override fun playerState(): PlayerState {
        return playerState
    }
}