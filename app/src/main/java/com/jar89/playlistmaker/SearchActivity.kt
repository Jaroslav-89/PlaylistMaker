package com.jar89.playlistmaker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.jar89.playlistmaker.adapters.TracksAdapter
import com.jar89.playlistmaker.api.ITunesApi
import com.jar89.playlistmaker.api.TracksResponse
import com.jar89.playlistmaker.model.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : Activity() {
    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }

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
    private lateinit var refreshButton: Button

    private val tracks = ArrayList<Track>()
    private val trackAdapter = TracksAdapter()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        backBtn = findViewById(R.id.backIv)
        inputEditText = findViewById(R.id.searchEt)
        clearBtn = findViewById(R.id.clearTextIv)
        trackRv = findViewById(R.id.trackRv)
        placeHolderText = findViewById(R.id.placeHolderText)
        placeHolderImage = findViewById(R.id.placeHolderImage)
        refreshButton = findViewById(R.id.placeHolderRefreshButton)
        trackRv.adapter = trackAdapter

        trackAdapter.tracks = tracks

        val textWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearBtn.visibility = clearButtonVisibility(s)
                searchText = inputEditText.text.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        inputEditText.addTextChangedListener(textWatcher)

        clearBtn.setOnClickListener {
            inputEditText.setText("")
            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            inputEditText.clearFocus()
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
        }

        backBtn.setOnClickListener {
            finish()
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks()
                true
            }
            false
        }

        refreshButton.setOnClickListener {
            searchTracks()
        }
    }

    private fun searchTracks() {
        placeHolderImage.visibility = View.INVISIBLE
        placeHolderText.visibility = View.INVISIBLE
        refreshButton.visibility = View.INVISIBLE
        trackRv.visibility = View.INVISIBLE
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
                            tracks.addAll(response.body()?.results!!)
                            trackAdapter.notifyDataSetChanged()
                            trackRv.visibility = View.VISIBLE
                        }
                        if (tracks.isEmpty()) {
                            placeHolderImage.setImageResource(R.drawable.img_search_fail)
                            placeHolderText.text = (getString(R.string.place_holder_text_search_fail))
                            placeHolderImage.visibility = View.VISIBLE
                            placeHolderText.visibility = View.VISIBLE
                        }
                    } else {
                        placeHolderImage.setImageResource(R.drawable.img_internet_throwable)
                        placeHolderText.text = (getString(R.string.place_holder_text_internet_throwable))
                        placeHolderImage.visibility = View.VISIBLE
                        placeHolderText.visibility = View.VISIBLE
                        refreshButton.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    placeHolderImage.setImageResource(R.drawable.img_internet_throwable)
                    placeHolderText.text = (getString(R.string.place_holder_text_internet_throwable))
                    placeHolderImage.visibility = View.VISIBLE
                    placeHolderText.visibility = View.VISIBLE
                    refreshButton.visibility = View.VISIBLE
                }

            })
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
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