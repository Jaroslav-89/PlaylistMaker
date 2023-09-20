package com.jar89.playlistmaker.albums.ui.fragments

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

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