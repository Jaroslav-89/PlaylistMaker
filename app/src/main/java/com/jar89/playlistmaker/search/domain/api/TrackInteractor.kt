package com.jar89.playlistmaker.search.domain.api

import com.jar89.playlistmaker.search.data.dto.ErrorType
import com.jar89.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackInteractor {

    fun searchTracks(expression: String): Flow<Pair<List<Track>?, ErrorType?>>

    fun saveTrack(track: Track)

    fun getAllTracks(): List<Track>

    fun clearHistory()
}