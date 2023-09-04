package com.jar89.playlistmaker.settings.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _themeSwitcherState = MutableLiveData<Boolean>()

    private val settingsInteractor = Creator.provideSettingsInteractor(application)
    private val sharingInteractor = Creator.provideSharingInteractor(application)

    val themeSwitcherState: LiveData<Boolean>
        get() = _themeSwitcherState

    fun updateThemeSettings(isDarkModeChecked: Boolean) {
        settingsInteractor.updateThemeSettings(
            ThemeSettings(isDarkModeChecked)
        )
        _themeSwitcherState.value = settingsInteractor.getThemeSettings().darkMode
    }

    fun checkTheme(){
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