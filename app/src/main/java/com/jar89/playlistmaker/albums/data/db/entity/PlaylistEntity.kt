package com.jar89.playlistmaker.albums.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val description: String,
    val coverUri: String,
    val tracksId: String,
    val numberOfTracks: Int,
    val playlistDuration: Long,
    val createTime: Long
)