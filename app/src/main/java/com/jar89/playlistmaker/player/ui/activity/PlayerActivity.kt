package com.jar89.playlistmaker.player.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.jar89.playlistmaker.R
import com.jar89.playlistmaker.databinding.ActivityPlayerBinding
import com.jar89.playlistmaker.player.ui.view_model.PlayerState
import com.jar89.playlistmaker.player.ui.view_model.PlayerViewModel
import com.jar89.playlistmaker.search.domain.model.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var track: Track

    private val playerViewModel: PlayerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        track = readTrackFromJson(intent.getStringExtra(EXTRA_KEY_TRACK).toString())

        createPlayer()

        setTrackInfoAndAlbumImg()

        observeViewModel()

        binding.backBtn.setOnClickListener {
            playerViewModel.releasePlayer()
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        playerViewModel.releasePlayer()
        finish()
    }

    private fun createPlayer() {
        playerViewModel.createPlayer(track.previewUrl)
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerViewModel.releasePlayer()
    }

    private fun setTrackInfoAndAlbumImg() {
        with(binding) {
            with(track) {
                trackNameTv.setTextOrHide(trackName, trackNameTv)
                artistNameTv.setTextOrHide(artistName, artistNameTv)
                durationDescriptionTv.setTextOrHide(longToTime(trackTimeMillis), durationHeadingTv)
                albumDescriptionTv.setTextOrHide(collectionName, albumHeadingTv)
                yearDescriptionTv.setTextOrHide(getYear(releaseDate), yearHeadingTv)
                genreDescriptionTv.setTextOrHide(primaryGenreName, genreHeadingTv)
                countryDescriptionTv.setTextOrHide(country, countryHeadingTv)

                Glide.with(this@PlayerActivity)
                    .load(getCoverArtwork(artworkUrl100))
                    .placeholder(R.drawable.img_place_holder_player_screen)
                    .transform(
                        CenterCrop(),
                        RoundedCorners(
                            resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius_activity_player)
                        ),
                    )
                    .into(placeHolderAlbum)
            }
        }
    }

    private fun TextView.setTextOrHide(text: String?, view: TextView) {
        if (text.isNullOrEmpty()) {
            this.visibility = View.GONE
            view.visibility = View.GONE
        } else {
            this.text = text
        }
    }

    private fun observeViewModel() {
        playerViewModel.playerState.observe(this) {
            renderState(it)
        }
        playerViewModel.elapsedTime.observe(this) {
            binding.progressTimeTv.text = it
        }
    }

    private fun renderState(state: PlayerState) {
        when (state) {
            PlayerState.STATE_PLAYING -> showPauseBtn()
            PlayerState.STATE_PAUSED, PlayerState.STATE_PREPARED -> showPlayBtn()
            PlayerState.STATE_DEFAULT -> showNotReady()
            PlayerState.STATE_ERROR -> showError()
        }
    }

    private fun showNotReady() {
        binding.playPauseBtn.setImageResource(R.drawable.ic_play_player_screen)
        binding.playPauseBtn.alpha = 0.5f
        binding.playPauseBtn.isEnabled = false
        checkReadyMediaPlayer()
    }

    private fun checkReadyMediaPlayer() {
        playerViewModel.updatePlayerState()
    }

    private fun showPlayBtn() {
        binding.playPauseBtn.setOnClickListener {
            playerViewModel.playbackControl()
        }
        binding.playPauseBtn.setImageResource(R.drawable.ic_play_player_screen)
        binding.playPauseBtn.alpha = 1f
        binding.playPauseBtn.isEnabled = true
    }

    private fun showPauseBtn() {
        binding.playPauseBtn.setOnClickListener {
            playerViewModel.playbackControl()
        }
        binding.playPauseBtn.setImageResource(R.drawable.ic_pause_player_screen)
        binding.playPauseBtn.isEnabled = true
    }

    private fun showError() {
        binding.playPauseBtn.setOnClickListener {
            showToast(getString(R.string.error_loading_preview))
        }
        binding.playPauseBtn.setImageResource(R.drawable.ic_play_player_screen)
        binding.playPauseBtn.alpha = 0.5f
    }

    private fun showToast(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun getCoverArtwork(artworkUrl100: String?) =
        artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")

    private fun getYear(date: String?) =
        date?.substringBefore('-')

    private fun longToTime(trackTime: Long?): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTime)
    }

    private fun readTrackFromJson(track: String): Track {
        return Gson().fromJson(track, Track::class.java)
    }

    companion object {
        private const val EXTRA_KEY_TRACK = "track"
    }
}