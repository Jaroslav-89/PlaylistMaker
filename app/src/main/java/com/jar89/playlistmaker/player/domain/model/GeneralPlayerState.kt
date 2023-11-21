package com.jar89.playlistmaker.player.domain.model

import com.jar89.playlistmaker.albums.domain.model.Playlist


sealed interface GeneralPlayerState {
    sealed class PlayerState(
        val isPlayButtonEnabled: Boolean,
        val buttonIsPlay: Boolean,
        val progress: Int
    ) : GeneralPlayerState {
        data object Default : PlayerState(false, true, 0)
        data object Prepared : PlayerState(true, true, 0)
        class Playing(progress: Int) : PlayerState(true, false, progress)
        class Paused(progress: Int) : PlayerState(true, true, progress)
    }

    data class FavoriteState(val isFavorite: Boolean) : GeneralPlayerState

    sealed class PlaylistsState : GeneralPlayerState {
        data class WasAdded(val playlistName: String) : PlaylistsState()

        data class AlreadyAdded(val playlistName: String) : PlaylistsState()

        data class ShowPlaylists(val playlists: List<Playlist>) : PlaylistsState()
    }
}


