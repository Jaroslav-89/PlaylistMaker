package com.jar89.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar89.playlistmaker.albums.domain.db.FavoritesTracksInteractor
import com.jar89.playlistmaker.player.domain.api.PlayerInteractor
import com.jar89.playlistmaker.player.domain.model.PlayerState
import com.jar89.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoritesTracksInteractor: FavoritesTracksInteractor
) : ViewModel() {

    private var timerJob: Job? = null
    private var track: Track? = null
    private var trackInFavorites = false

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    private val _isFavorite = MutableLiveData<Boolean>()

    val playerState: LiveData<PlayerState>
        get() = _playerState

    val isFavorite: LiveData<Boolean>
        get() = _isFavorite

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
    }

    fun createPlayer(newTrack: Track) {
        track = newTrack
        playerInteractor.createPlayer(track!!.previewUrl) {
            _playerState.postValue(playerInteractor.getPlayerState())
        }
    }

    fun checkFavoriteBtn() {
        viewModelScope.launch(Dispatchers.IO) {
            track?.trackId.let { id ->
                favoritesTracksInteractor.checkTrackById(id!!)
                    .collect { value ->
                        trackInFavorites = value
                        _isFavorite.postValue(trackInFavorites)
                    }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch(Dispatchers.IO) {
            if (trackInFavorites) {
                favoritesTracksInteractor.deleteTrack(track!!)
                checkFavoriteBtn()
            } else {
                favoritesTracksInteractor.insertTrack(track!!)
                checkFavoriteBtn()
            }
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