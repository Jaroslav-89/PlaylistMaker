package com.jar89.playlistmaker.albums.domain.api

import com.jar89.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesTracksInteractor {
    suspend fun insertTrack(track: Track)

    suspend fun deleteTrack(track: Track)

    fun checkTrackById(trackId: Long): Flow<Boolean>

    fun getFavoritesTracks(): Flow<List<Track>>
}