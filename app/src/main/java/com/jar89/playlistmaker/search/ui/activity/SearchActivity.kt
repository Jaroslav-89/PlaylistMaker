package com.jar89.playlistmaker.search.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.jar89.playlistmaker.R
import com.jar89.playlistmaker.databinding.ActivitySearchBinding
import com.jar89.playlistmaker.search.domain.model.Track
import com.jar89.playlistmaker.player.ui.activity.PlayerActivity
import com.jar89.playlistmaker.search.ui.view_model.SearchActivityState
import com.jar89.playlistmaker.search.ui.adapters.SearchHistoryAdapter
import com.jar89.playlistmaker.search.ui.adapters.TracksAdapter
import com.jar89.playlistmaker.search.ui.view_model.SearchViewModel


class SearchActivity : AppCompatActivity(), TracksAdapter.TrackClickListener {
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchViewModel: SearchViewModel

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val trackAdapter = TracksAdapter(this)
    private val searchHistoryAdapter = SearchHistoryAdapter(this)
    private var searchText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()

        setKeyboardFocus()

        loadSearchHistory()

        setTrackAdapter()

        setSearchHistoryAdapter()

        setTextAndFocusChangedListener()

        setClickListeners()

        searchViewModel.state.observe(this) {
            renderState(it)
        }
    }

    override fun onResume() {
        super.onResume()
        loadSearchHistory()
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
        if (tracks.isNotEmpty()) {
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

    private fun initViewModel() {
        searchViewModel = ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory()
        )[SearchViewModel::class.java]
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
        val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (searchText.isEmpty()) {
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

        binding.backIv.setOnClickListener {
            finish()
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

            val playerIntent = Intent(this, PlayerActivity::class.java)
            playerIntent.putExtra("track", writeTrackToJson(track))
            startActivity(playerIntent)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun writeTrackToJson(track: Track): String {
        return Gson().toJson(track)
    }
}
