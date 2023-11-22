package com.jar89.playlistmaker.albums.ui.create_playlist.view_model

import com.jar89.playlistmaker.albums.domain.model.Playlist

sealed interface CreatePlaylistState {

    data class SaveSuccess(val name: String): CreatePlaylistState

    data class EditInProgress(val isStarted: Boolean): CreatePlaylistState

    data class LoadPlaylist(val playlist: Playlist): CreatePlaylistState
}