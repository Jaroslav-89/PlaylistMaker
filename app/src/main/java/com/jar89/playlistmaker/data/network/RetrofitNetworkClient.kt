package com.jar89.playlistmaker.data.network

import com.jar89.playlistmaker.data.NetworkClient
import com.jar89.playlistmaker.data.dto.TrackSearchRequest
import com.jar89.playlistmaker.data.mappers.TrackListMapper
import com.jar89.playlistmaker.data.mappers.mapToApiResponse
import com.jar89.playlistmaker.domain.entity.ApiResponse
import com.jar89.playlistmaker.domain.entity.Track
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ITunesApi::class.java)

    override fun doRequest(dto: TrackSearchRequest): ApiResponse<List<Track>> {
            val resp = itunesService.searchTrack(dto.expression).execute()
            return resp.mapToApiResponse { dto -> TrackListMapper.mapDtoToEntity(dto.results) }
    }
}
