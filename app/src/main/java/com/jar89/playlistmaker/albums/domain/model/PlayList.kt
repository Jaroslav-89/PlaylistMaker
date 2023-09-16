package com.jar89.playlistmaker.albums.domain.model

import com.jar89.playlistmaker.search.domain.model.Track

data class PlayList(
    val name: String,
    val tracks: List<Track>
)