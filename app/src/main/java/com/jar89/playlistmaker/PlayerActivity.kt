package com.jar89.playlistmaker

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.jar89.playlistmaker.Utils.longToTime
import com.jar89.playlistmaker.Utils.toPx
import com.jar89.playlistmaker.databinding.ActivytyPlayerBinding
import com.jar89.playlistmaker.model.Track
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val UPDATE_ELAPSED_TIME_DELAY = 300L
    }

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private val playTimeRunnable = Runnable { elapsedTime() }
    private lateinit var binding: ActivytyPlayerBinding
    private lateinit var track: Track
    private lateinit var trackUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivytyPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        track = readTrackFromJson(intent.getStringExtra("track").toString())

        setAlbumImage(this, track)

        initViews()

        preparePlayer()
    }

    private fun initViews() {
        setCollectionName(track)
        trackUrl = track.previewUrl
        binding.trackNameTv.text = track.trackName
        binding.artistNameTv.text = track.artistName
        binding.durationDescriptionTv.text = longToTime(track.trackTimeMillis)
        binding.yearDescriptionTv.text = getYear(track)
        binding.genreDescriptionTv.text = track.primaryGenreName
        binding.countryDescriptionTv.text = track.country
        binding.backBtn.setOnClickListener {
            finish()
        }
        binding.playPauseBtn.setOnClickListener {
            playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(playTimeRunnable)
        mediaPlayer.release()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            binding.playPauseBtn.setImageResource(R.drawable.ic_play_player_screen)
            binding.progressTimeTv.text = getString(R.string.start_time)
            handler.removeCallbacks(playTimeRunnable)
        }
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun elapsedTime() {
        binding.progressTimeTv.text = getCurrentPosition()
        handler.postDelayed(playTimeRunnable, UPDATE_ELAPSED_TIME_DELAY)
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.playPauseBtn.setImageResource(R.drawable.ic_pause_player_screen)
        playerState = STATE_PLAYING
        handler.post(playTimeRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        binding.playPauseBtn.setImageResource(R.drawable.ic_play_player_screen)
        playerState = STATE_PAUSED
        handler.removeCallbacks(playTimeRunnable)
    }

    private fun setAlbumImage(context: Context, track: Track) {
        Glide.with(context)
            .load(getCoverArtwork(track.artworkUrl100))
            .centerCrop()
            .transform(RoundedCorners(8f.toPx(context)))
            .placeholder(R.drawable.img_place_holder_player_screen)
            .into(binding.placeHolderAlbum)
    }

    private fun readTrackFromJson(track: String): Track {
        return Gson().fromJson(track, Track::class.java)
    }

    private fun setCollectionName(track: Track) {
        if (track.collectionName.isNullOrEmpty()) {
            binding.albumDescriptionTv.text = ""
            binding.albumHeadingTv.visibility = View.GONE
            binding.albumDescriptionTv.visibility = View.GONE
        } else {
            binding.albumDescriptionTv.text = track.collectionName
            binding.albumHeadingTv.visibility = View.VISIBLE
            binding.albumDescriptionTv.visibility = View.VISIBLE
        }
    }

    private fun getCoverArtwork(artworkUrl100: String) =
        artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

    private fun getYear(track: Track) =
        track.releaseDate.substringBefore('-')

    private fun getCurrentPosition() =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
}