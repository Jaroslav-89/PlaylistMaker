package com.jar89.playlistmaker.albums.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.jar89.playlistmaker.albums.ui.view_model.FavoritesState
import com.jar89.playlistmaker.albums.ui.view_model.FavoritesViewModel
import com.jar89.playlistmaker.databinding.FragmentFavoritesBinding
import com.jar89.playlistmaker.player.ui.activity.PlayerActivity
import com.jar89.playlistmaker.search.domain.model.Track
import com.jar89.playlistmaker.search.ui.adapters.TracksAdapter
import com.jar89.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment(), TracksAdapter.TrackClickListener {

    private val viewModel: FavoritesViewModel by viewModel()
    private var isClickAllowed = true
    private val favoritesTrackAdapter = TracksAdapter(this)
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val onTrackClickDebounce = debounce<Boolean>(
        CLICK_DEBOUNCE_DELAY,
        lifecycleScope,
        false
    ) { param ->
        isClickAllowed = param
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.favoritesTrackRv.adapter = favoritesTrackAdapter

        viewModel.getFavoritesTracks()

        viewModel.state.observe(viewLifecycleOwner) {
            renderState(it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavoritesTracks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onTrackClick(track: Track) {
        if (clickDebounce()) {
            val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
            playerIntent.putExtra(PlayerActivity.EXTRA_KEY_TRACK, track)
            startActivity(playerIntent)
        }
    }

    private fun renderState(state: FavoritesState) {
        when (state) {
            is FavoritesState.Empty -> showPlaceHolder()
            is FavoritesState.Content -> showFavoritesTracks(state.favorites)
        }
    }

    private fun showPlaceHolder() {
        binding.placeHolderGroup.visibility = View.VISIBLE
        binding.favoritesTrackRv.visibility = View.GONE
    }

    private fun showFavoritesTracks(favoritesTrack: List<Track>) {
        favoritesTrackAdapter.tracks.clear()
        favoritesTrackAdapter.tracks.addAll(favoritesTrack)
        favoritesTrackAdapter.notifyDataSetChanged()
        binding.placeHolderGroup.visibility = View.GONE
        binding.favoritesTrackRv.visibility = View.VISIBLE
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            onTrackClickDebounce(true)
        }
        return current
    }

    companion object {
        fun newInstance() = FavoritesFragment()

        private const val CLICK_DEBOUNCE_DELAY = 500L
    }
}