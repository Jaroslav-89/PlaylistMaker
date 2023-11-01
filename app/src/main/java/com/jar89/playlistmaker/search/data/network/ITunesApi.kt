package com.jar89.playlistmaker.search.data.network

import com.jar89.playlistmaker.search.data.dto.TracksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("/search")
    suspend fun searchTrack(@Query("term") text: String): TracksResponse
}