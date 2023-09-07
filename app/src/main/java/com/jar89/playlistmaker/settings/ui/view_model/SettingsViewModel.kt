package com.jar89.playlistmaker.settings.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jar89.playlistmaker.settings.domain.api.SettingsInteractor
import com.jar89.playlistmaker.settings.domain.model.ThemeSettings
import com.jar89.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    application: Application,
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : AndroidViewModel(application) {

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