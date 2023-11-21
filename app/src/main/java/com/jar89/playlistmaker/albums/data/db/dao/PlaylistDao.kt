package com.jar89.playlistmaker.albums.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.jar89.playlistmaker.albums.data.db.entity.PlaylistEntity

@Dao
interface PlaylistDao {
    @Insert
    suspend fun savePlaylist(playlistEntity: PlaylistEntity)

    @Query("SELECT * FROM playlist_table ORDER BY createTime DESC")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    @Update
    suspend fun updateTracksInPlaylist(playlistEntity: PlaylistEntity)
}