package com.jar89.playlistmaker.search.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jar89.playlistmaker.R
import com.jar89.playlistmaker.search.data.dto.ErrorType
import com.jar89.playlistmaker.search.domain.api.TrackInteractor
import com.jar89.playlistmaker.search.domain.model.Track
import com.jar89.playlistmaker.util.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    application: Application,
    private val trackInteractor: TrackInteractor
) : AndroidViewModel(application) {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_IN_MILLIS = 2000L
    }

    private var latestSearchText: String? = null

    private val searchTrackDebounce = debounce<String>(
        SEARCH_DEBOUNCE_DELAY_IN_MILLIS,
        viewModelScope,
        true
    ) { changedText ->
        searchRequest(changedText)
    }

    private val _state = MutableLiveData<SearchActivityState>()
    val state: LiveData<SearchActivityState>
        get() = _state

    fun searchDebounce(changedText: String) {
        if (latestSearchText != changedText) {
            this.latestSearchText = changedText
            searchTrackDebounce(latestSearchText!!)
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

            viewModelScope.launch {
                trackInteractor
                    .searchTracks(searchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: ErrorType?) {
        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }

        when {
            errorMessage != null -> {
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

    private fun renderState(state: SearchActivityState) {
        _state.postValue(state)
    }
}