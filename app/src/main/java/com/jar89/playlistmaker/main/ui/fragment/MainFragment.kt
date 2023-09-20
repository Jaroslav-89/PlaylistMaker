package com.jar89.playlistmaker.main.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import com.jar89.playlistmaker.R
import com.jar89.playlistmaker.albums.ui.fragments.AlbumsFragment
import com.jar89.playlistmaker.databinding.FragmentMainBinding
import com.jar89.playlistmaker.search.ui.fragment.SearchFragment
import com.jar89.playlistmaker.settings.ui.fragment.SettingsFragment

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_searchFragment,
                SearchFragment.createArgs())
//            parentFragmentManager.commit {
//                replace(
//                    // Указали, в каком контейнере работаем
//                    R.id.rootFragmentContainerView,
//                    // Создали фрагмент
//                    SearchFragment.newInstance(),
//                    SearchFragment.TAG
//                )
//                addToBackStack(SearchFragment.TAG)
//            }
        }

        binding.albumsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_albumsFragment,
                AlbumsFragment.createArgs())
//            parentFragmentManager.commit {
//                replace(
//                    // Указали, в каком контейнере работаем
//                    R.id.rootFragmentContainerView,
//                    // Создали фрагмент
//                    AlbumsFragment.newInstance(),
//                    AlbumsFragment.TAG
//                )
//                addToBackStack(AlbumsFragment.TAG)
//            }
        }

        binding.settingsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_settingsFragment,
                SettingsFragment.createArgs())
//            parentFragmentManager.commit {
//                replace(
//                    // Указали, в каком контейнере работаем
//                    R.id.rootFragmentContainerView,
//                    // Создали фрагмент
//                    SettingsFragment.newInstance(),
//                    SettingsFragment.TAG
//                )
//                addToBackStack(SettingsFragment.TAG)
//            }
        }
    }
}