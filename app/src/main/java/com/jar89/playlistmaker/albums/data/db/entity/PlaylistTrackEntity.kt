package com.jar89.playlistmaker.albums.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks_from_playlists")
data class PlaylistTrackEntity (
    @PrimaryKey(autoGenerate = false)
    val trackId: Int,
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Long?,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?,
    val usedInPlaylists: Int = 0
)