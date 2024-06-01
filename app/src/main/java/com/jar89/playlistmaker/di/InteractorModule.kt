package com.jar89.playlistmaker.di

import com.jar89.playlistmaker.albums.domain.api.FavoritesTracksInteractor
import com.jar89.playlistmaker.albums.domain.api.PlaylistInteractor
import com.jar89.playlistmaker.albums.domain.impl.FavoritesTracksInteractorImpl
import com.jar89.playlistmaker.albums.domain.impl.PlaylistInteractorImpl
import com.jar89.playlistmaker.player.domain.api.PlayerInteractor
import com.jar89.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.jar89.playlistmaker.search.domain.api.TrackInteractor
import com.jar89.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.jar89.playlistmaker.settings.domain.api.SettingsInteractor
import com.jar89.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.jar89.playlistmaker.sharing.domain.api.SharingInteractor
import com.jar89.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<TrackInteractor> {
        TrackInteractorImpl(repository = get())
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(player = get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(settingsRepository = get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(sharingRepository = get())
    }

    single<FavoritesTracksInteractor> {
        FavoritesTracksInteractorImpl(repository = get())
    }

    single<PlaylistInteractor> {
        PlaylistInteractorImpl(repository = get())
    }
}