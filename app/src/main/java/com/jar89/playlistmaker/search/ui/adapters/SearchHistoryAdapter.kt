package com.jar89.playlistmaker.search.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jar89.playlistmaker.databinding.TrackViewItemBinding
import com.jar89.playlistmaker.search.domain.model.Track

class SearchHistoryAdapter(private val clickListener: TracksAdapter.TrackClickListener) :
    RecyclerView.Adapter<TracksViewHolder>() {

    var searchHistoryTracks = listOf<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val binding = TrackViewItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TracksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(searchHistoryTracks[position])
        holder.itemView.setOnClickListener { clickListener.onTrackClick(searchHistoryTracks[position]) }
    }

    override fun getItemCount() = searchHistoryTracks.size
}
