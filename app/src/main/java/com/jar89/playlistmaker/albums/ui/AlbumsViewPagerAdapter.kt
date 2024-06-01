package com.jar89.playlistmaker.albums.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jar89.playlistmaker.albums.ui.favorites.fragment.FavoritesFragment
import com.jar89.playlistmaker.albums.ui.playlist.fragment.PlaylistFragment

class AlbumsViewPagerAdapter(parentFragment: Fragment) :
    FragmentStateAdapter(parentFragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoritesFragment.newInstance()
            else -> PlaylistFragment.newInstance()
        }
    }
}