package com.jar89.playlistmaker.data.mappers

import com.jar89.playlistmaker.domain.entity.ApiResponse
import retrofit2.Response

fun <T, V> Response<T>.mapToApiResponse(mapSuccess: (dto: T) -> V):ApiResponse<V> {
    val dto = body()
    val errorMessage = "Error loading"
    return when {
        !isSuccessful -> {
            ApiResponse.Error(errorBody()?.string() ?: errorMessage)
        }
        dto != null -> ApiResponse.Success(mapSuccess(dto))
        else -> {
            ApiResponse.Error(errorMessage)
        }
    }
}