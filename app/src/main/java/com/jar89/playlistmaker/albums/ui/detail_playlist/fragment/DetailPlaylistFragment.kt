package com.jar89.playlistmaker.albums.ui.detail_playlist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jar89.playlistmaker.R
import com.jar89.playlistmaker.albums.domain.model.Playlist
import com.jar89.playlistmaker.albums.ui.create_playlist.fragment.CreatePlaylistFragment
import com.jar89.playlistmaker.albums.ui.detail_playlist.fragment.adapter.DetailPlaylistAdapter
import com.jar89.playlistmaker.albums.ui.detail_playlist.view_model.DetailPlaylistState
import com.jar89.playlistmaker.albums.ui.detail_playlist.view_model.DetailPlaylistViewModel
import com.jar89.playlistmaker.databinding.FragmentPlaylistDetailBinding
import com.jar89.playlistmaker.player.ui.fragment.PlayerFragment
import com.jar89.playlistmaker.search.domain.model.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs

class DetailPlaylistFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistDetailBinding
    private lateinit var trackBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private var playlistId: Int = 0
    private var playlistName: String = ""

    private val viewModel: DetailPlaylistViewModel by viewModel()

    private val adapter = DetailPlaylistAdapter(
        object : DetailPlaylistAdapter.TrackClickListener {
            override fun onTrackClick(track: Track) {
                findNavController().navigate(
                    R.id.action_detailPlaylistFragment_to_playerFragment,
                    PlayerFragment.createArgs(track = track)
                )
            }

            override fun onTrackLongClick(track: Track): Boolean {
                showDeleteTrackDialog(track)
                return true
            }
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistDetailBinding.inflate(inflater, container, false)
        binding.root.doOnNextLayout {
            calculatePeekHeight()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireArguments().let {
            playlistId = it.getInt(ARGS_PLAYLIST)
        }

        viewModel.getPlaylistById(playlistId)

        setBottomSheets()

        setTracksRv()

        setClickListeners()

        viewModel.state.observe(viewLifecycleOwner) {
            renderState(it)
        }


    }

    private fun setBottomSheets() {
        trackBottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet).apply {
            state = STATE_COLLAPSED
        }
        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = STATE_HIDDEN
        }

        menuBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = 1 - abs(slideOffset)
            }
        })
    }

    private fun setTracksRv() {
        binding.tracksRv.layoutManager = LinearLayoutManager(requireContext())
        binding.tracksRv.adapter = adapter
    }

    private fun setClickListeners() {
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.shareBtn.setOnClickListener {
            menuBottomSheetBehavior.state = STATE_HIDDEN
            viewModel.sharePlaylist(requireContext())
        }

        binding.menuBtn.setOnClickListener {
            menuBottomSheetBehavior.state = STATE_COLLAPSED
            binding.menuBottomSheet.visibility = View.VISIBLE
        }

        binding.shareMenuBottomSheetTv.setOnClickListener {
            menuBottomSheetBehavior.state = STATE_HIDDEN

            viewModel.sharePlaylist(requireContext())
        }

        binding.editMenuBottomSheetTv.setOnClickListener {
            findNavController().navigate(
                R.id.action_detailPlaylistFragment_to_createPlaylistFragment,
                CreatePlaylistFragment.createArgs(playlistId)
            )
        }

        binding.deleteMenuBottomSheetTv.setOnClickListener {
            menuBottomSheetBehavior.state = STATE_HIDDEN
            MaterialAlertDialogBuilder(
                requireContext(),
                R.style.MaterialAlertDialog
            )
                .setTitle(getString(R.string.title_delete_playlist_dialog, playlistName))
                .setPositiveButton(getString(R.string.dialog_btn_positive))
                { _, _ ->
                    viewModel.deletePlaylist()
                }
                .setNegativeButton(getString(R.string.dialog_btn_negative))
                { _, _ -> }
                .show()
        }
    }

    private fun renderState(state: DetailPlaylistState) {
        when (state) {
            is DetailPlaylistState.Content -> {
                showPlaylistInfo(state.playlist)
                showTrackRv(state.trackList)
            }

            is DetailPlaylistState.Message -> {

            }

            is DetailPlaylistState.PlaylistDeleted -> {
                findNavController().navigateUp()
            }
        }
    }

    private fun showPlaylistInfo(playlist: Playlist) {
        with(binding) {
            with(playlist) {
                if (!playlist.coverUri.toString().isNullOrBlank()) {
                    Glide.with(root)
                        .load(coverUri)
                        .transform(
                            CenterCrop(),
                            RoundedCorners(
                                resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius_activity_player)
                            ),
                        )
                        .into(placeHolderPlaylistDetail)

                    Glide.with(root)
                        .load(coverUri)
                        .transform(
                            CenterCrop(),
                            RoundedCorners(
                                resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius_activity_search)
                            ),
                        )
                        .into(playlistMenuBottomSheet.playlistCoverIv)
                }

                playlistTitleTv.text = name
                playlistName = name
                playlistDescriptionTv.text = description
                totalDurationTv.text = resources.getQuantityString(
                    R.plurals.minutes_number,
                    longToMinInt(playlistDuration),
                    longToTime(playlistDuration)
                )
                tracksTotalTv.text = resources.getQuantityString(
                    R.plurals.tracks_number,
                    numberOfTracks,
                    numberOfTracks
                )
                playlistMenuBottomSheet.playlistTitleTv.text = name
                playlistMenuBottomSheet.numOfTracksTv.text = resources.getQuantityString(
                    R.plurals.tracks_number,
                    numberOfTracks,
                    numberOfTracks
                )
            }
        }
    }

    private fun showTrackRv(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            binding.placeholderMessage.visibility = View.VISIBLE
            binding.tracksRv.visibility = View.GONE
        } else {
            adapter.setTracks(tracks)
            adapter.notifyDataSetChanged()
            binding.placeholderMessage.visibility = View.GONE
            binding.tracksRv.visibility = View.VISIBLE
        }

    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(
            requireContext(),
            R.style.MaterialAlertDialog
        )
            .setTitle(getString(R.string.delete_track_dialog_title))
            .setPositiveButton(getString(R.string.dialog_btn_positive))
            { _, _ ->
                viewModel.deleteTrack(track)
            }
            .setNegativeButton(getString(R.string.dialog_btn_negative))
            { _, _ -> }
            .show()
    }

    private fun longToMinInt(tracksTime: Long): Int {
        return (tracksTime.toInt() / 60000)
    }

    private fun longToTime(trackTime: Long?): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTime)
    }

    private fun calculatePeekHeight() {
        val screenHeight = binding.root.height
        menuBottomSheetBehavior.peekHeight =
            (screenHeight - binding.playlistTitleTv.bottom).coerceAtLeast(
                BS_MIN_SIZE_PX
            )
        trackBottomSheetBehavior.peekHeight =
            (screenHeight - binding.shareBtn.bottom - BS_TRACKS_OFFSET_PX).coerceAtLeast(
                BS_MIN_SIZE_PX
            )
        trackBottomSheetBehavior.state = STATE_COLLAPSED
    }

    companion object {
        private const val ARGS_PLAYLIST = "playlist"
        private const val BS_TRACKS_OFFSET_PX = 24
        private const val BS_MIN_SIZE_PX = 100

        fun createArgs(playlistId: Int): Bundle =
            bundleOf(ARGS_PLAYLIST to playlistId)
    }
}