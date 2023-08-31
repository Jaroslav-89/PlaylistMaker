package com.jar89.playlistmaker.creator

import android.content.Context
import com.jar89.playlistmaker.search.data.network.RetrofitNetworkClient
import com.jar89.playlistmaker.player.data.PlayerImpl
import com.jar89.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.jar89.playlistmaker.search.data.storage.shared_prefs.SearchHistoryStorageSharPrefs
import com.jar89.playlistmaker.player.domain.api.PlayerInteractor
import com.jar89.playlistmaker.player.domain.api.Player
import com.jar89.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.jar89.playlistmaker.search.data.network.NetworkClient
import com.jar89.playlistmaker.search.data.storage.SearchHistoryStorage
import com.jar89.playlistmaker.search.domain.api.TrackInteractor
import com.jar89.playlistmaker.search.domain.api.TrackRepository
import com.jar89.playlistmaker.search.domain.impl.TrackInteractorImpl

object Creator {

    fun providePlayerInteractor(trackUrl: String?): PlayerInteractor {
        return PlayerInteractorImpl(getPlayer(trackUrl))
    }

    fun provideSearchAndHistoryInteractor(context: Context): TrackInteractor {
        return TrackInteractorImpl(getHistoryRepository(context))
    }

    private fun getPlayer(trackUrl: String?): Player {
        return PlayerImpl(trackUrl)
    }

    private fun getHistoryRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(
            getRetrofitNetworkClient(context),
            getSearchHistoryStorage(context)
        )
    }

    private fun getRetrofitNetworkClient(context: Context): NetworkClient {
        return RetrofitNetworkClient(context)
    }

    private fun getSearchHistoryStorage(context: Context): SearchHistoryStorage {
        return SearchHistoryStorageSharPrefs(context)
    }

}