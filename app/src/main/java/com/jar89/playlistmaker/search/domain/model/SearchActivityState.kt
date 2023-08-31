package com.jar89.playlistmaker.search.domain.model

sealed interface SearchActivityState {
    object Loading : SearchActivityState

    data class Content(
        val tracks: List<Track>
    ) : SearchActivityState

    data class Error(val errorMessage: String) : SearchActivityState

    data class Empty(val message: String) : SearchActivityState

    data class SearchHistory(
        val tracks: List<Track>
    ) : SearchActivityState
}