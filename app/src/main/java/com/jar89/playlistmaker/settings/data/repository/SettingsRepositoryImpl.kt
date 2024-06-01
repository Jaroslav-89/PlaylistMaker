package com.jar89.playlistmaker.settings.data.repository

import android.content.Context
import com.jar89.playlistmaker.app.App
import com.jar89.playlistmaker.settings.domain.api.SettingsRepository
import com.jar89.playlistmaker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {
    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(
            darkMode = (context as App).darkTheme
        )
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        (context as App).switchTheme(settings.darkMode)
    }
}