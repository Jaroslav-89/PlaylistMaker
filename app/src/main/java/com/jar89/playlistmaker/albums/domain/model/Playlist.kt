package com.jar89.playlistmaker.albums.domain.model

import android.net.Uri

data class Playlist(
    val id: Int,
    val name: String,
    val description: String?,
    val coverUri: Uri?,
    val tracksId: List<Int>,
    val numberOfTracks: Int = tracksId.size,
    val playlistDuration: Long
)