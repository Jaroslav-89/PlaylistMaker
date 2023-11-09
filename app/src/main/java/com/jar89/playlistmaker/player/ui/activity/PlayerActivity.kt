package com.jar89.playlistmaker.player.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.jar89.playlistmaker.R
import com.jar89.playlistmaker.databinding.ActivityPlayerBinding
import com.jar89.playlistmaker.player.domain.model.PlayerState
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

        track = intent.getParcelableExtra<Track>(EXTRA_KEY_TRACK)!!

        createPlayer(track)

        checkFavoriteBtn()

        setTrackInfoAndAlbumImg()

        binding.backBtn.setOnClickListener {
            finish()
        }

        playerViewModel.isFavorite.observe(this) {
            renderFavoriteBtn(it)
        }

        playerViewModel.playerState.observe(this) {
            renderState(it)
        }

        binding.playPauseBtn.setOnClickListener {
            playerViewModel.onPlayButtonClicked()
        }

        binding.addInFavoriteBtn.setOnClickListener {
            playerViewModel.toggleFavorite()
        }
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.onPause()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun createPlayer(track: Track) {
        playerViewModel.createPlayer(track)
    }

    private fun checkFavoriteBtn() {
        playerViewModel.checkFavoriteBtn()
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

    private fun renderState(state: PlayerState) {
        if (state.buttonIsPlay) {
            showPlayBtn()
        } else {
            showPauseBtn()
        }

        if (!state.isPlayButtonEnabled) {
            showNotReady()
        }

        binding.progressTimeTv.text = getCurrentPosition(state.progress)
    }

    private fun renderFavoriteBtn(isFavorite: Boolean) {
        if (isFavorite) {
            binding.addInFavoriteBtn.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.ic_favorite_active_player_screen
                )
            )
        } else {
            binding.addInFavoriteBtn.setImageDrawable(
                AppCompatResources.getDrawable(this, R.drawable.ic_favorite_inactive_player_screen)
            )
        }
    }

    private fun showNotReady() {
        binding.playPauseBtn.setImageResource(R.drawable.ic_play_player_screen)
        binding.playPauseBtn.alpha = 0.5f
        binding.playPauseBtn.isEnabled = false
    }

    private fun showPlayBtn() {
        binding.playPauseBtn.setImageResource(R.drawable.ic_play_player_screen)
        binding.playPauseBtn.alpha = 1f
        binding.playPauseBtn.isEnabled = true
    }

    private fun showPauseBtn() {
        binding.playPauseBtn.setOnClickListener {
            playerViewModel.onPlayButtonClicked()
        }
        binding.playPauseBtn.setImageResource(R.drawable.ic_pause_player_screen)
        binding.playPauseBtn.isEnabled = true
    }

    private fun getCoverArtwork(artworkUrl100: String?) =
        artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")

    private fun getYear(date: String?) =
        date?.substringBefore('-')

    private fun longToTime(trackTime: Long?): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTime)
    }

    private fun getCurrentPosition(time: Int) =
        android.icu.text.SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)

    companion object {
        const val EXTRA_KEY_TRACK = "track"
    }
}