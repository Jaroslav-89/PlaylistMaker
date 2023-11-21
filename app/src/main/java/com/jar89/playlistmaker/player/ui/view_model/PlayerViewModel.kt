package com.jar89.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar89.playlistmaker.albums.domain.api.FavoritesTracksInteractor
import com.jar89.playlistmaker.albums.domain.api.PlaylistInteractor
import com.jar89.playlistmaker.albums.domain.model.Playlist
import com.jar89.playlistmaker.player.domain.api.PlayerInteractor
import com.jar89.playlistmaker.player.domain.model.GeneralPlayerState
import com.jar89.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoritesTracksInteractor: FavoritesTracksInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var timerJob: Job? = null
    private lateinit var track: Track
    private var trackInFavorites = false
    private val _generalPlayerState = MutableLiveData<GeneralPlayerState>()
    private val _isFavorite = MutableLiveData<GeneralPlayerState>()

    val generalPlayerState: LiveData<GeneralPlayerState>
        get() = _generalPlayerState

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
    }

    fun createPlayer(newTrack: Track) {
        track = newTrack
        _generalPlayerState.postValue(GeneralPlayerState.PlayerState.Default)
        playerInteractor.createPlayer(track.previewUrl) {
            _generalPlayerState.postValue(playerInteractor.getPlayerState())
        }
    }

    fun checkFavoriteBtn() {
        viewModelScope.launch {
            favoritesTracksInteractor.checkTrackById(track.trackId)
                .collect { value ->
                    trackInFavorites = value
                    _generalPlayerState.postValue(GeneralPlayerState.FavoriteState(trackInFavorites))
                }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            if (trackInFavorites) {
                favoritesTracksInteractor.deleteTrack(track)
                checkFavoriteBtn()
            } else {
                favoritesTracksInteractor.insertTrack(track)
                checkFavoriteBtn()
            }
        }
    }

    fun onPlayButtonClicked() {
        when (_generalPlayerState.value) {
            is GeneralPlayerState.PlayerState.Playing -> {
                onPause()
            }

            is GeneralPlayerState.PlayerState.Prepared, is GeneralPlayerState.PlayerState.Paused -> {
                startPlayer()
            }

            else -> {}
        }
    }

    fun getAllPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect {
                _generalPlayerState.postValue(GeneralPlayerState.PlaylistsState.ShowPlaylists(it))
            }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        if (playlist.tracksId.contains(track.trackId.toInt())) {
            _generalPlayerState.value =
                GeneralPlayerState.PlaylistsState.AlreadyAdded(playlist.name)
        } else {
            viewModelScope.launch {
                playlistInteractor.addTrackToPlaylist(track, playlist)
                _generalPlayerState.postValue(GeneralPlayerState.PlaylistsState.WasAdded(playlist.name))
            }
        }
    }

    private fun startPlayer() {
        playerInteractor.play()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(UPDATE_DELAY)
                _generalPlayerState.postValue(playerInteractor.getPlayerState())
            }
        }
    }

    fun onPause() {
        playerInteractor.pause()
        _generalPlayerState.postValue(playerInteractor.getPlayerState())
        timerJob?.cancel()
    }

    companion object {
        private const val UPDATE_DELAY = 300L
    }
}