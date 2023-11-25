package com.jar89.playlistmaker.search.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.jar89.playlistmaker.R
import com.jar89.playlistmaker.databinding.TrackViewItemBinding
import com.jar89.playlistmaker.search.domain.model.Track
import com.jar89.playlistmaker.util.toTimeFormat

class TracksAdapter(private val clickListener: TrackClickListener) :
    RecyclerView.Adapter<TracksViewHolder>() {

    private var tracks = emptyList<Track>()

    fun setTracks(content: List<Track>) {
        tracks = content
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val binding = TrackViewItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TracksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener { clickListener.onTrackClick(tracks[position]) }
    }

    override fun getItemCount() = tracks.size

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }
}

class TracksViewHolder(private val binding: TrackViewItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        with(binding) {
            with(track) {
                trackNameTv.text = track.trackName
                artistNameTv.text = track.artistName
                trackTimeTv.text = track.trackTimeMillis?.toTimeFormat()
                Glide.with(itemView)
                    .load(track.artworkUrl100)
                    .placeholder(R.drawable.ic_place_holder_search_screen)
                    .transform(
                        CenterCrop(),
                        RoundedCorners(
                            itemView.resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius_activity_search)
                        ),
                    )
                    .into(albumPicIv)
            }
        }
    }
}


