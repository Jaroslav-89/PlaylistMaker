package com.jar89.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar89.playlistmaker.player.domain.api.PlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {

    private var timerJob: Job? = null

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default())

    val playerState: LiveData<PlayerState>
        get() = _playerState

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
    }

    fun createPlayer(trackUrl: String?) {
        playerInteractor.createPlayer(trackUrl) {
            _playerState.postValue(playerInteractor.getPlayerState())
        }
    }

    fun onPlayButtonClicked() {
        when (_playerState.value) {
            is PlayerState.Playing -> {
                onPause()
            }

            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlayer()
            }

            else -> {}
        }
    }

    private fun startPlayer() {
        playerInteractor.play()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(UPDATE_DELAY)
                _playerState.postValue(playerInteractor.getPlayerState())
            }
        }
    }

    fun onPause() {
        playerInteractor.pause()
        _playerState.postValue(playerInteractor.getPlayerState())
        timerJob?.cancel()
    }

    companion object {
        private const val UPDATE_DELAY = 300L
    }
}