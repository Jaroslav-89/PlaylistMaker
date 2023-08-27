package com.jar89.playlistmaker.domain.api

import com.jar89.playlistmaker.domain.Consumer
import com.jar89.playlistmaker.domain.entity.Track

interface TrackInteractor {

    fun searchTracks(expression: String, consumer: Consumer<List<Track>>)

    fun saveTrack(track: Track)

    fun getAllTracks(): List<Track>

    fun clearHistory()
}