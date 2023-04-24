package com.jar89.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchBtn = findViewById<Button>(R.id.search_btn)
        val albumsBtn = findViewById<Button>(R.id.albums_btn)
        val settingsBtn = findViewById<Button>(R.id.settings_btn)

        val searchButtonClickListener: View.OnClickListener = View.OnClickListener {
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }

        searchBtn.setOnClickListener(searchButtonClickListener)



        albumsBtn.setOnClickListener {
            val albumsIntent = Intent(this, AlbumsActivity::class.java)
            startActivity(albumsIntent )
        }

        settingsBtn.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }





    }
}