package com.jar89.playlistmaker.albums.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jar89.playlistmaker.albums.ui.view_model.FavoritesViewModel
import com.jar89.playlistmaker.databinding.FragmentFavoritesBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private val viewModel: FavoritesViewModel by viewModel()
    private var binding: FragmentFavoritesBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onDestroyView(){
        super.onDestroyView()
        binding = null
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }
}