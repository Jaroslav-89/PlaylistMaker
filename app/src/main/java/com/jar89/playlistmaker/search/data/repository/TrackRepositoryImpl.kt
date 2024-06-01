package com.jar89.playlistmaker.search.data.repository

import com.jar89.playlistmaker.search.data.dto.ErrorType
import com.jar89.playlistmaker.search.data.dto.Resource
import com.jar89.playlistmaker.search.data.dto.TrackSearchRequest
import com.jar89.playlistmaker.search.data.dto.TracksResponse
import com.jar89.playlistmaker.search.data.mappers.TrackListMapper
import com.jar89.playlistmaker.search.data.network.NetworkClient
import com.jar89.playlistmaker.search.data.storage.SearchHistoryStorage
import com.jar89.playlistmaker.search.domain.api.TrackRepository
import com.jar89.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val storage: SearchHistoryStorage,
    private val mapper: TrackListMapper
) : TrackRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error(ErrorType.NO_INTERNET))
            }

            200 -> {
                emit(Resource.Success((response as TracksResponse).tracks.map {
                    mapper.mapDtoToEntity(it)
                }))
            }

            else -> {
                emit(Resource.Error(ErrorType.SERVER_ERROR))
            }
        }
    }


    override fun saveTrack(track: Track) {
        storage.addTrackInHistory(mapper.mapEntityToDto(track))
    }

    override fun getAllTracks(): List<Track> {
        return mapper.mapListDtoToEntity(storage.getSavedHistoryTracks())
    }

    override fun clearHistory() {
        storage.clearHistory()
    }
}