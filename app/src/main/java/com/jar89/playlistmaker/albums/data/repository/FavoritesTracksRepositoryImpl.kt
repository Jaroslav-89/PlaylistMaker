package com.jar89.playlistmaker.albums.data.repository

import com.jar89.playlistmaker.albums.data.converters.TrackDbConvertor
import com.jar89.playlistmaker.albums.data.db.AppDatabase
import com.jar89.playlistmaker.albums.domain.db.FavoritesTracksRepository
import com.jar89.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesTracksRepositoryImpl(
    private val dataBase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : FavoritesTracksRepository {
    override suspend fun insertTrack(track: Track) {
        dataBase.favoritesTracksDao().insertTrack(trackDbConvertor.mapTrackToEntity(track))
    }

    override suspend fun deleteTrack(track: Track) {
       dataBase.favoritesTracksDao().deleteTrack(trackDbConvertor.mapTrackToEntity(track))
    }

    override fun checkTrackById(trackId: Long): Flow<Boolean> = flow {
        emit(dataBase.favoritesTracksDao().checkTrackById(trackId) != null)
    }

    override fun getFavoritesTracks(): Flow<List<Track>> = flow {
        val tracksEntityList = dataBase.favoritesTracksDao().getFavoritesTracks()
        if (tracksEntityList.isNotEmpty()) {
            emit(tracksEntityList.map { trackDbConvertor.mapEntityToTrack(it) })
        } else {
            emit(emptyList())
        }
    }
}