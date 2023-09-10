package com.jar89.playlistmaker.search.ui.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jar89.playlistmaker.R
import com.jar89.playlistmaker.search.domain.api.TrackInteractor
import com.jar89.playlistmaker.search.data.dto.ErrorType
import com.jar89.playlistmaker.search.domain.model.Track

class SearchViewModel(
    application: Application,
    private val trackInteractor: TrackInteractor
) : AndroidViewModel(application) {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_IN_MILLIS = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    private val handler = Handler(Looper.getMainLooper())
    private var latestSearchText: String? = null

    private val _state = MutableLiveData<SearchActivityState>()
    val state: LiveData<SearchActivityState>
        get() = _state

    fun searchDebounce(changedText: String) {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        if (changedText.isBlank()) {
            _state.value = SearchActivityState.SearchHistory(getHistoryTrack())
        } else {
            this.latestSearchText = changedText

            val searchRunnable = Runnable { searchRequest(changedText) }

            val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY_IN_MILLIS
            handler.postAtTime(
                searchRunnable,
                SEARCH_REQUEST_TOKEN,
                postTime,
            )
        }
    }

    fun showHistory() {
        _state.value = SearchActivityState.SearchHistory(
            getHistoryTrack()
        )
    }

    fun saveTrack(track: Track) {
        trackInteractor.saveTrack(track)
    }

    fun clearHistory() {
        trackInteractor.clearHistory()
        _state.value = SearchActivityState.SearchHistory(
            emptyList()
        )
    }

    private fun getHistoryTrack() = trackInteractor.getAllTracks()

    private fun searchRequest(searchText: String) {
        if (searchText.isNotEmpty()) {
            renderState(SearchActivityState.Loading)

            trackInteractor.searchTracks(searchText, object : TrackInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorType: ErrorType?) {
                    val tracks = mutableListOf<Track>()
                    if (foundTracks != null) {
                        tracks.addAll(foundTracks)
                    }

                    when {
                        errorType != null -> {
                            renderState(
                                SearchActivityState.Error(
                                    errorMessage = getApplication<Application>().getString(R.string.place_holder_text_internet_throwable),
                                )
                            )
                        }

                        tracks.isEmpty() -> {
                            renderState(
                                SearchActivityState.Empty(
                                    message = getApplication<Application>().getString(R.string.place_holder_text_search_fail),
                                )
                            )
                        }

                        else -> {
                            renderState(
                                SearchActivityState.Content(
                                    tracks = tracks,
                                )
                            )
                        }
                    }

                }
            })
        }
    }

    private fun renderState(state: SearchActivityState) {
        _state.postValue(state)
    }
}