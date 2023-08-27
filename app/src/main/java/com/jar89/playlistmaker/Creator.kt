package com.jar89.playlistmaker

import android.content.SharedPreferences
import com.jar89.playlistmaker.data.network.RetrofitNetworkClient
import com.jar89.playlistmaker.data.repository.MediaPlayerRepositoryImpl
import com.jar89.playlistmaker.data.repository.TrackRepositoryImpl
import com.jar89.playlistmaker.data.storage.shared_prefs.SearchHistoryStorageSharPrefs
import com.jar89.playlistmaker.domain.api.MediaPlayerInteractor
import com.jar89.playlistmaker.domain.api.MediaPlayerRepository
import com.jar89.playlistmaker.domain.impl.MediaPlayerInteractorImpl
import com.jar89.playlistmaker.domain.impl.TrackInteractorImpl

object Creator {

    fun provideMediaPlayerInteractor(): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl(getMediaPlayerRepository())
    }

    fun provideSearchAndHistoryInteractor(sharedPrefs: SharedPreferences) =
        TrackInteractorImpl(getHistoryRepository(sharedPrefs))

    private fun getMediaPlayerRepository(): MediaPlayerRepository {
        return MediaPlayerRepositoryImpl()
    }

    private fun getSearchHistoryStorage(sharedPrefs: SharedPreferences) =
        SearchHistoryStorageSharPrefs(sharedPrefs)

    private fun getHistoryRepository(sharedPrefs: SharedPreferences) =
        TrackRepositoryImpl(getRetrofitNetworkClient(), getSearchHistoryStorage(sharedPrefs))

    private fun getRetrofitNetworkClient() = RetrofitNetworkClient()

}