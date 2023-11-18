package com.jar89.playlistmaker.albums.domain.api

import com.jar89.playlistmaker.albums.domain.model.Playlist
import com.jar89.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun savePlaylist(playlist: Playlist)

    fun getAllPlaylists(): Flow<List<Playlist>>

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)
}