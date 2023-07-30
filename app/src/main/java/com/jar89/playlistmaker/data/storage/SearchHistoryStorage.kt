package com.jar89.playlistmaker.data.storage

import com.jar89.playlistmaker.data.dto.TrackDto

interface SearchHistoryStorage {

    fun getSavedHistoryTracks(): List<TrackDto>

    fun addTrackInHistory(newTrack: TrackDto)

    fun clearHistory()
}