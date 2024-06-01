package com.jar89.playlistmaker.search.domain.api

import com.jar89.playlistmaker.search.data.dto.Resource
import com.jar89.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {

    fun searchTracks(expression: String): Flow<Resource<List<Track>>>

    fun saveTrack(track: Track)

    fun getAllTracks(): List<Track>

    fun clearHistory()
}