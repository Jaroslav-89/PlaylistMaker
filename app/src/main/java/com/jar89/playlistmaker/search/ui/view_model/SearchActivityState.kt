package com.jar89.playlistmaker.search.ui.view_model

import com.jar89.playlistmaker.search.domain.model.Track

sealed interface SearchActivityState {
    data object Loading : SearchActivityState

    data class Content(
        val tracks: List<Track>
    ) : SearchActivityState

    data object Error : SearchActivityState

    data object Empty : SearchActivityState

    data class SearchHistory(
        val tracks: List<Track>
    ) : SearchActivityState
}