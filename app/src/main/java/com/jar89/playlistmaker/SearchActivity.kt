package com.jar89.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.jar89.playlistmaker.adapters.SearchHistoryAdapter
import com.jar89.playlistmaker.adapters.TracksAdapter
import com.jar89.playlistmaker.api.ITunesApi
import com.jar89.playlistmaker.api.TracksResponse
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
    }

    lateinit var sharedPrefs: SharedPreferences

    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ITunesApi::class.java)

    private lateinit var backBtn: ImageView
    private lateinit var inputEditText: EditText
    private lateinit var clearBtn: ImageView
    private lateinit var trackRv: RecyclerView
    private lateinit var searchText: String
    private lateinit var placeHolderImage: ImageView
    private lateinit var placeHolderText: TextView
    private lateinit var refreshBtn: Button
    private lateinit var searchHistoryGroup: NestedScrollView
    private lateinit var searchHistoryRv: RecyclerView
    private lateinit var clearSearchHistoryBtn: Button
    private lateinit var searchHistory: SearchHistory
    private lateinit var searchedTracks: List<Track>

    private var tracks = ArrayList<Track>()
    private val trackAdapter = TracksAdapter(this)
    private val searchHistoryAdapter = SearchHistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        sharedPrefs = getSharedPreferences(SEARCH_HISTORY_TRACKS, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs)
        backBtn = findViewById(R.id.backIv)
        inputEditText = findViewById(R.id.searchEt)
        clearBtn = findViewById(R.id.clearTextIv)
        trackRv = findViewById(R.id.trackRv)
        placeHolderText = findViewById(R.id.placeHolderText)
        placeHolderImage = findViewById(R.id.placeHolderImage)
        refreshBtn = findViewById(R.id.placeHolderRefreshButton)
        searchHistoryGroup = findViewById(R.id.searchHistoryGroup)
        searchHistoryRv = findViewById(R.id.searchHistoryRv)
        clearSearchHistoryBtn = findViewById(R.id.searchHistoryClearButton)
        trackRv.adapter = trackAdapter
        searchText = ""
        trackAdapter.tracks = tracks
        searchHistoryRv.adapter = searchHistoryAdapter
        searchedTracks = searchHistory.getSavedHistoryTracks()
        searchHistoryAdapter.searchHistoryTracks = searchedTracks
        sharedPrefs = getSharedPreferences(SEARCH_HISTORY_TRACKS, MODE_PRIVATE)

        val textWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearBtn.visibility = clearButtonVisibility(s)
                searchText = inputEditText.text.toString()
                searchHistoryGroup.visibility =
                    if (inputEditText.hasFocus() && s?.isEmpty() == true && searchedTracks.isNotEmpty()) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        inputEditText.addTextChangedListener(textWatcher)
        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            searchHistoryGroup.visibility =
                if (hasFocus && inputEditText.text.isEmpty() && searchedTracks.isNotEmpty()) View.VISIBLE else View.GONE
        }


        clearBtn.setOnClickListener {
            inputEditText.setText("")
            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            inputEditText.clearFocus()
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            rvAndPlaceHolderGone()
        }

        backBtn.setOnClickListener {
            finish()
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks()
            }
            false
        }

        refreshBtn.setOnClickListener {
            searchTracks()
        }

        clearSearchHistoryBtn.setOnClickListener {
            searchHistory.clearHistory()
            searchHistoryGroup.visibility = View.GONE
        }
    }

    private fun searchTracks() {
        rvAndPlaceHolderGone()
        itunesService.searchTrack(inputEditText.text.toString())
            .enqueue(object : Callback<TracksResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
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
                    showInternetThrowable()
                }
            })
    }

    private fun rvAndPlaceHolderGone() {
        placeHolderImage.visibility = View.GONE
        placeHolderText.visibility = View.GONE
        refreshBtn.visibility = View.GONE
        trackRv.visibility = View.GONE
    }

    private fun showTracksList(response: Response<TracksResponse>) {
        tracks.addAll(response.body()?.results!!)
        trackAdapter.notifyDataSetChanged()
        trackRv.visibility = View.VISIBLE
        trackRv.scrollToPosition(0)
    }

    private fun showSearchFail() {
        placeHolderImage.setImageResource(R.drawable.img_search_fail)
        placeHolderText.text = (getString(R.string.place_holder_text_search_fail))
        placeHolderImage.visibility = View.VISIBLE
        placeHolderText.visibility = View.VISIBLE
    }

    private fun showInternetThrowable() {
        placeHolderImage.setImageResource(R.drawable.img_internet_throwable)
        placeHolderText.text = (getString(R.string.place_holder_text_internet_throwable))
        placeHolderImage.visibility = View.VISIBLE
        placeHolderText.visibility = View.VISIBLE
        refreshBtn.visibility = View.VISIBLE
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onTrackClick(track: Track) {
        searchHistory.addTrackInHistory(track)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputEditText.setText(savedInstanceState.getString(SEARCH_TEXT, ""))
    }
}