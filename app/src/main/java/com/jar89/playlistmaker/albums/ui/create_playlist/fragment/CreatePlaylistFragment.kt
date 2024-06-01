package com.jar89.playlistmaker.albums.ui.create_playlist.fragment

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jar89.playlistmaker.R
import com.jar89.playlistmaker.albums.ui.create_playlist.view_model.CreatePlaylistState
import com.jar89.playlistmaker.albums.ui.create_playlist.view_model.CreatePlaylistViewModel
import com.jar89.playlistmaker.databinding.FragmentCreatePlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlaylistFragment : Fragment() {

    private val viewModel: CreatePlaylistViewModel by viewModel()

    private lateinit var binding: FragmentCreatePlaylistBinding
    private var coverUri: Uri? = null
    private var textWatcher: TextWatcher? = null
    private var playlistId: Int = 0

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                setImageToPlaceHolder(uri)
                coverUri = uri
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        setTextWatcher()
        binding.playlistPlaceHolder.setOnClickListener {
            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.createBtn.setOnClickListener {
            viewModel.createPlaylist(
                name = binding.playlistTitleEt.text.toString(),
                description = binding.playlistDescriptionEt.text.toString(),
                coverUri = coverUri
            )
        }

        binding.backBtn.setOnClickListener {
            if (playlistId == 0) {
                viewModel.checkBeforeCloseScreen(
                    name = binding.playlistTitleEt.text.toString(),
                    description = binding.playlistDescriptionEt.text.toString(),
                    uri = coverUri
                )
            } else {
                findNavController().navigateUp()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.checkBeforeCloseScreen(
                name = binding.playlistTitleEt.text.toString(),
                description = binding.playlistDescriptionEt.text.toString(),
                uri = coverUri
            )
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (playlistId == 0) {
                        viewModel.checkBeforeCloseScreen(
                            name = binding.playlistTitleEt.text.toString(),
                            description = binding.playlistDescriptionEt.text.toString(),
                            uri = coverUri
                        )
                    } else {
                        findNavController().navigateUp()
                    }
                }
            })
    }

    private fun initView() {
        requireArguments().let {
            playlistId = it.getInt(ARGS_PLAYLIST)

            if (playlistId == 0) {
                binding.createBtn.text = requireContext().getString(R.string.label_create)
            } else {
                viewModel.getPlaylistById(playlistId)
                binding.createBtn.text = requireContext().getString(R.string.label_save)
            }
        }
    }

    private fun renderState(state: CreatePlaylistState) {
        when (state) {
            is CreatePlaylistState.SaveSuccess -> {
                showToast(state.name)
                closePlaylistFragment()
            }

            is CreatePlaylistState.EditInProgress -> {
                if (state.isStarted) {
                    showAlertDialog()
                } else {
                    closePlaylistFragment()
                }
            }

            is CreatePlaylistState.LoadPlaylist -> {
                if (state.playlist.coverUri != null) {
                    setImageToPlaceHolder(state.playlist.coverUri)
                }
                binding.playlistTitleEt.setText(state.playlist.name)
                binding.playlistDescriptionEt.setText(state.playlist.description)
            }
        }

    }

    private fun showToast(name: String) {
        Toast.makeText(
            requireContext(),
            getString(R.string.playlist_create, name), Toast.LENGTH_SHORT
        ).show()
    }

    private fun showAlertDialog() {
        MaterialAlertDialogBuilder(
            requireContext(),
            R.style.MaterialAlertDialog
        )
            .setTitle(getString(R.string.finish_creating_dialog_title))
            .setMessage(getString(R.string.finish_creating_dialog_message))
            .setPositiveButton(getString(R.string.finish_creating_dialog_btn_positive))
            { _, _ -> closePlaylistFragment() }
            .setNegativeButton(getString(R.string.finish_creating_dialog_btn_negative))
            { _, _ -> }
            .show()
    }

    private fun closePlaylistFragment() {
        findNavController().navigateUp()
    }

    private fun setTextWatcher() {
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                setBoxStrokeColor()
            }
        }
        binding.playlistTitleEt.addTextChangedListener(textWatcher)
        binding.playlistDescriptionEt.addTextChangedListener(textWatcher)
    }

    private fun setBoxStrokeColor() {
        if (binding.playlistTitleEt.text.isNullOrBlank()) {
            binding.createBtn.isEnabled = false
            binding.titleIl.setBoxStrokeColorStateList(
                AppCompatResources.getColorStateList(
                    requireContext(),
                    R.color.edit_text_color_selector
                )
            )
            binding.titleIl.defaultHintTextColor = AppCompatResources.getColorStateList(
                requireContext(),
                R.color.edit_text_color_selector
            )
        } else {
            binding.createBtn.isEnabled = true
            binding.titleIl.setBoxStrokeColorStateList(
                AppCompatResources.getColorStateList(
                    requireContext(),
                    R.color.edit_text_color_selector_blue
                )
            )
            binding.titleIl.defaultHintTextColor = AppCompatResources.getColorStateList(
                requireContext(),
                R.color.edit_text_color_selector_blue
            )
        }

        if (binding.playlistDescriptionEt.text.isNullOrBlank()) {
            binding.descriptionIl.setBoxStrokeColorStateList(
                AppCompatResources.getColorStateList(
                    requireContext(),
                    R.color.edit_text_color_selector
                )
            )
            binding.descriptionIl.defaultHintTextColor = AppCompatResources.getColorStateList(
                requireContext(),
                R.color.edit_text_color_selector
            )
        } else {
            binding.descriptionIl.setBoxStrokeColorStateList(
                AppCompatResources.getColorStateList(
                    requireContext(),
                    R.color.edit_text_color_selector_blue
                )
            )
            binding.descriptionIl.defaultHintTextColor = AppCompatResources.getColorStateList(
                requireContext(),
                R.color.edit_text_color_selector_blue
            )
        }
    }

    private fun setImageToPlaceHolder(uri: Uri) {
        if (!uri.toString().isNullOrBlank()) {
            Glide.with(binding.root)
                .load(uri)
                .transform(
                    CenterCrop(),
                    RoundedCorners(
                        resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius_activity_player)
                    ),
                )
                .into(binding.playlistPlaceHolder)
        }
    }

    companion object {
        private const val ARGS_PLAYLIST = "playlist"

        fun createArgs(playlistId: Int): Bundle =
            bundleOf(ARGS_PLAYLIST to playlistId)
    }
}