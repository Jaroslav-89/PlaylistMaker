package com.jar89.playlistmaker.search.domain.impl

import com.jar89.playlistmaker.search.domain.api.TrackInteractor
import com.jar89.playlistmaker.search.domain.api.TrackRepository
import com.jar89.playlistmaker.search.domain.model.Resource
import com.jar89.playlistmaker.search.domain.model.Track
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TrackRepository): TrackInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TrackInteractor.TracksConsumer) {
        executor.execute {

            when(val trackList = repository.searchTracks(expression)){
                is Resource.Success -> {
                    consumer.consume(trackList.data, null)
                }

                is Resource.Error -> {
                    consumer.consume(null, trackList.errorType)
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