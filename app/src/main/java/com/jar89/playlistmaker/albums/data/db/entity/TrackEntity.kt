package com.jar89.playlistmaker.albums.data.db.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks_table")
data class TrackEntity(
    @PrimaryKey(autoGenerate = false)
    @NonNull
    val trackId: Long,
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Long?,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?,
    @NonNull
    val addTime: Long
)
