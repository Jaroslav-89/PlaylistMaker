package com.jar89.playlistmaker.albums.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.jar89.playlistmaker.albums.data.converters.PlaylistDbConvertor
import com.jar89.playlistmaker.albums.data.converters.TrackDbConvertor
import com.jar89.playlistmaker.albums.data.db.AppDatabase
import com.jar89.playlistmaker.albums.data.db.entity.PlaylistTrackEntity
import com.jar89.playlistmaker.albums.domain.api.PlaylistRepository
import com.jar89.playlistmaker.albums.domain.model.Playlist
import com.jar89.playlistmaker.search.domain.model.Track
import com.jar89.playlistmaker.sharing.data.navigation.ExternalNavigator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream

class PlaylistRepositoryImpl(
    private val dataBase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val trackDbConvertor: TrackDbConvertor,
    private val externalNavigator: ExternalNavigator,
    private val context: Context
) : PlaylistRepository {

    override suspend fun savePlaylist(playlist: Playlist) {
        val privateStorageUri = saveImageToPrivateStorage(playlist)
        val playlistWithPrivateStorageUri = playlist.copy(coverUri = privateStorageUri)

        dataBase.playlistsDao()
            .savePlaylist(playlistDbConvertor.mapPlaylistToEntity(playlistWithPrivateStorageUri))
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        val playlistEntity = dataBase.playlistsDao().getPlaylistById(playlistId)
        val tracksIdList = playlistDbConvertor.mapStringToListInt(playlistEntity.tracksId)

        dataBase.playlistsDao().deletePlaylist(playlistId)

        for (trackId in tracksIdList) {
            checkAndDeleteTrackFromTracksTable(trackId.toLong())
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        emit(
            playlistDbConvertor.mapEntityListToPlaylists(
                dataBase.playlistsDao().getAllPlaylists()
            )
        )
    }

    override suspend fun getPlaylistById(playlistId: Int): Playlist {
        val playlistEntity = dataBase.playlistsDao().getPlaylistById(playlistId)
        return playlistDbConvertor.mapEntityToPlaylist(playlistEntity)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        val tracksId = playlist.tracksId.toMutableList().apply {
            add(track.trackId.toInt())
        }
        if (track.trackTimeMillis != null) {
            updatePlaylist(
                playlist.copy(
                    tracksId = tracksId,
                    numberOfTracks = tracksId.size,
                    playlistDuration = playlist.playlistDuration + track.trackTimeMillis
                )
            )
        } else {
            updatePlaylist(playlist.copy(tracksId = tracksId, numberOfTracks = tracksId.size))
        }
        addTrack(track)
    }

    override suspend fun deleteTrackFromPlaylist(track: Track, playlist: Playlist) {
        val playlistEntity = dataBase.playlistsDao().getPlaylistById(playlist.id)
        val tracksIdList = playlistDbConvertor.mapStringToListInt(playlistEntity.tracksId)
        val newTracksId = mutableListOf<Int>()

        for (trackId in tracksIdList) {
            if (trackId != track.trackId.toInt()) {
                newTracksId.add(trackId)
            }
        }
        dataBase.playlistsDao()
            .savePlaylist(
                playlistEntity.copy(
                    tracksId = newTracksId.joinToString(","),
                    numberOfTracks = playlistEntity.numberOfTracks - 1,
                    playlistDuration = track.trackTimeMillis.let { playlistEntity.playlistDuration - it!! }
                )
            )
        checkAndDeleteTrackFromTracksTable(track.trackId)
    }

    private suspend fun checkAndDeleteTrackFromTracksTable(trackId: Long) {
        val trackEntity = dataBase.playlistTrackDao().getTrackById(trackId)
        val newTrackEntity = trackEntity.copy(usedInPlaylists = trackEntity.usedInPlaylists - 1)
        if (newTrackEntity.usedInPlaylists == 0) {
            dataBase.playlistTrackDao().deleteTrack(trackEntity)
        } else {
            dataBase.playlistTrackDao().addTrack(newTrackEntity)
        }
    }

    override fun getTracksFromPlaylist(tracksId: List<Int>): Flow<List<Track>> {
        return flow {
            val allTracks = dataBase.playlistTrackDao().getAllTracks()
            emit(
                allTracks
                    .filter {
                        tracksId.contains(it.trackId)
                    }
                    .map {
                        trackDbConvertor.mapPlaylistTrackEntityToTrack(it)
                    }
            )
        }
    }

    override fun sharePlaylist(playlistDescription: String) {
        externalNavigator.sharePlaylist(playlistDescription)
    }

    private suspend fun updatePlaylist(playlist: Playlist) {
        dataBase.playlistsDao()
            .updateTracksInPlaylist(playlistDbConvertor.mapPlaylistToEntity(playlist))
    }

    private suspend fun addTrack(track: Track) {
        var trackDb: PlaylistTrackEntity? = null

        dataBase.let {
            trackDb = dataBase.playlistTrackDao().getTrackById(track.trackId)
        }

        val newTrackEntity = trackDbConvertor.mapTrackToPlaylistTrackEntity(track)
        dataBase.playlistTrackDao()
            .addTrack(
                newTrackEntity.copy(
                    usedInPlaylists = trackDb?.usedInPlaylists?.plus(1) ?: 1
                )
            )
    }

    private fun saveImageToPrivateStorage(playlist: Playlist): Uri {
        return if (playlist.coverUri != null) {
            val filePath =
                File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlists_cover")
            if (!filePath.exists()) {
                filePath.mkdirs()
            }
            val file = File(filePath, "${playlist.name}.jpg")
            val inputStream = context.contentResolver.openInputStream(playlist.coverUri)
            val outputStream = FileOutputStream(file)
            BitmapFactory
                .decodeStream(inputStream)
                .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
            file.toUri()
        } else "".toUri()
    }
}