package com.jar89.playlistmaker.di

import com.jar89.playlistmaker.albums.ui.create_playlist.view_model.CreatePlaylistViewModel
import com.jar89.playlistmaker.albums.ui.detail_playlist.view_model.DetailPlaylistViewModel
import com.jar89.playlistmaker.albums.ui.favorites.view_model.FavoritesViewModel
import com.jar89.playlistmaker.albums.ui.playlist.view_model.PlaylistViewModel
import com.jar89.playlistmaker.player.ui.view_model.PlayerViewModel
import com.jar89.playlistmaker.search.domain.model.Track
import com.jar89.playlistmaker.search.ui.view_model.SearchViewModel
import com.jar89.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SearchViewModel(
            trackInteractor = get()
        )
    }

    viewModel { (track: Track) ->
        PlayerViewModel(
            track = track,
            playerInteractor = get(),
            favoritesTracksInteractor = get(),
            playlistInteractor = get()
        )
    }

    viewModel {
        SettingsViewModel(
            settingsInteractor = get(),
            sharingInteractor = get()
        )
    }

    viewModel { FavoritesViewModel(favoritesTracksInteractor = get()) }

    viewModel { PlaylistViewModel(playlistInteractor = get()) }

    viewModel { CreatePlaylistViewModel(playlistInteractor = get()) }

    viewModel { DetailPlaylistViewModel(playlistInteractor = get()) }
}