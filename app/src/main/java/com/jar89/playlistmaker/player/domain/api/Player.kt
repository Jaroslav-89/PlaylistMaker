package com.jar89.playlistmaker.player.domain.api

import androidx.lifecycle.LiveData
import com.jar89.playlistmaker.player.domain.model.PlayerState

interface Player {

    fun play()

    fun pause()

    fun release()

    fun elapsedTime(): Int

    fun playerState(): LiveData<PlayerState>
}