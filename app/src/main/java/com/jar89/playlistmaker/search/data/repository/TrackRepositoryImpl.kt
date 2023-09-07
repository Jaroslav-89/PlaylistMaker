package com.jar89.playlistmaker.search.data.repository

import com.jar89.playlistmaker.search.data.network.NetworkClient
import com.jar89.playlistmaker.search.data.dto.TrackSearchRequest
import com.jar89.playlistmaker.search.data.dto.TracksResponse
import com.jar89.playlistmaker.search.data.mappers.TrackListMapper
import com.jar89.playlistmaker.search.data.storage.SearchHistoryStorage
import com.jar89.playlistmaker.search.domain.api.TrackRepository
import com.jar89.playlistmaker.search.data.dto.ErrorType
import com.jar89.playlistmaker.search.data.dto.Resource
import com.jar89.playlistmaker.search.domain.model.Track

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val storage: SearchHistoryStorage,
    private val mapper: TrackListMapper
) : TrackRepository {

    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error(ErrorType.NO_INTERNET)
            }

            200 -> {
                Resource.Success((response as TracksResponse).tracks.map {
                    mapper.mapDtoToEntity(it)
                })
            }

            else -> {
                Resource.Error(ErrorType.SERVER_ERROR)
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