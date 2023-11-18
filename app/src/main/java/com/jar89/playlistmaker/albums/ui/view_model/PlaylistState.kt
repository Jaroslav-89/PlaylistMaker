package com.jar89.playlistmaker.albums.ui.view_model

import com.jar89.playlistmaker.albums.domain.model.Playlist

sealed interface PlaylistState {
    data object Empty : PlaylistState
    data class Content(val playlists: List<Playlist>) : PlaylistState
}
