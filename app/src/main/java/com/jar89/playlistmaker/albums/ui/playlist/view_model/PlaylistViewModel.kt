package com.jar89.playlistmaker.albums.ui.playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar89.playlistmaker.albums.domain.api.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {
    private val _state = MutableLiveData<PlaylistState>()

    val state: LiveData<PlaylistState>
        get() = _state

    fun getAllPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect {
                if (it.isEmpty()) {
                    _state.postValue(PlaylistState.Empty)
                } else {
                    _state.postValue(PlaylistState.Content(it))
                }
            }
        }
    }
}