package com.jar89.playlistmaker.albums.ui.view_model

import com.jar89.playlistmaker.search.domain.model.Track

sealed class FavoritesState {
    object NOTHING_TO_SHOW : FavoritesState()
    object LOADING : FavoritesState()
    data class ERROR(val message: String) : FavoritesState()
    data class Favorites(val favorites: List<Track>) : FavoritesState()
}
