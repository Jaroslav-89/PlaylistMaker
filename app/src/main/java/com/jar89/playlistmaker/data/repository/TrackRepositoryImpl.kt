package com.jar89.playlistmaker.data.repository

import com.jar89.playlistmaker.data.NetworkClient
import com.jar89.playlistmaker.data.dto.TrackSearchRequest
import com.jar89.playlistmaker.data.mappers.TrackListMapper
import com.jar89.playlistmaker.data.storage.shared_prefs.SearchHistoryStorageSharPrefs
import com.jar89.playlistmaker.domain.api.TrackRepository
import com.jar89.playlistmaker.domain.entity.ApiResponse
import com.jar89.playlistmaker.domain.entity.Track

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val storage: SearchHistoryStorageSharPrefs
) : TrackRepository {

    override fun searchTracks(expression: String): ApiResponse<List<Track>> {
        return networkClient.doRequest(TrackSearchRequest(expression))
    }

    override fun saveTrack(track: Track) {
        storage.addTrackInHistory(TrackListMapper.mapEntityToDto(track))
    }

    override fun getAllTracks(): List<Track> {
        return TrackListMapper.mapDtoToEntity(storage.getSavedHistoryTracks())
    }

    override fun clearHistory() {
        storage.clearHistory()
    }
}