package com.jar89.playlistmaker.search.domain.api

import com.jar89.playlistmaker.search.data.dto.Resource
import com.jar89.playlistmaker.search.domain.model.Track

interface TrackRepository {

    fun searchTracks(expression: String): Resource<List<Track>>

    fun saveTrack(track: Track)

    fun getAllTracks(): List<Track>

    fun clearHistory()
}