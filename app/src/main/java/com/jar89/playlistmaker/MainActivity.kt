package com.jar89.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchBtn = findViewById<Button>(R.id.searchBtn)
        val albumsBtn = findViewById<Button>(R.id.albumsBtn)
        val settingsBtn = findViewById<Button>(R.id.settingsBtn)

        searchBtn.setOnClickListener {
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }

        albumsBtn.setOnClickListener {
            val albumsIntent = Intent(this, AlbumsActivity::class.java)
            startActivity(albumsIntent)
        }

        settingsBtn.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }

    }
}