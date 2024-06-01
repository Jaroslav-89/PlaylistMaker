package com.jar89.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jar89.playlistmaker.settings.domain.api.SettingsInteractor
import com.jar89.playlistmaker.settings.domain.model.ThemeSettings
import com.jar89.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val _themeSwitcherState = MutableLiveData<Boolean>()

    val themeSwitcherState: LiveData<Boolean>
        get() = _themeSwitcherState

    fun updateThemeSettings(isDarkModeChecked: Boolean) {
        settingsInteractor.updateThemeSettings(
            ThemeSettings(isDarkModeChecked)
        )
        _themeSwitcherState.value = settingsInteractor.getThemeSettings().darkMode
    }

    fun checkTheme() {
        _themeSwitcherState.value = settingsInteractor.getThemeSettings().darkMode
    }

    fun shareApp() {
        try {
            sharingInteractor.shareApp()
        } catch (e: Exception) {
            return
        }
    }

    fun contactSupport() {
        try {
            sharingInteractor.contactSupport()
        } catch (e: Exception) {
            return
        }
    }

    fun openTerms() {
        try {
            sharingInteractor.openTerms()
        } catch (e: Exception) {
            return
        }
    }
}