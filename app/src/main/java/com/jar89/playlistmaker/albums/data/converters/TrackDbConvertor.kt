package com.jar89.playlistmaker.albums.data.converters

import com.jar89.playlistmaker.albums.data.db.entity.PlaylistTrackEntity
import com.jar89.playlistmaker.albums.data.db.entity.TrackEntity
import com.jar89.playlistmaker.search.domain.model.Track

class TrackDbConvertor {
    fun mapTrackToEntity(track: Track): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            addTime = System.currentTimeMillis()
        )
    }

    fun mapEntityToTrack(entity: TrackEntity): Track {
        return Track(
            entity.trackId,
            entity.trackName,
            entity.artistName,
            entity.trackTimeMillis,
            entity.artworkUrl100,
            entity.collectionName,
            entity.releaseDate,
            entity.primaryGenreName,
            entity.country,
            entity.previewUrl
        )
    }

    fun mapTrackToPlaylistTrackEntity(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            track.trackId.toInt(),
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
        )
    }
}