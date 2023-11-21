package com.jar89.playlistmaker.albums.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jar89.playlistmaker.albums.data.db.entity.PlaylistTrackEntity

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM tracks_from_playlists WHERE trackId = :trackId limit 1")
    suspend fun getTrackById(trackId: Long): PlaylistTrackEntity
}