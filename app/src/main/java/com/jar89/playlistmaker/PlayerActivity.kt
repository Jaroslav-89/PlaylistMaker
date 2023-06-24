package com.jar89.playlistmaker

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.jar89.playlistmaker.Utils.longToTime
import com.jar89.playlistmaker.Utils.toPx
import com.jar89.playlistmaker.databinding.ActivytyPlayerBinding
import com.jar89.playlistmaker.model.Track

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivytyPlayerBinding
    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivytyPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        track = readTrackFromJson(intent.getStringExtra("track").toString())

        setAlbumImage(this, track)

        initViews()

    }

    private fun initViews(){
        binding.trackNameTv.text = track.trackName
        binding.artistNameTv.text = track.artistName
        binding.durationDescriptionTv.text = longToTime(track.trackTimeMillis)
        setCollectionName(track)
        binding.yearDescriptionTv.text = getYear(track)
        binding.genreDescriptionTv.text = track.primaryGenreName
        binding.countryDescriptionTv.text = track.country
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun setAlbumImage(context: Context, track: Track) {
        Glide.with(context)
            .load(getCoverArtwork(track.artworkUrl100))
            .centerInside()
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
}