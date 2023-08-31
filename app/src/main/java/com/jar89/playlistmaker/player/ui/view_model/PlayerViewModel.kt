package com.jar89.playlistmaker.player.ui.view_model

import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jar89.playlistmaker.creator.Creator
import com.jar89.playlistmaker.player.domain.model.PlayerState
import java.util.Locale

class PlayerViewModel(trackUrl: String?) : ViewModel() {

    companion object {
        private const val UPDATE_DELAY = 100L
        private const val INITIAL_TIME = "00:00"

        fun getViewModelFactory(trackUrl: String?): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(trackUrl)
            }
        }
    }

    private val playerInteractor = Creator.providePlayerInteractor(trackUrl)
    val playerState = playerInteractor.getPlayerState()

    private val looper = Looper.getMainLooper()
    private val handler = Handler(looper)
    private val timerRunnable = Runnable { updateTimer() }

    private val _elapsedTime = MutableLiveData<String>(INITIAL_TIME)

    val elapsedTime: LiveData<String>
        get() = _elapsedTime

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
        updateTimer()
    }

    private fun play() {
        playerInteractor.play()
    }

    fun pause() {
        playerInteractor.pause()
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