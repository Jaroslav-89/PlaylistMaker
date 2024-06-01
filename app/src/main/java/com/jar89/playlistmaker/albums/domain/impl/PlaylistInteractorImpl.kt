package com.jar89.playlistmaker.albums.domain.impl

import com.jar89.playlistmaker.albums.domain.api.PlaylistInteractor
import com.jar89.playlistmaker.albums.domain.api.PlaylistRepository
import com.jar89.playlistmaker.albums.domain.model.Playlist
import com.jar89.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistInteractorImpl(private val repository: PlaylistRepository) : PlaylistInteractor {
    override suspend fun savePlaylist(playlist: Playlist) {
        repository.savePlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        repository.deletePlaylist(playlistId)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        repository.getAllPlaylists().collect {
            emit(it)
        }
    }

    override suspend fun getPlaylistById(playlistId: Int): Playlist {
        return repository.getPlaylistById(playlistId)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        repository.addTrackToPlaylist(track, playlist)
    }

    override suspend fun deleteTrackFromPlaylist(track: Track, playlist: Playlist) {
        repository.deleteTrackFromPlaylist(track, playlist)
    }

    override fun getTracksFromPlaylist(tracksId: List<Int>): Flow<List<Track>> {
        return flow {
            repository.getTracksFromPlaylist(tracksId).collect {
                emit(it)
            }
        }
    }

    override fun sharePlaylist(playlistDescription: String) {
        repository.sharePlaylist(playlistDescription)
    }
}