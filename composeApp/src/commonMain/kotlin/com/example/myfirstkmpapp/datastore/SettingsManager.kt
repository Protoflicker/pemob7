package com.example.myfirstkmpapp.datastore

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import kotlinx.coroutines.flow.Flow

class SettingsManager(settings: Settings) {

    private val flowSettings: FlowSettings = (settings as ObservableSettings).toFlowSettings()

    val themeFlow: Flow<String> = flowSettings.getStringFlow("app_theme", "Sistem")

    suspend fun setTheme(theme: String) {
        flowSettings.putString("app_theme", theme)
    }
}