package com.jar89.playlistmaker.albums.ui.playlist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.jar89.playlistmaker.R
import com.jar89.playlistmaker.albums.domain.model.Playlist
import com.jar89.playlistmaker.albums.ui.detail_playlist.fragment.DetailPlaylistFragment
import com.jar89.playlistmaker.albums.ui.playlist.fragment.adapters.PlaylistAdapter
import com.jar89.playlistmaker.albums.ui.playlist.view_model.PlaylistState
import com.jar89.playlistmaker.albums.ui.playlist.view_model.PlaylistViewModel
import com.jar89.playlistmaker.databinding.FragmentPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private val viewModel: PlaylistViewModel by viewModel()
    private lateinit var adapter: PlaylistAdapter
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setPlaylistRv()
        binding.createNewPlaylistBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_albumsFragment_to_createPlaylistFragment,
                DetailPlaylistFragment.createArgs(0)
            )
        }

        viewModel.state.observe(viewLifecycleOwner) {
            renderState(it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllPlaylists()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setPlaylistRv() {
        adapter = PlaylistAdapter { playlist ->
            findNavController().navigate(
                R.id.action_albumsFragment_to_detailPlaylistFragment,
                DetailPlaylistFragment.createArgs(playlist.id)
            )
        }
        binding.rvPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvPlaylists.adapter = adapter
    }

    private fun renderState(state: PlaylistState) {
        when (state) {
            is PlaylistState.Empty -> showEmpty()
            is PlaylistState.Content -> showContent(state.playlists)
        }
    }

    private fun showEmpty() {
        adapter.setPlaylists(emptyList())
        binding.rvPlaylists.visibility = View.GONE
        binding.playlistsIsEmpty.visibility = View.VISIBLE
    }

    private fun showContent(playlists: List<Playlist>) {
        adapter.setPlaylists(playlists)
        binding.playlistsIsEmpty.visibility = View.GONE
        binding.rvPlaylists.visibility = View.VISIBLE
    }

    companion object {
        fun newInstance() = PlaylistFragment()
    }
}