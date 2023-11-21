package com.jar89.playlistmaker.albums.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.jar89.playlistmaker.R
import com.jar89.playlistmaker.albums.domain.model.Playlist
import com.jar89.playlistmaker.databinding.PlaylistAlbumsItemBinding

class PlaylistAdapter(private val clickListener: PlaylistClickListener) :
    RecyclerView.Adapter<PlaylistViewHolder>() {

    private var playlists = emptyList<Playlist>()

    fun setPlaylists(content: List<Playlist>) {
        playlists = content
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = PlaylistAlbumsItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding)
    }

    override fun getItemCount() = playlists.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { clickListener.onClick(playlists[position]) }
    }

    fun interface PlaylistClickListener {
        fun onClick(playlist: Playlist)
    }
}

class PlaylistViewHolder(private val binding: PlaylistAlbumsItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        with(binding) {
            with(playlist) {
                tvPlaylistTitle.text = name
                val numOfTracks = itemView.resources.getQuantityString(
                    R.plurals.tracks_number,
                    numberOfTracks,
                    numberOfTracks
                )
                tvNumOfTracks.text = numOfTracks
                Glide.with(itemView)
                    .load(coverUri)
                    .placeholder(R.drawable.img_playlist_albums_placeholder)
                    .transform(
                        CenterCrop(),
                        RoundedCorners(
                            itemView.resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius_activity_player)
                        ),
                    )
                    .into(ivPlaylistCover)
            }
        }
    }
}