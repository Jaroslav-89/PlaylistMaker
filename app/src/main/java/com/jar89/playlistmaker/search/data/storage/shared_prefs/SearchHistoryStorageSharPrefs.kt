package com.jar89.playlistmaker.search.data.storage.shared_prefs

import android.content.SharedPreferences
import com.google.gson.Gson
import com.jar89.playlistmaker.search.data.dto.TrackDto
import com.jar89.playlistmaker.search.data.storage.SearchHistoryStorage


class SearchHistoryStorageSharPrefs(private val sharedPrefs: SharedPreferences, private val gson: Gson) :
    SearchHistoryStorage {

    companion object {
        const val SEARCH_HISTORY_PREFERENCES = "search_history_tracks"
        const val HISTORY_TRACKS_KEY = "history_tracks_key"
        const val MAX_NUMBER_OF_TRACKS = 10
    }

    private var tracksHistory = mutableListOf<TrackDto>()

    override fun getSavedHistoryTracks(): List<TrackDto> {
        tracksHistory = readHistoryFromJson()
        return tracksHistory
    }

    override fun addTrackInHistory(newTrack: TrackDto) {
        tracksHistory = readHistoryFromJson()
        if (tracksHistory.contains(newTrack)) tracksHistory.remove(newTrack)
        if (tracksHistory.size == MAX_NUMBER_OF_TRACKS) {
            tracksHistory.removeLast()
        }
        tracksHistory.add(0, newTrack)
        writeHistoryToJson(tracksHistory)
    }

    override fun clearHistory() {
        tracksHistory.clear()
        writeHistoryToJson(tracksHistory)
    }

    private fun readHistoryFromJson(): MutableList<TrackDto> {
        val json =
            sharedPrefs.getString(HISTORY_TRACKS_KEY, null) ?: return mutableListOf()
        return gson.fromJson(json, Array<TrackDto>::class.java).toMutableList()
    }

    private fun writeHistoryToJson(searchTracksHistory: List<TrackDto>) {
        val json = gson.toJson(searchTracksHistory)
        sharedPrefs.edit()
            .putString(HISTORY_TRACKS_KEY, json)
            .apply()
    }
}