package com.jar89.playlistmaker.data

import com.jar89.playlistmaker.data.dto.TrackSearchRequest
import com.jar89.playlistmaker.domain.entity.ApiResponse
import com.jar89.playlistmaker.domain.entity.Track

interface NetworkClient {
    fun doRequest(dto: TrackSearchRequest): ApiResponse<List<Track>>

}