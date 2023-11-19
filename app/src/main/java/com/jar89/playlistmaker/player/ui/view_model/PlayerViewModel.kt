package com.jar89.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar89.playlistmaker.albums.domain.api.FavoritesTracksInteractor
import com.jar89.playlistmaker.albums.domain.api.PlaylistInteractor
import com.jar89.playlistmaker.albums.domain.model.Playlist
import com.jar89.playlistmaker.player.domain.api.PlayerInteractor
import com.jar89.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track,
    private val playerInteractor: PlayerInteractor,
    private val favoritesTracksInteractor: FavoritesTracksInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var timerJob: Job? = null
    private var trackInFavorites = false

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState>
        get() = _playerState

    private val _isFavorite = MutableLiveData<FavoriteState>()
    val isFavorite: LiveData<FavoriteState>
        get() = _isFavorite

    private val _playlistsState = MutableLiveData<PlaylistsState>()
    val playlistsState: LiveData<PlaylistsState>
        get() = _playlistsState

    init {
        track.previewUrl?.let {
            playerInteractor.createPlayer(it) {
                _playerState.postValue(playerInteractor.getPlayerState())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
    }

    fun checkFavoriteBtn() {
        viewModelScope.launch {
            favoritesTracksInteractor.checkTrackById(track!!.trackId)
                .collect { value ->
                    trackInFavorites = value
                    _isFavorite.postValue(FavoriteState(trackInFavorites))
                }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
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

            is PlayerState.Default -> {
            }

            else -> {
            }
        }
    }

    fun getAllPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect {
                _playlistsState.postValue(PlaylistsState.ShowPlaylists(it))
            }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        if (playlist.tracksId.contains(track!!.trackId.toInt())) {
            _playlistsState.value =
                PlaylistsState.AlreadyAdded(playlist.name)
        } else {
            viewModelScope.launch {
                playlistInteractor.addTrackToPlaylist(track!!, playlist)
                _playlistsState.postValue(PlaylistsState.WasAdded(playlist.name))
            }
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