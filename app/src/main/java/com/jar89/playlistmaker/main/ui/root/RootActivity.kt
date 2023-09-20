package com.jar89.playlistmaker.main.ui.root

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.jar89.playlistmaker.R
import com.jar89.playlistmaker.albums.ui.fragments.AlbumsFragment
import com.jar89.playlistmaker.databinding.ActivityRootBinding
import com.jar89.playlistmaker.main.ui.fragment.MainFragment

class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        if (savedInstanceState == null) {
//            supportFragmentManager.commit {
//                this.add(R.id.rootFragmentContainerView, MainFragment())
//            }
//        }
    }
}