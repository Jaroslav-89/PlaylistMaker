package com.jar89.playlistmaker.search.data.dto

import com.google.gson.annotations.SerializedName

data class TracksResponse(@SerializedName("results") val tracks: List<TrackDto>): Response()