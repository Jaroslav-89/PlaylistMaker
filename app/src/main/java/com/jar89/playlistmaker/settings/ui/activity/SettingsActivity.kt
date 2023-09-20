//package com.jar89.playlistmaker.settings.ui.activity
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import com.jar89.playlistmaker.databinding.ActivitySettingsBinding
//import com.jar89.playlistmaker.settings.ui.view_model.SettingsViewModel
//import org.koin.androidx.viewmodel.ext.android.viewModel
//
//class SettingsActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivitySettingsBinding
//    private val settingsViewModel: SettingsViewModel by viewModel()
//    override fun onCreate(savedInstanceState: Bundle?) {
//
//        super.onCreate(savedInstanceState)
//        binding = ActivitySettingsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        checkTheme()
//
//        observeThemeSwitcherState()
//
//        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
//            settingsViewModel.updateThemeSettings(isChecked)
//        }
//
//        binding.settingsShareBtn.setOnClickListener { settingsViewModel.shareApp() }
//
//        binding.settingsSupportBtn.setOnClickListener { settingsViewModel.contactSupport() }
//
//        binding.settingsUserAgreementBtn.setOnClickListener { settingsViewModel.openTerms() }
//
//        binding.settingsBackBtn.setOnClickListener {
//            finish()
//        }
//    }
//
//    private fun checkTheme() {
//        settingsViewModel.checkTheme()
//    }
//
//    private fun observeThemeSwitcherState() {
//        settingsViewModel.themeSwitcherState.observe(this) {
//            renderState(it)
//        }
//    }
//
//    private fun renderState(darkMode: Boolean) {
//        binding.themeSwitcher.isChecked = darkMode
//    }
//}