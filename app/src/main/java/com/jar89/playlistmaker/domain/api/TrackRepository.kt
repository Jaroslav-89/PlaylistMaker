package com.jar89.playlistmaker.domain.api

import com.jar89.playlistmaker.domain.entity.ApiResponse
import com.jar89.playlistmaker.domain.entity.Track

interface TrackRepository {

    fun searchTracks(expression: String): ApiResponse<List<Track>>

    fun saveTrack(track: Track)

    fun getAllTracks(): List<Track>

    fun clearHistory()
}