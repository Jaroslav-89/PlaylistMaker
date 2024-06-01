package com.jar89.playlistmaker.search.data.storage

import com.jar89.playlistmaker.search.data.dto.TrackDto

interface SearchHistoryStorage {

    fun getSavedHistoryTracks(): List<TrackDto>

    fun addTrackInHistory(newTrack: TrackDto)

    fun clearHistory()
}