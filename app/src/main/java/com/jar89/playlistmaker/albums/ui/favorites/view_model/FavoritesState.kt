package com.jar89.playlistmaker.albums.ui.favorites.view_model

import com.jar89.playlistmaker.search.domain.model.Track

sealed interface FavoritesState {
    data object Empty : FavoritesState
    data class Content(val favorites: List<Track>) : FavoritesState
}
