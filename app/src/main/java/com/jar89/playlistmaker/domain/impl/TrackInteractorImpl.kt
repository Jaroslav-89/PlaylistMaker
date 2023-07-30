package com.jar89.playlistmaker.domain.impl

import com.jar89.playlistmaker.domain.Consumer
import com.jar89.playlistmaker.domain.api.TrackInteractor
import com.jar89.playlistmaker.domain.api.TrackRepository
import com.jar89.playlistmaker.domain.entity.ApiResponse
import com.jar89.playlistmaker.domain.entity.ConsumerData
import com.jar89.playlistmaker.domain.entity.Track
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TrackRepository): TrackInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: Consumer<List<Track>>) {
        executor.execute {
            val trackList = repository.searchTracks(expression)
            when(trackList){
                is ApiResponse.Error -> consumer.consume(ConsumerData.Error(trackList.message))
                is ApiResponse.Success -> consumer.consume(ConsumerData.Data(trackList.data))
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