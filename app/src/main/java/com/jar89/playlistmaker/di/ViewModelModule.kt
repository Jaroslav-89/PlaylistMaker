package com.jar89.playlistmaker.di

import com.jar89.playlistmaker.albums.ui.view_model.FavoritesViewModel
import com.jar89.playlistmaker.albums.ui.view_model.PlaylistViewModel
import com.jar89.playlistmaker.player.ui.view_model.PlayerViewModel
import com.jar89.playlistmaker.search.ui.view_model.SearchViewModel
import com.jar89.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SearchViewModel(
            application = androidApplication(),
            trackInteractor = get()
        )
    }

    viewModel {
        PlayerViewModel(
            playerInteractor = get()
        )
    }

    viewModel {
        SettingsViewModel(
            application = get(),
            settingsInteractor = get(),
            sharingInteractor = get()
        )
    }

    viewModel { FavoritesViewModel() }

    viewModel { PlaylistViewModel() }
}