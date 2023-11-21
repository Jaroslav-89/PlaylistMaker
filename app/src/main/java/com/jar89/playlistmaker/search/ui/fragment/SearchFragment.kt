package com.jar89.playlistmaker.search.ui.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jar89.playlistmaker.R
import com.jar89.playlistmaker.databinding.FragmentSearchBinding
import com.jar89.playlistmaker.player.ui.fragment.PlayerFragment
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

    private var isClickAllowed = true
    private val trackAdapter = TracksAdapter(this)
    private val searchHistoryAdapter = SearchHistoryAdapter(this)
    private lateinit var binding: FragmentSearchBinding

    private val onTrackClickDebounce = debounce<Boolean>(
        CLICK_DEBOUNCE_DELAY,
        lifecycleScope,
        false
    ) { param ->
        isClickAllowed = param
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
        } else {
            searchViewModel.showTracksAfterChangeScreen()
        }
    }

    private fun renderState(it: SearchActivityState) {
        when (it) {
            is SearchActivityState.Loading -> showLoading()
            is SearchActivityState.Content -> showFoundTracks(it.tracks)
            is SearchActivityState.Empty -> showEmpty(requireContext().getString(R.string.place_holder_text_search_fail))
            is SearchActivityState.Error -> showError(requireContext().getString(R.string.place_holder_text_internet_throwable))
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
                if (s.isNullOrEmpty()) {
                    searchViewModel.showHistory()
                } else {
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
            searchViewModel.searchDebounce("")
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
        if (clickDebounce()) {
            searchViewModel.saveTrack(track)

            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment,
                PlayerFragment.createArgs(track)
            )
        }
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
        private const val CLICK_DEBOUNCE_DELAY = 500L
    }
}