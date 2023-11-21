package com.jar89.playlistmaker.albums.ui.view_model

sealed interface CreatePlaylistState {

    data class SaveSuccess(val name: String): CreatePlaylistState

    data class EditInProgress(val isStarted: Boolean): CreatePlaylistState
}