package com.jar89.playlistmaker.albums.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jar89.playlistmaker.albums.data.db.entity.TrackEntity

@Dao
interface TrackDao {
    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertTrack(track: TrackEntity)

    @Delete(entity = TrackEntity::class)
    fun deleteTrack(track: TrackEntity)

    @Query("SELECT * FROM favorite_tracks_table WHERE trackId = :trackId limit 1")
    fun checkTrackById(trackId: Long): TrackEntity?

    @Query("SELECT * FROM favorite_tracks_table ORDER BY addTime DESC")
    fun getFavoritesTracks(): List<TrackEntity>

}