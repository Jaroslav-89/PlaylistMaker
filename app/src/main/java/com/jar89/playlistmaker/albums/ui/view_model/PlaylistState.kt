package com.jar89.playlistmaker.albums.ui.view_model

import com.jar89.playlistmaker.albums.domain.model.PlayList

sealed class PlaylistState {
    object NOTHING_TO_SHOW : PlaylistState()
    object LOADING : PlaylistState()
    data class ERROR(val message: String) : PlaylistState()
    data class PLAYLISTS(val playlists: List<PlayList>) : PlaylistState()
}
