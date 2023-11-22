package com.jar89.playlistmaker.albums.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jar89.playlistmaker.albums.data.db.dao.PlaylistDao
import com.jar89.playlistmaker.albums.data.db.dao.PlaylistTrackDao
import com.jar89.playlistmaker.albums.data.db.dao.TrackDao
import com.jar89.playlistmaker.albums.data.db.entity.PlaylistEntity
import com.jar89.playlistmaker.albums.data.db.entity.PlaylistTrackEntity
import com.jar89.playlistmaker.albums.data.db.entity.TrackEntity

@Database(version = 4, entities = [TrackEntity::class, PlaylistEntity::class, PlaylistTrackEntity::class], exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoritesTracksDao(): TrackDao

    abstract fun playlistsDao(): PlaylistDao

    abstract fun playlistTrackDao(): PlaylistTrackDao
}