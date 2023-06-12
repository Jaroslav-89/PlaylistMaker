package com.jar89.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.jar89.playlistmaker.model.Track

const val SEARCH_HISTORY_TRACKS_KEY = "search_history_tracks_key"

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private var tracksHistory = mutableListOf<Track>()

    fun getSavedHistoryTracks(): List<Track> {
        tracksHistory = readHistoryFromJson()
        return tracksHistory
    }

    fun addTrackInHistory(newTrack: Track) {
        tracksHistory = readHistoryFromJson()
        if (tracksHistory.contains(newTrack)) tracksHistory.remove(newTrack)
        tracksHistory.add(0, newTrack)
        if (tracksHistory.size == 11) tracksHistory.removeAt(10)
        writeHistoryToJson(tracksHistory)
    }

    fun clearHistory() {
        tracksHistory.clear()
        writeHistoryToJson(tracksHistory)
    }

    private fun readHistoryFromJson(): MutableList<Track> {
        val json =
            sharedPreferences.getString(SEARCH_HISTORY_TRACKS_KEY, null) ?: return mutableListOf()
        return Gson().fromJson(json, Array<Track>::class.java).toMutableList()
    }

    private fun writeHistoryToJson(searchTracksHistory: List<Track>) {
        val json = Gson().toJson(searchTracksHistory)
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_TRACKS_KEY, json)
            .apply()
    }
}