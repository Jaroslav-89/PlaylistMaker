package com.jar89.playlistmaker.albums.data.converters

import android.net.Uri
import com.jar89.playlistmaker.albums.data.db.entity.PlaylistEntity
import com.jar89.playlistmaker.albums.domain.model.Playlist

class PlaylistDbConvertor {
    fun mapPlaylistToEntity(playlist: Playlist): PlaylistEntity {
        return with(playlist) {
            PlaylistEntity(
                id = id,
                name = name,
                description = description ?: "",
                coverUri = coverUri.toString(),
                tracksId = tracksId.joinToString(","),
                numberOfTracks = numberOfTracks,
                playlistDuration = playlistDuration,
                createTime = System.currentTimeMillis()
            )
        }
    }

    fun mapEntityListToPlaylists(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map {
            mapEntityToPlaylist(it)
        }
    }

    private fun mapEntityToPlaylist(playlistEntity: PlaylistEntity): Playlist {
        return with(playlistEntity) {
            Playlist(
                id = id,
                name = name,
                description = description,
                coverUri = Uri.parse(coverUri),
                tracksId = mapStringToListInt(tracksId),
                playlistDuration = playlistDuration,
                numberOfTracks = numberOfTracks
            )
        }
    }

    private fun mapStringToListInt(tracksId: String): List<Int> {
        return if (tracksId.isEmpty()) {
            emptyList()
        } else {
            tracksId.split(",").map { it.toInt() }
        }
    }
}