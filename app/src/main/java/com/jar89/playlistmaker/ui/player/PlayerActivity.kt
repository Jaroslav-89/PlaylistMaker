package com.jar89.playlistmaker.ui.player

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.jar89.playlistmaker.Creator
import com.jar89.playlistmaker.R
import com.jar89.playlistmaker.Utils.longToTime
import com.jar89.playlistmaker.Utils.toPx
import com.jar89.playlistmaker.databinding.ActivytyPlayerBinding
import com.jar89.playlistmaker.domain.entity.MediaPlayerState
import com.jar89.playlistmaker.domain.entity.Track
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val UPDATE_DELAY = 100L
    }

    private var playerState = MediaPlayerState.STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())

    private val updateButton = Runnable { updateButton() }
    private val updateTimer = Runnable { updateTimer() }
    private val playerInteractor = Creator.provideMediaPlayerInteractor()
    private lateinit var binding: ActivytyPlayerBinding
    private lateinit var track: Track
    private var trackUrl: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivytyPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        track = readTrackFromJson(intent.getStringExtra("track").toString())

        setAlbumImage(this, track)

        initViews()

        preparePlayer()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateButton, updateTimer)
        playerInteractor.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateButton, updateTimer)
        playerInteractor.destroy()
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
        binding.playPauseBtn.isEnabled = false
        binding.playPauseBtn.setOnClickListener {
            handler.post(updateButton)
            playbackControl()
        }
    }

    private fun preparePlayer() {
        handler.post(updateButton)
        playerInteractor.createMediaPlayer(trackUrl) {
            binding.playPauseBtn.isEnabled = true
        }
    }

    private fun playbackControl() {
        when (playerState) {
            MediaPlayerState.STATE_PLAYING -> {
                playerInteractor.pause()
            }

            MediaPlayerState.STATE_PREPARED, MediaPlayerState.STATE_PAUSED -> {
                playerInteractor.play()
                handler.post(updateTimer)
            }

            else -> return
        }
    }

    private fun playPauseBtnDrawer() {
        playerState = playerInteractor.getPlayerState()
        when (playerState) {
            MediaPlayerState.STATE_DEFAULT -> {
                binding.playPauseBtn.setImageResource(R.drawable.ic_play_player_screen)
                binding.playPauseBtn.alpha = 0.5f
            }

            MediaPlayerState.STATE_PREPARED -> {
                binding.playPauseBtn.setImageResource(R.drawable.ic_play_player_screen)
                binding.playPauseBtn.alpha = 1f
                handler.removeCallbacks(updateTimer)
                binding.progressTimeTv.text = getString(R.string.start_time)
            }

            MediaPlayerState.STATE_PAUSED -> {
                binding.playPauseBtn.setImageResource(R.drawable.ic_play_player_screen)
            }

            MediaPlayerState.STATE_PLAYING -> {
                binding.playPauseBtn.setImageResource(R.drawable.ic_pause_player_screen)
            }
        }
    }

    private fun setAlbumImage(context: Context, track: Track) {
        Glide.with(context)
            .load(getCoverArtwork(track.artworkUrl100))
            .centerCrop()
            .transform(RoundedCorners(8f.toPx(context)))
            .placeholder(R.drawable.img_place_holder_player_screen)
            .into(binding.placeHolderAlbum)
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

    private fun updateButton() {
        playPauseBtnDrawer()
        handler.postDelayed(updateButton, UPDATE_DELAY)
    }

    private fun updateTimer() {
        binding.progressTimeTv.text = getCurrentPosition(playerInteractor.getElapsedTime())
        handler.postDelayed(updateTimer, UPDATE_DELAY)
    }

    private fun getCoverArtwork(artworkUrl100: String?) =
        artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")

    private fun getYear(track: Track) =
        track.releaseDate?.substringBefore('-')

    private fun getCurrentPosition(time: Int) =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)

    private fun readTrackFromJson(track: String): Track {
        return Gson().fromJson(track, Track::class.java)
    }
}