package com.jar89.playlistmaker.albums.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoritesViewModel : ViewModel() {
    val _state = MutableLiveData<FavoritesState>(FavoritesState.NOTHING_TO_SHOW)

    val state: LiveData<FavoritesState>
        get() = _state
}

