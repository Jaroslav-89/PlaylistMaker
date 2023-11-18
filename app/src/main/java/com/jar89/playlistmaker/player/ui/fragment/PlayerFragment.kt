package com.jar89.playlistmaker.player.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.jar89.playlistmaker.R
import com.jar89.playlistmaker.databinding.FragmentPlayerBinding
import com.jar89.playlistmaker.player.domain.model.GeneralPlayerState
import com.jar89.playlistmaker.player.ui.fragment.adapter.BottomSheetPlaylistsAdapter
import com.jar89.playlistmaker.player.ui.view_model.PlayerViewModel
import com.jar89.playlistmaker.search.domain.model.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs

class PlayerFragment : Fragment() {

    private lateinit var binding: FragmentPlayerBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var adapter: BottomSheetPlaylistsAdapter
    private var track: Track? = null

    private val playerViewModel: PlayerViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        track = requireArguments().getParcelable(ARGS_TRACK)

        createPlayer(track)

        setPlaylistsRv()

        setBottomSheet()

        checkFavoriteBtn()

        setTrackInfoAndAlbumImg(track)

        setClickListeners()

        playerViewModel.generalPlayerState.observe(viewLifecycleOwner) {
            renderState(it)
        }
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.onPause()
    }

    private fun createPlayer(track: Track?) {
        if (track != null) {
            playerViewModel.createPlayer(track)
        }
    }

    private fun setClickListeners() {
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.addInAlbumBtn.setOnClickListener {
            playerViewModel.getAllPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.playPauseBtn.setOnClickListener {
            playerViewModel.onPlayButtonClicked()
        }

        binding.addInFavoriteBtn.setOnClickListener {
            playerViewModel.toggleFavorite()
        }

        binding.createPlaylistBtn.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_createPlaylistFragment)
        }
    }

    private fun setPlaylistsRv() {
        adapter = BottomSheetPlaylistsAdapter { playlist ->
            playerViewModel.addTrackToPlaylist(playlist)
        }

        binding.playlistsRv.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistsRv.adapter = adapter
    }

    private fun setBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
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

    private fun checkFavoriteBtn() {
        playerViewModel.checkFavoriteBtn()
    }

    private fun setTrackInfoAndAlbumImg(track: Track?) {
        if (track != null) {
            with(binding) {
                with(track) {
                    trackNameTv.setTextOrHide(trackName, trackNameTv)
                    artistNameTv.setTextOrHide(artistName, artistNameTv)
                    durationDescriptionTv.setTextOrHide(
                        longToTime(trackTimeMillis),
                        durationHeadingTv
                    )
                    albumDescriptionTv.setTextOrHide(collectionName, albumHeadingTv)
                    yearDescriptionTv.setTextOrHide(getYear(releaseDate), yearHeadingTv)
                    genreDescriptionTv.setTextOrHide(primaryGenreName, genreHeadingTv)
                    countryDescriptionTv.setTextOrHide(country, countryHeadingTv)

                    Glide.with(requireContext())
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
    }

    private fun TextView.setTextOrHide(text: String?, view: TextView) {
        if (text.isNullOrEmpty()) {
            this.visibility = View.GONE
            view.visibility = View.GONE
        } else {
            this.text = text
        }
    }

    private fun renderState(state: GeneralPlayerState) {
        when (state) {
            is GeneralPlayerState.PlayerState -> {
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

            is GeneralPlayerState.FavoriteState -> {
                if (state.isFavorite) {
                    binding.addInFavoriteBtn.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_favorite_active_player_screen
                        )
                    )
                } else {
                    binding.addInFavoriteBtn.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_favorite_inactive_player_screen
                        )
                    )
                }
            }

            is GeneralPlayerState.PlaylistsState -> {
                when (state) {
                    is GeneralPlayerState.PlaylistsState.AlreadyAdded -> showToast(
                        getString(
                            R.string.track_already_added,
                            state.playlistName
                        )
                    )

                    is GeneralPlayerState.PlaylistsState.WasAdded -> showTrackAdded(state.playlistName)
                    is GeneralPlayerState.PlaylistsState.ShowPlaylists -> {
                        adapter.setPlaylists(state.playlists)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showTrackAdded(playlistName: String) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        showToast(getString(R.string.track_added_in_playlist, playlistName))
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
        const val ARGS_TRACK = "track"

        fun createArgs(track: Track): Bundle =
            bundleOf(ARGS_TRACK to track)
    }
}