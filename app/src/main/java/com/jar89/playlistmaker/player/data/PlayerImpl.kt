package com.jar89.playlistmaker.player.data

import android.media.MediaPlayer
import com.jar89.playlistmaker.player.domain.api.Player
import com.jar89.playlistmaker.player.ui.view_model.PlayerState

class PlayerImpl(private val trackUrl: String?) : Player {

    private var playerState = PlayerState.STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()

    override fun createPlayer(completion: () -> Unit) {
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
        mediaPlayer.pause()
        playerState = PlayerState.STATE_PAUSED
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