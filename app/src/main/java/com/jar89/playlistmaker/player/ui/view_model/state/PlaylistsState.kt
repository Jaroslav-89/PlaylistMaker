package com.jar89.playlistmaker.player.ui.view_model.state

import com.jar89.playlistmaker.albums.domain.model.Playlist

sealed class PlaylistsState {
    data class WasAdded(val playlistName: String) : PlaylistsState()

    data class AlreadyAdded(val playlistName: String) : PlaylistsState()

    data class ShowPlaylists(val playlists: List<Playlist>) : PlaylistsState()
}
