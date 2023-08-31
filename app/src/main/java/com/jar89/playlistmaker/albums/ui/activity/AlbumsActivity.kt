package com.jar89.playlistmaker.albums.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jar89.playlistmaker.databinding.ActivityAlbumsBinding

class AlbumsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlbumsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}