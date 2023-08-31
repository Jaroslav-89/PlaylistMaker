package com.jar89.playlistmaker.player.data

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jar89.playlistmaker.player.domain.api.Player
import com.jar89.playlistmaker.player.domain.model.PlayerState

class PlayerImpl(trackUrl: String?) : Player {

    private var playerState = MutableLiveData<PlayerState>(PlayerState.STATE_DEFAULT)
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
                    playerState.value = PlayerState.STATE_ERROR
                }
                setOnPreparedListener {
                    playerState.value = PlayerState.STATE_PREPARED
                }
                setOnCompletionListener {
                    playerState.value = PlayerState.STATE_PREPARED
                }
            }
        } else {
            playerState.value = PlayerState.STATE_ERROR
        }
    }

    override fun play() {
        mediaPlayer.start()
        playerState.value = PlayerState.STATE_PLAYING
    }

    override fun pause() {
        mediaPlayer.pause()
        playerState.value = PlayerState.STATE_PAUSED
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun elapsedTime(): Int {
        return mediaPlayer.currentPosition
    }

    override fun playerState(): LiveData<PlayerState> {
        return playerState
    }
}