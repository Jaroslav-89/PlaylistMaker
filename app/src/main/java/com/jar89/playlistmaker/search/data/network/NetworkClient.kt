package com.jar89.playlistmaker.search.data.network

import com.jar89.playlistmaker.search.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response

}