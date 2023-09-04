package com.jar89.playlistmaker.settings.domain.impl

import com.jar89.playlistmaker.settings.domain.api.SettingsInteractor
import com.jar89.playlistmaker.settings.domain.api.SettingsRepository
import com.jar89.playlistmaker.settings.domain.model.ThemeSettings

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository) :
    SettingsInteractor {
    override fun getThemeSettings(): ThemeSettings {
        return settingsRepository.getThemeSettings()
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        settingsRepository.updateThemeSettings(settings)
    }

}