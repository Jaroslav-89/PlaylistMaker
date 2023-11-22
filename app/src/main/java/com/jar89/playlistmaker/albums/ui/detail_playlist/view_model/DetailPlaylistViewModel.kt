package com.jar89.playlistmaker.albums.ui.detail_playlist.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar89.playlistmaker.R
import com.jar89.playlistmaker.albums.domain.api.PlaylistInteractor
import com.jar89.playlistmaker.albums.domain.model.Playlist
import com.jar89.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DetailPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private lateinit var playlist: Playlist
    private var tracks = emptyList<Track>()

    private val _state = MutableLiveData<DetailPlaylistState>()
    val state: LiveData<DetailPlaylistState>
        get() = _state


    fun getPlaylistById(playlistId: Int) {
        viewModelScope.launch {
            playlist = playlistInteractor.getPlaylistById(playlistId)
            tracks = playlistInteractor.getTracksFromPlaylist(playlist.tracksId).first()
            _state.postValue(DetailPlaylistState.Content(playlist = playlist, trackList = tracks))
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlist.id)
            _state.postValue(DetailPlaylistState.PlaylistDeleted)
        }
    }

    fun deleteTrack(track: Track) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(track, playlist)
            playlist = playlistInteractor.getPlaylistById(playlist.id)
            tracks = playlistInteractor.getTracksFromPlaylist(playlist.tracksId).first()
            _state.postValue(DetailPlaylistState.Content(playlist = playlist, trackList = tracks))
        }
    }

    fun sharePlaylist(context: Context) {
        if (tracks.isEmpty()) {
            _state.value =
                DetailPlaylistState.Message(text = context.getString(R.string.empty_playlist_nothing_to_share))
            return
        }
        var message = "${playlist.name}\n"
        message += "${playlist.description}\n"
        message += "${
            context.applicationContext.resources.getQuantityString(
                R.plurals.tracks_number,
                playlist.numberOfTracks,
                playlist.numberOfTracks
            )
        }\n"

        for ((index, track) in tracks.withIndex()) {
            message += "${index + 1}. ${track.artistName} - ${track.trackName} (${
                (track.trackTimeMillis)?.div(
                    60000
                )?.toDouble().toString() + "min"
            })\n"
        }

        playlistInteractor.sharePlaylist(message)
    }
}