package com.jar89.playlistmaker.data.storage.shared_prefs

import android.content.SharedPreferences
import com.google.gson.Gson
import com.jar89.playlistmaker.data.dto.TrackDto
import com.jar89.playlistmaker.data.storage.SearchHistoryStorage

const val SEARCH_HISTORY_TRACKS = "search_history_tracks"

class SearchHistoryStorageSharPrefs(private val sharedPreferences: SharedPreferences) :
    SearchHistoryStorage {

    private var tracksHistory = mutableListOf<TrackDto>()

    override fun getSavedHistoryTracks(): List<TrackDto> {
        tracksHistory = readHistoryFromJson()
        return tracksHistory
    }

    override fun addTrackInHistory(newTrack: TrackDto) {
        tracksHistory = readHistoryFromJson()
        if (tracksHistory.contains(newTrack)) tracksHistory.remove(newTrack)
        tracksHistory.add(0, newTrack)
        if (tracksHistory.size == 11) tracksHistory.removeAt(10)
        writeHistoryToJson(tracksHistory)
    }

    override fun clearHistory() {
        tracksHistory.clear()
        writeHistoryToJson(tracksHistory)
    }

    private fun readHistoryFromJson(): MutableList<TrackDto> {
        val json =
            sharedPreferences.getString(SEARCH_HISTORY_TRACKS, null) ?: return mutableListOf()
        return Gson().fromJson(json, Array<TrackDto>::class.java).toMutableList()
    }

    private fun writeHistoryToJson(searchTracksHistory: List<TrackDto>) {
        val json = Gson().toJson(searchTracksHistory)
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_TRACKS, json)
            .apply()
    }
}