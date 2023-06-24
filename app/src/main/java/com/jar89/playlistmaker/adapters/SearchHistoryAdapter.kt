package com.jar89.playlistmaker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jar89.playlistmaker.R
import com.jar89.playlistmaker.model.Track

class SearchHistoryAdapter(private val clickListener: TracksAdapter.TrackClickListener) :
    RecyclerView.Adapter<TracksViewHolder>() {

    var searchHistoryTracks = listOf<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.track_view_item, parent, false)
        return TracksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(searchHistoryTracks[position])
        holder.itemView.setOnClickListener { clickListener.onTrackClick(searchHistoryTracks[position]) }
    }

    override fun getItemCount() = searchHistoryTracks.size
}
