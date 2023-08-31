package com.jar89.playlistmaker.search.domain.api

import com.jar89.playlistmaker.search.domain.model.ErrorType
import com.jar89.playlistmaker.search.domain.model.Track

interface TrackInteractor {

    fun searchTracks(expression: String, consumer: TracksConsumer)

    fun saveTrack(track: Track)

    fun getAllTracks(): List<Track>

    fun clearHistory()

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorType: ErrorType?)
    }
}