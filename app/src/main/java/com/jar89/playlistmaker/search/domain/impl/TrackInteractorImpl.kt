package com.jar89.playlistmaker.search.domain.impl

import com.jar89.playlistmaker.search.data.dto.ErrorType
import com.jar89.playlistmaker.search.data.dto.Resource
import com.jar89.playlistmaker.search.domain.api.TrackInteractor
import com.jar89.playlistmaker.search.domain.api.TrackRepository
import com.jar89.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, ErrorType?>> {
        return repository.searchTracks(expression).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.errorType)
                }
            }
        }
    }

    override fun saveTrack(track: Track) {
        repository.saveTrack(track)
    }

    override fun getAllTracks(): List<Track> {
        return repository.getAllTracks()
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}