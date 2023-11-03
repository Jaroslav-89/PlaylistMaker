package com.jar89.playlistmaker.player.data

import android.media.MediaPlayer
import com.jar89.playlistmaker.player.domain.api.Player
import com.jar89.playlistmaker.player.domain.model.PlayerState

class PlayerImpl(private val mediaPlayer: MediaPlayer) : Player {

    private lateinit var playerState: PlayerState

    override fun createPlayer(trackUrl: String?, completion: () -> Unit) {
        playerState = PlayerState.Default()
        if (trackUrl != null) {
            with(mediaPlayer) {
                try {
                    setDataSource(trackUrl)
                    prepareAsync()
                } catch (e: Exception) {
                    playerState = PlayerState.Default()
                }
                setOnPreparedListener {
                    playerState = PlayerState.Prepared()
                    completion()
                }
                setOnCompletionListener {
                    playerState = PlayerState.Prepared()
                    completion()
                }
            }
        } else {
            playerState = PlayerState.Default()
            completion()
        }
    }

    override fun play() {
        mediaPlayer.start()
        playerState = PlayerState.Playing(elapsedTime())
    }

    override fun pause() {
        if (playerState != PlayerState.Default() && playerState != PlayerState.Prepared()) {
            mediaPlayer.pause()
            playerState = PlayerState.Paused(elapsedTime())
        }
    }

    override fun release() {
        if (playerState != PlayerState.Default()) {
            mediaPlayer.stop()
            mediaPlayer.release()
            playerState = PlayerState.Default()
        }
    }

    override fun playerState(): PlayerState {
        return when (playerState) {
            is PlayerState.Default -> {
                PlayerState.Default()
            }

            is PlayerState.Prepared -> {
                PlayerState.Prepared()
            }

            is PlayerState.Playing -> {
                PlayerState.Playing(elapsedTime())
            }

            is PlayerState.Paused -> {
                PlayerState.Paused(elapsedTime())
            }
        }
    }

    private fun elapsedTime(): Int {
        return mediaPlayer.currentPosition
    }
}