package com.jar89.playlistmaker.albums.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar89.playlistmaker.albums.domain.db.FavoritesTracksInteractor
import kotlinx.coroutines.launch

class FavoritesViewModel(private val favoritesTracksInteractor: FavoritesTracksInteractor) :
    ViewModel() {
    val _state = MutableLiveData<FavoritesState>()

    val state: LiveData<FavoritesState>
        get() = _state

    fun getFavoritesTracks() {
        viewModelScope.launch {
            favoritesTracksInteractor.getFavoritesTracks().collect() {
                if (it.isEmpty()) {
                    _state.postValue(FavoritesState.Empty)
                } else {
                    _state.postValue(FavoritesState.Content(it))
                }
            }
        }
    }
}

