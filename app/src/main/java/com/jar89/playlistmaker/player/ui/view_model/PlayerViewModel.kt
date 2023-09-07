package com.jar89.playlistmaker.player.ui.view_model

import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jar89.playlistmaker.player.domain.api.PlayerInteractor
import java.util.Locale

class PlayerViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {

    companion object {
        private const val UPDATE_DELAY = 100L
        private const val INITIAL_TIME = "00:00"
    }

    private val looper = Looper.getMainLooper()
    private val handler = Handler(looper)
    private val timerRunnable = Runnable { updateTimer() }

    private val _playerState = MutableLiveData<PlayerState>()
    private val _elapsedTime = MutableLiveData<String>()

    val playerState: LiveData<PlayerState>
        get() = _playerState

    val elapsedTime: LiveData<String>
        get() = _elapsedTime

    fun createPlayer(trackUrl: String?) {
        playerInteractor.createPlayer(trackUrl) {
            _playerState.postValue(playerInteractor.getPlayerState())
        }
        updateTimer()
    }

    fun playbackControl() {
        when (playerState.value) {
            PlayerState.STATE_PLAYING -> {
                pause()
            }

            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                play()
            }

            else -> return
        }
    }

    private fun play() {
        playerInteractor.play()
        updatePlayerState()
        updateTimer()
    }

    fun pause() {
        playerInteractor.pause()
        updatePlayerState()
        updateTimer()
    }

    fun updatePlayerState() {
        _playerState.value = playerInteractor.getPlayerState()
    }

    private fun updateTimer() {
        when (playerState.value) {
            PlayerState.STATE_PLAYING -> {
                _elapsedTime.value = getCurrentPosition(playerInteractor.getElapsedTime())
                handler.postDelayed(timerRunnable, UPDATE_DELAY)
            }

            PlayerState.STATE_PAUSED -> {
                handler.removeCallbacks(timerRunnable)
            }

            else -> {
                handler.removeCallbacks(timerRunnable)
                _elapsedTime.value = INITIAL_TIME
            }
        }
    }

    private fun getCurrentPosition(time: Int) =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    private fun releasePlayer() {
        playerInteractor.release()
        updateTimer()
    }
}