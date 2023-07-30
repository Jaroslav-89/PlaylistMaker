package com.jar89.playlistmaker.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.jar89.playlistmaker.R
import com.jar89.playlistmaker.Utils.longToTime
import com.jar89.playlistmaker.Utils.toPx
import com.jar89.playlistmaker.domain.entity.Track

class TracksAdapter(private val clickListener: TrackClickListener) :
    RecyclerView.Adapter<TracksViewHolder>() {

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.track_view_item, parent, false)
        return TracksViewHolder(view)
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

class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val albumImg: ImageView = itemView.findViewById(R.id.albumPicIv)
    private val trackName: TextView = itemView.findViewById(R.id.trackNameTv)
    private val artistName: TextView = itemView.findViewById(R.id.artistNameTv)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTimeTv)

    fun bind(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = longToTime(track.trackTimeMillis)
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(2f.toPx(itemView.context)))
            .placeholder(R.drawable.ic_place_holder_search_screen)
            .into(albumImg)
    }
}


