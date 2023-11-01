package com.jar89.playlistmaker.player.ui.view_model

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class PlayerViewModel() : ViewModel() {

    private var mediaPlayer: MediaPlayer = MediaPlayer()

    private var timerJob: Job? = null

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default())

    val playerState: LiveData<PlayerState>
        get() = _playerState

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    fun onPause() {
        pausePlayer()
    }

    fun onPlayButtonClicked() {
        when (_playerState.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }

            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlayer()
            }

            else -> {}
        }
    }

    fun createPlayer(trackUrl: String?) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            _playerState.postValue(PlayerState.Prepared())
        }
        mediaPlayer.setOnCompletionListener {
            _playerState.postValue(PlayerState.Prepared())
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        _playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        _playerState.postValue(PlayerState.Paused(getCurrentPlayerPosition()))
    }

    private fun releasePlayer() {
        mediaPlayer.stop()
        mediaPlayer.release()
        _playerState.value = PlayerState.Default()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(UPDATE_DELAY)
                _playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
            }
            timerJob?.cancel()
            _playerState.postValue(PlayerState.Prepared())
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
            ?: "00:00"
    }

    companion object {
        private const val UPDATE_DELAY = 300L
    }
}