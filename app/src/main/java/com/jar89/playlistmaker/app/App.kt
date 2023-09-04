package com.jar89.playlistmaker.app

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

const val DAY_NIGHT_THEME_PREFERENCES = "day_night_theme"
const val DAY_NIGHT_THEME_KEY = "day_night_theme_key"

class App : Application() {

    companion object {
        lateinit var sharedPrefs: SharedPreferences
    }

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        sharedPrefs = getSharedPreferences(DAY_NIGHT_THEME_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(DAY_NIGHT_THEME_KEY, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        darkTheme = darkThemeEnabled
        sharedPrefs.edit()
            .putBoolean(DAY_NIGHT_THEME_KEY, darkTheme)
            .apply()
    }
}