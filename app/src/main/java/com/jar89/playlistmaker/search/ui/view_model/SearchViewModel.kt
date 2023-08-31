package com.jar89.playlistmaker.search.ui.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jar89.playlistmaker.R
import com.jar89.playlistmaker.creator.Creator
import com.jar89.playlistmaker.search.domain.api.TrackInteractor
import com.jar89.playlistmaker.search.domain.model.ErrorType
import com.jar89.playlistmaker.search.domain.model.SearchActivityState
import com.jar89.playlistmaker.search.domain.model.Track

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_IN_MILLIS = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
        private const val CLICK_DEBOUNCE_DELAY_IN_MILLIS = 1000L

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
            }
        }
    }

    private val trackInteractor = Creator.provideSearchAndHistoryInteractor(application)
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
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

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed(
                { isClickAllowed = true },
                CLICK_DEBOUNCE_DELAY_IN_MILLIS
            )
        }
        return current
    }

    private fun getHistoryTrack() = trackInteractor.getAllTracks()

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchActivityState.Loading)

            trackInteractor.searchTracks(newSearchText, object : TrackInteractor.TracksConsumer {
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