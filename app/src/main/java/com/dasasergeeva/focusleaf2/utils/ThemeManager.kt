package com.dasasergeeva.focusleaf2.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {

    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME = "app_theme"

    enum class AppTheme {
        SYSTEM, LIGHT, DARK
    }

    /**
     * Получение текущей темы
     */
    fun getCurrentTheme(context: Context): AppTheme {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val themeName = prefs.getString(KEY_THEME, AppTheme.SYSTEM.name) ?: AppTheme.SYSTEM.name
        return try {
            AppTheme.valueOf(themeName)
        } catch (e: Exception) {
            AppTheme.SYSTEM
        }
    }

    /**
     * Установка темы
     */
    fun setTheme(context: Context, theme: AppTheme) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_THEME, theme.name).apply()
        applyTheme(theme)
    }

    /**
     * Применение темы
     */
    fun applyTheme(context: Context) {
        val theme = getCurrentTheme(context)
        applyTheme(theme)
    }

    /**
     * Применение конкретной темы
     */
    private fun applyTheme(theme: AppTheme) {
        when (theme) {
            AppTheme.SYSTEM -> {
                // Следовать системной теме
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            AppTheme.LIGHT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            AppTheme.DARK -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

    /**
     * Получение названия темы для отображения
     */
    fun getThemeName(context: Context): String {
        return when (getCurrentTheme(context)) {
            AppTheme.SYSTEM -> "Системная"
            AppTheme.LIGHT -> "Светлая"
            AppTheme.DARK -> "Тёмная"
        }
    }
}


