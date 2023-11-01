package com.jar89.playlistmaker.search.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.jar89.playlistmaker.R
import com.jar89.playlistmaker.databinding.FragmentSearchBinding
import com.jar89.playlistmaker.player.ui.activity.PlayerActivity
import com.jar89.playlistmaker.search.domain.model.Track
import com.jar89.playlistmaker.search.ui.adapters.SearchHistoryAdapter
import com.jar89.playlistmaker.search.ui.adapters.TracksAdapter
import com.jar89.playlistmaker.search.ui.view_model.SearchActivityState
import com.jar89.playlistmaker.search.ui.view_model.SearchViewModel
import com.jar89.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.view.ViewGroup as ViewGroup1

class SearchFragment : Fragment(), TracksAdapter.TrackClickListener {

    private val searchViewModel: SearchViewModel by viewModel()

    private val trackAdapter = TracksAdapter(this)
    private val searchHistoryAdapter = SearchHistoryAdapter(this)
    private lateinit var binding: FragmentSearchBinding

    private val onTrackClickDebounce = debounce<Track>(
        CLICK_DEBOUNCE_DELAY,
        lifecycleScope,
        false
    ) { track ->
        searchViewModel.saveTrack(track)

        val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
        playerIntent.putExtra("track", writeTrackToJson(track))
        startActivity(playerIntent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup1?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setKeyboardFocus()

        loadSearchHistory()

        setTrackAdapter()

        setSearchHistoryAdapter()

        setTextAndFocusChangedListener()

        setClickListeners()

        searchViewModel.state.observe(viewLifecycleOwner) {
            renderState(it)
        }
    }

    override fun onResume() {
        super.onResume()
        if (binding.searchEt.text.isEmpty()) {
            loadSearchHistory()
        }
    }

    private fun renderState(it: SearchActivityState) {
        when (it) {
            is SearchActivityState.Loading -> showLoading()
            is SearchActivityState.Content -> showFoundTracks(it.tracks)
            is SearchActivityState.Empty -> showEmpty(it.message)
            is SearchActivityState.Error -> showError(it.errorMessage)
            is SearchActivityState.SearchHistory -> showHistory(it.tracks)
        }
    }

    private fun showHistory(tracks: List<Track>) {
        searchHistoryAdapter.searchHistoryTracks.clear()
        searchHistoryAdapter.searchHistoryTracks.addAll(tracks)
        binding.placeHolderImage.visibility = View.GONE
        binding.placeHolderText.visibility = View.GONE
        binding.placeHolderRefreshButton.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.trackRv.visibility = View.GONE
        if (tracks.isNotEmpty() && binding.searchEt.text.isEmpty()) {
            binding.searchHistoryGroup.visibility = View.VISIBLE
            searchHistoryAdapter.notifyDataSetChanged()
        }
    }

    private fun showError(errorMessage: String) {
        binding.searchHistoryGroup.visibility = View.GONE
        binding.placeHolderImage.visibility = View.VISIBLE
        binding.placeHolderText.visibility = View.VISIBLE
        binding.placeHolderRefreshButton.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.trackRv.visibility = View.GONE
        binding.placeHolderText.text = errorMessage
        binding.placeHolderImage.setImageResource(R.drawable.img_internet_throwable_search_screen)
    }

    private fun showEmpty(message: String) {
        binding.searchHistoryGroup.visibility = View.GONE
        binding.placeHolderImage.visibility = View.VISIBLE
        binding.placeHolderText.visibility = View.VISIBLE
        binding.placeHolderRefreshButton.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.trackRv.visibility = View.GONE
        binding.placeHolderText.text = message
        binding.placeHolderImage.setImageResource(R.drawable.img_search_fail_search_screen)
    }

    private fun showFoundTracks(foundTracks: List<Track>) {
        binding.searchHistoryGroup.visibility = View.GONE
        binding.placeHolderImage.visibility = View.GONE
        binding.placeHolderText.visibility = View.GONE
        binding.placeHolderRefreshButton.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.trackRv.visibility = View.VISIBLE
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(foundTracks)
        trackAdapter.notifyDataSetChanged()
    }

    private fun showLoading() {
        binding.trackRv.visibility = View.GONE
        binding.searchHistoryGroup.visibility = View.GONE
        binding.placeHolderImage.visibility = View.GONE
        binding.placeHolderText.visibility = View.GONE
        binding.placeHolderRefreshButton.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun loadSearchHistory() {
        searchViewModel.showHistory()
    }

    private fun setTrackAdapter() {
        binding.trackRv.adapter = trackAdapter
        trackAdapter.tracks = ArrayList<Track>()
    }

    private fun setSearchHistoryAdapter() {
        binding.searchHistoryRv.adapter = searchHistoryAdapter
        searchViewModel.showHistory()
        searchHistoryAdapter.searchHistoryTracks = ArrayList<Track>()
    }

    private fun setTextAndFocusChangedListener() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearTextIv.visibility = clearButtonVisibility(s)
                if (s != null) {
                    search()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        binding.searchEt.addTextChangedListener(textWatcher)
        binding.searchEt.setOnFocusChangeListener { view, hasFocus ->
            searchViewModel.showHistory()
        }
    }

    private fun setKeyboardFocus() {
        val keyboard =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (binding.searchEt.text.isEmpty()) {
            binding.searchEt.requestFocus()
            keyboard.showSoftInput(binding.searchEt, InputMethodManager.SHOW_IMPLICIT)
        } else {
            binding.searchEt.clearFocus()
            keyboard.hideSoftInputFromWindow(binding.searchEt.windowToken, 0)
        }
    }

    private fun setClickListeners() {
        binding.clearTextIv.setOnClickListener {
            binding.searchEt.setText("")
            setKeyboardFocus()
            trackAdapter.tracks.clear()
            trackAdapter.notifyDataSetChanged()
            binding.placeHolderImage.visibility = View.GONE
            if (searchHistoryAdapter.searchHistoryTracks.isNotEmpty()) {
                binding.searchHistoryGroup.visibility = View.VISIBLE
            }
        }

        binding.searchEt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && binding.searchEt.text.isNotEmpty()) {
                search()
            }
            false
        }

        binding.placeHolderRefreshButton.setOnClickListener {
            search()
        }

        binding.searchHistoryClearButton.setOnClickListener {
            searchViewModel.clearHistory()
            binding.searchHistoryGroup.visibility = View.GONE
            searchHistoryAdapter.notifyDataSetChanged()
        }
    }

    private fun search() {
        searchViewModel.searchDebounce(binding.searchEt.text.toString())
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onTrackClick(track: Track) {
        onTrackClickDebounce(track)
    }

    private fun writeTrackToJson(track: Track): String {
        return Gson().toJson(track)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }
}