package com.jar89.playlistmaker.albums.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistViewModel : ViewModel() {
    val _state = MutableLiveData<PlaylistState>(PlaylistState.NOTHING_TO_SHOW)

    val state: LiveData<PlaylistState>
        get() = _state
}