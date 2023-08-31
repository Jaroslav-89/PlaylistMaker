package com.jar89.playlistmaker.settings.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jar89.playlistmaker.creator.Creator
import com.jar89.playlistmaker.settings.domain.model.ThemeSettings

class SettingsViewModel(
    private val application: Application
) : AndroidViewModel(application) {

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
            }
        }
    }

    private val sharingRepository = Creator.provideSharingRepository(application)
    private val settingsRepository = Creator.provideSettingsRepository(application)

    fun isDarkMode(): Boolean {
        return settingsRepository.getThemeSettings().darkMode
    }

    fun updateThemeSettings(isDarkModeChecked: Boolean) {
        settingsRepository.updateThemeSettings(
            ThemeSettings(isDarkModeChecked)
        )
    }

    fun shareApp() {
        try {
            sharingRepository.shareApp()
        } catch (e: Exception) {
            return
        }
    }

    fun contactSupport() {
        try {
            sharingRepository.contactSupport()
        } catch (e: Exception) {
            return
        }
    }

    fun openTerms() {
        try {
            sharingRepository.openTerms()
        } catch (e: Exception) {
            return
        }
    }
}