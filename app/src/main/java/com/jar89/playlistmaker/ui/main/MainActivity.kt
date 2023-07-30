package com.jar89.playlistmaker.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jar89.playlistmaker.ui.search.SearchActivity
import com.jar89.playlistmaker.ui.settings.SettingsActivity
import com.jar89.playlistmaker.databinding.ActivityMainBinding
import com.jar89.playlistmaker.ui.albums.AlbumsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchBtn.setOnClickListener {
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }

        binding.albumsBtn.setOnClickListener {
            val albumsIntent = Intent(this, AlbumsActivity::class.java)
            startActivity(albumsIntent)
        }

        binding.settingsBtn.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }

    }
}