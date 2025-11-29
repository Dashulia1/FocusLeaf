package com.dasasergeeva.focusleaf2.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale

/**
 * Утилита для управления локалью приложения и поддержки русского языка для ввода
 */
class LocaleManager(private val context: Context) {

    /**
     * Установить локаль приложения
     * @param languageCode Код языка (например, "ru" для русского, "en" для английского)
     */
    fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources: Resources = context.resources
        val configuration: Configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        resources.updateConfiguration(configuration, resources.displayMetrics)

        // Сохраняем выбор языка
        val prefs = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        prefs.edit().putString("app_language", languageCode).apply()
    }

    /**
     * Получить сохраненный язык приложения
     * @return Код языка (по умолчанию "ru")
     */
    fun getSavedLanguage(): String {
        val prefs = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return prefs.getString("app_language", "ru") ?: "ru"
    }

    /**
     * Применить сохраненную локаль к контексту
     * Используйте этот метод в onCreate() каждой Activity или в Application
     */
    fun applySavedLocale(context: Context): Context {
        val language = getSavedLanguage()
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources: Resources = context.resources
        val configuration: Configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return context.createConfigurationContext(configuration)
    }
}

