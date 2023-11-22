package com.jar89.playlistmaker.albums.ui.detail_playlist.view_model

import com.jar89.playlistmaker.albums.domain.model.Playlist
import com.jar89.playlistmaker.search.domain.model.Track

sealed interface DetailPlaylistState {

    data class Content(val playlist: Playlist, val trackList: List<Track>) : DetailPlaylistState

    data class Message(val text: String) : DetailPlaylistState

    data object PlaylistDeleted : DetailPlaylistState
}