package com.jar89.playlistmaker.data.repository

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.jar89.playlistmaker.domain.api.MediaPlayerRepository
import com.jar89.playlistmaker.domain.entity.MediaPlayerState

class MediaPlayerRepositoryImpl() : MediaPlayerRepository {

    companion object {
        private const val DELAY_MILLIS = 100L
    }

    private var playerState = MediaPlayerState.STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private val playTimeRunnable = Runnable { refreshElapsedTime() }
    private var elapsedTime = 0

    override fun play() {
        mediaPlayer.start()
        playerState = MediaPlayerState.STATE_PLAYING
        handler.post(playTimeRunnable)
    }

    override fun pause() {
        mediaPlayer.pause()
        playerState = MediaPlayerState.STATE_PAUSED
        handler.removeCallbacks(playTimeRunnable)
    }

    override fun destroy() {
        handler.removeCallbacks(playTimeRunnable)
        mediaPlayer.release()
    }

    override fun preparePlayer(url: String?, completion: () -> Unit) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = MediaPlayerState.STATE_PREPARED
            completion()
        }
        mediaPlayer.setOnCompletionListener {
            playerState = MediaPlayerState.STATE_PREPARED
            handler.removeCallbacks(playTimeRunnable)
        }
    }

    override fun elapsedTime(): Int {
        return elapsedTime
    }

    override fun playerState(): MediaPlayerState {
        return playerState
    }

    private fun refreshElapsedTime() {
        elapsedTime = mediaPlayer.currentPosition
        handler.postDelayed(playTimeRunnable, DELAY_MILLIS)
    }
}