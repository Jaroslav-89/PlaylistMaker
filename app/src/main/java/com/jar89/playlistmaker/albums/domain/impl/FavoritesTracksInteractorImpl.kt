package com.jar89.playlistmaker.albums.domain.impl

import com.jar89.playlistmaker.albums.domain.db.FavoritesTracksInteractor
import com.jar89.playlistmaker.albums.domain.db.FavoritesTracksRepository
import com.jar89.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class FavoritesTracksInteractorImpl(
    val repository: FavoritesTracksRepository
) : FavoritesTracksInteractor {
    override suspend fun insertTrack(track: Track) {
        repository.insertTrack(track)
    }

    override suspend fun deleteTrack(track: Track) {
        repository.deleteTrack(track)
    }

    override fun checkTrackById(trackId: Long): Flow<Boolean> {
        return repository.checkTrackById(trackId)
    }

    override fun getFavoritesTracks(): Flow<List<Track>> {
        return repository.getFavoritesTracks()
    }
}