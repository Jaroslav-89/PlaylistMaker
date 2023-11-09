package com.jar89.playlistmaker.di

import com.jar89.playlistmaker.albums.data.converters.TrackDbConvertor
import com.jar89.playlistmaker.albums.data.repository.FavoritesTracksRepositoryImpl
import com.jar89.playlistmaker.albums.domain.api.FavoritesTracksRepository
import com.jar89.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.jar89.playlistmaker.search.domain.api.TrackRepository
import com.jar89.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.jar89.playlistmaker.settings.domain.api.SettingsRepository
import com.jar89.playlistmaker.sharing.data.repository.SharingRepositoryImpl
import com.jar89.playlistmaker.sharing.domain.api.SharingRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<TrackRepository> {
        TrackRepositoryImpl(networkClient = get(), storage = get(), mapper = get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(context = get())
    }

    single<SharingRepository> {
        SharingRepositoryImpl(externalNavigator = get(), context = get())
    }

    factory { TrackDbConvertor() }

    single<FavoritesTracksRepository> {
        FavoritesTracksRepositoryImpl(dataBase = get(), trackDbConvertor = get())
    }
}