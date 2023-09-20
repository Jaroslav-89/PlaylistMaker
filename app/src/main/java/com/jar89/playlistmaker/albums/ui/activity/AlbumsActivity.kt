//package com.jar89.playlistmaker.albums.ui.activity
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import com.google.android.material.tabs.TabLayoutMediator
//import com.jar89.playlistmaker.R
//import com.jar89.playlistmaker.albums.ui.fragments.AlbumsViewPagerAdapter
//import com.jar89.playlistmaker.databinding.ActivityAlbumsBinding
//
//class AlbumsActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityAlbumsBinding
//    private lateinit var tabMediator: TabLayoutMediator
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityAlbumsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.viewPager.adapter = AlbumsViewPagerAdapter(supportFragmentManager, lifecycle)
//
//        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
//            when (position) {
//                0 -> tab.text = getString(R.string.favorite_tracks)
//                1 -> tab.text = getString(R.string.playlists)
//            }
//        }
//        tabMediator.attach()
//
//        binding.backBtn.setOnClickListener {
//            finish()
//        }
//    }
//    override fun onDestroy() {
//        super.onDestroy()
//        tabMediator.detach()
//    }
//}