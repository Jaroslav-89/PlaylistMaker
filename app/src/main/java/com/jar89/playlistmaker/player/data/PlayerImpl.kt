package com.jar89.playlistmaker.player.data

import android.media.MediaPlayer
import com.jar89.playlistmaker.player.domain.api.Player
import com.jar89.playlistmaker.player.domain.model.GeneralPlayerState

class PlayerImpl(private val mediaPlayer: MediaPlayer) : Player {

    private lateinit var playerState: GeneralPlayerState

    override fun createPlayer(trackUrl: String?, completion: () -> Unit) {
        playerState = GeneralPlayerState.PlayerState.Default
        if (trackUrl != null) {
            with(mediaPlayer) {
                try {
                    setDataSource(trackUrl)
                    prepareAsync()
                } catch (e: Exception) {
                    playerState = GeneralPlayerState.PlayerState.Default
                }
                setOnPreparedListener {
                    playerState = GeneralPlayerState.PlayerState.Prepared
                    completion()
                }
                setOnCompletionListener {
                    playerState = GeneralPlayerState.PlayerState.Prepared
                    completion()
                }
            }
        } else {
            playerState = GeneralPlayerState.PlayerState.Default
            completion()
        }
    }

    override fun play() {
        mediaPlayer.start()
        playerState = GeneralPlayerState.PlayerState.Playing(elapsedTime())
    }

    override fun pause() {
        when (playerState) {
            is GeneralPlayerState.PlayerState.Default -> {}
            is GeneralPlayerState.PlayerState.Prepared -> {}
            else -> {
                mediaPlayer.pause()
                playerState = GeneralPlayerState.PlayerState.Paused(elapsedTime())
            }
        }
    }

    override fun release() {
        if (playerState != GeneralPlayerState.PlayerState.Default) {
            mediaPlayer.stop()
            mediaPlayer.release()
            playerState = GeneralPlayerState.PlayerState.Default
        }
    }

    override fun playerState(): GeneralPlayerState {
        return when (playerState) {
            is GeneralPlayerState.PlayerState.Default -> {
                GeneralPlayerState.PlayerState.Default
            }

            is GeneralPlayerState.PlayerState.Prepared -> {
                GeneralPlayerState.PlayerState.Prepared
            }

            is GeneralPlayerState.PlayerState.Playing -> {
                GeneralPlayerState.PlayerState.Playing(elapsedTime())
            }

            is GeneralPlayerState.PlayerState.Paused -> {
                GeneralPlayerState.PlayerState.Paused(elapsedTime())
            }

            else -> {
                GeneralPlayerState.PlayerState.Default
            }
        }
    }

    private fun elapsedTime(): Int {
        return mediaPlayer.currentPosition
    }
}