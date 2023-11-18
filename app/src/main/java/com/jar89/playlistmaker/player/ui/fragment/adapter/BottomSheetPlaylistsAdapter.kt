package com.jar89.playlistmaker.player.ui.fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.jar89.playlistmaker.R
import com.jar89.playlistmaker.albums.domain.model.Playlist
import com.jar89.playlistmaker.databinding.PlaylistBottomsheetPlayerItemBinding

class BottomSheetPlaylistsAdapter(
    private val onPlaylistClicked: PlaylistClickListener
) : RecyclerView.Adapter<BottomSheetPlaylistsViewHolder>() {

    private var playlists = emptyList<Playlist>()

    fun setPlaylists(content: List<Playlist>) {
        playlists = content
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BottomSheetPlaylistsViewHolder {
        val binding = PlaylistBottomsheetPlayerItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BottomSheetPlaylistsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BottomSheetPlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { onPlaylistClicked.onPlaylistClick(playlists[position]) }
    }

    override fun getItemCount() = playlists.size

    fun interface PlaylistClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }
}

class BottomSheetPlaylistsViewHolder(private val binding: PlaylistBottomsheetPlayerItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        with(binding) {
            with(playlist) {
                playlistTitleTv.text = name
                val numOfTracks = itemView.resources.getQuantityString(
                    R.plurals.tracks_number,
                    numberOfTracks,
                    numberOfTracks
                )
                numOfTracksTv.text = numOfTracks
                Glide.with(itemView)
                    .load(coverUri)
                    .placeholder(R.drawable.ic_place_holder_search_screen)
                    .transform(
                        CenterCrop(),
                        RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius_activity_player)),
                    )
                    .into(playlistCoverIv)
            }
        }
    }
}