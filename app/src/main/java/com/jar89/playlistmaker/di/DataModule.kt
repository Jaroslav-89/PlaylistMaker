package com.jar89.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.google.gson.Gson
import com.jar89.playlistmaker.player.data.PlayerImpl
import com.jar89.playlistmaker.player.domain.api.Player
import com.jar89.playlistmaker.search.data.mappers.TrackListMapper
import com.jar89.playlistmaker.search.data.network.ITunesApi
import com.jar89.playlistmaker.search.data.network.NetworkClient
import com.jar89.playlistmaker.search.data.network.RetrofitNetworkClient
import com.jar89.playlistmaker.search.data.storage.SearchHistoryStorage
import com.jar89.playlistmaker.search.data.storage.shared_prefs.SearchHistoryStorageSharPrefs
import com.jar89.playlistmaker.sharing.data.navigation.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    //Search
    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl(RetrofitNetworkClient.ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    factory {
        Gson()
    }

    factory {
        TrackListMapper()
    }

    single<NetworkClient> {
        RetrofitNetworkClient(
            context = androidContext(), itunesService = get()
        )
    }

    single {
        androidContext().getSharedPreferences(
            SearchHistoryStorageSharPrefs.SEARCH_HISTORY_PREFERENCES,
            Context.MODE_PRIVATE
        )
    }

    single<SearchHistoryStorage> {
        SearchHistoryStorageSharPrefs(
            sharedPrefs = get(),
            gson = get()
        )
    }

    //Player
    factory {
        MediaPlayer()
    }

    factory<Player> {
        PlayerImpl(mediaPlayer = get())
    }

    //Settings
    single {
        ExternalNavigator(context = androidContext())
    }
}