package com.jar89.playlistmaker.settings.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jar89.playlistmaker.databinding.FragmentSettingsBinding
import com.jar89.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkTheme()

        observeThemeSwitcherState()

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.updateThemeSettings(isChecked)
        }

        binding.settingsShareBtn.setOnClickListener { settingsViewModel.shareApp() }

        binding.settingsSupportBtn.setOnClickListener { settingsViewModel.contactSupport() }

        binding.settingsUserAgreementBtn.setOnClickListener { settingsViewModel.openTerms() }
    }

    private fun checkTheme() {
        settingsViewModel.checkTheme()
    }

    private fun observeThemeSwitcherState() {
        settingsViewModel.themeSwitcherState.observe(viewLifecycleOwner) {
            renderState(it)
        }
    }

    private fun renderState(darkMode: Boolean) {
        binding.themeSwitcher.isChecked = darkMode
    }
}