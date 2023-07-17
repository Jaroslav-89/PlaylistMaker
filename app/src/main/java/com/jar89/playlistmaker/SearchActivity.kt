package com.jar89.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.jar89.playlistmaker.adapters.SearchHistoryAdapter
import com.jar89.playlistmaker.adapters.TracksAdapter
import com.jar89.playlistmaker.api.ITunesApi
import com.jar89.playlistmaker.api.TracksResponse
import com.jar89.playlistmaker.databinding.ActivitySearchBinding
import com.jar89.playlistmaker.model.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val SEARCH_HISTORY_TRACKS = "search_history_tracks"

class SearchActivity : AppCompatActivity(), TracksAdapter.TrackClickListener {
    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchTracks() }
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var binding: ActivitySearchBinding

    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ITunesApi::class.java)

    private lateinit var searchHistoryObj: SearchHistory
    private lateinit var searchedTracks: List<Track>

    private var tracks = ArrayList<Track>()
    private val trackAdapter = TracksAdapter(this)
    private val searchHistoryAdapter = SearchHistoryAdapter(this)
    private var searchText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setKeyboardFocus()

        getHistoryFromsharedPrefs()

        setTrackAdapter()

        setSearchHistoryAdapter()

        showHistoryTrackRv()

        setTextAndFocusChangedListener()

        setClickListeners()

    }

    override fun onResume() {
        super.onResume()
        searchHistoryAdapter.searchHistoryTracks = searchHistoryObj.getSavedHistoryTracks()
        searchHistoryAdapter.notifyDataSetChanged()
    }

    private fun getHistoryFromsharedPrefs() {
        sharedPrefs = getSharedPreferences(SEARCH_HISTORY_TRACKS, MODE_PRIVATE)
        searchHistoryObj = SearchHistory(sharedPrefs)
        searchedTracks = searchHistoryObj.getSavedHistoryTracks()
    }

    private fun setTrackAdapter() {
        binding.trackRv.adapter = trackAdapter
        trackAdapter.tracks = tracks
    }

    private fun setSearchHistoryAdapter() {
        binding.searchHistoryRv.adapter = searchHistoryAdapter
        searchHistoryAdapter.searchHistoryTracks = searchedTracks
    }

    private fun setTextAndFocusChangedListener() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                rvAndPlaceHolderGone()
                binding.clearTextIv.visibility = clearButtonVisibility(s)
                if (binding.searchEt.text.isNotEmpty()) {
                    binding.searchHistoryGroup.visibility = View.GONE
                    binding.trackRv.visibility = View.VISIBLE
                    searchText = binding.searchEt.text.toString()
                    searchDebounce()
                } else {
                    handler.removeCallbacks(searchRunnable)
                    showHistoryTrackRv()
                    tracks.clear()
                    trackAdapter.notifyDataSetChanged()
                }
            }
        }

        binding.searchEt.addTextChangedListener(textWatcher)
        binding.searchEt.setOnFocusChangeListener { view, hasFocus ->
            showHistoryTrackRv()
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
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            rvAndPlaceHolderGone()
            searchHistoryAdapter.searchHistoryTracks = searchHistoryObj.getSavedHistoryTracks()
            searchHistoryAdapter.notifyDataSetChanged()
            showHistoryTrackRv()
        }

        binding.searchEt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && binding.searchEt.text.isNotEmpty()) {
                searchTracks()
                handler.removeCallbacks(searchRunnable)
            }
            false
        }

        binding.placeHolderRefreshButton.setOnClickListener {
            searchTracks()
        }

        binding.searchHistoryClearButton.setOnClickListener {
            searchHistoryObj.clearHistory()
            binding.searchHistoryGroup.visibility = View.GONE
        }

        binding.backIv.setOnClickListener {
            finish()
        }
    }

    private fun searchTracks() {
        rvAndPlaceHolderGone()
        if (searchText.isNotEmpty()) {
            binding.progressBar.visibility = View.VISIBLE
            itunesService.searchTrack(searchText)
                .enqueue(object : Callback<TracksResponse> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(
                        call: Call<TracksResponse>,
                        response: Response<TracksResponse>
                    ) {
                        binding.progressBar.visibility = View.GONE
                        if (response.code() == 200) {
                            tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                showTracksList(response)
                            }
                            if (tracks.isEmpty()) {
                                showSearchFail()
                            }
                        } else {
                            showInternetThrowable()
                        }
                    }

                    override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                        binding.progressBar.visibility = View.GONE
                        showInternetThrowable()
                    }
                })
        }
    }

    private fun rvAndPlaceHolderGone() {
        binding.placeHolderImage.visibility = View.GONE
        binding.placeHolderText.visibility = View.GONE
        binding.placeHolderRefreshButton.visibility = View.GONE
        binding.trackRv.visibility = View.GONE
    }

    private fun showTracksList(response: Response<TracksResponse>) {
        tracks.addAll(response.body()?.results!!)
        trackAdapter.notifyDataSetChanged()
        binding.trackRv.visibility = View.VISIBLE
        binding.trackRv.scrollToPosition(0)
    }

    private fun showSearchFail() {
        binding.placeHolderImage.apply {
            setImageResource(R.drawable.img_search_fail)
            visibility = View.VISIBLE
        }
        binding.placeHolderText.apply {
            text = (getString(R.string.place_holder_text_search_fail))
            visibility = View.VISIBLE
        }
    }

    private fun showInternetThrowable() {
        binding.placeHolderImage.apply {
            setImageResource(R.drawable.img_internet_throwable)
            visibility = View.VISIBLE
        }
        binding.placeHolderText.apply {
            text = (getString(R.string.place_holder_text_internet_throwable))
            visibility = View.VISIBLE
        }
        binding.placeHolderRefreshButton.visibility = View.VISIBLE
    }

    private fun showHistoryTrackRv() {
        binding.searchHistoryGroup.visibility =
            if (binding.searchEt.text.isEmpty() && searchHistoryAdapter.searchHistoryTracks.isNotEmpty()) View.VISIBLE else View.GONE
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
            searchHistoryObj.addTrackInHistory(track)

            val playerIntent = Intent(this, PlayerActivity::class.java)
            playerIntent.putExtra("track", writeTrackToJson(track))
            startActivity(playerIntent)
        }
    }

    private fun writeTrackToJson(track: Track): String {
        return Gson().toJson(track)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.searchEt.setText(savedInstanceState.getString(SEARCH_TEXT, ""))
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
}
