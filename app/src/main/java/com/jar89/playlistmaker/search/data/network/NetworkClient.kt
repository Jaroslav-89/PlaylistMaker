package com.jar89.playlistmaker.search.data.network

import com.jar89.playlistmaker.search.data.dto.Response

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response

}