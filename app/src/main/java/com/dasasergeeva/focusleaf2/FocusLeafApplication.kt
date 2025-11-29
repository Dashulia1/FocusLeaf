package com.dasasergeeva.focusleaf2

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.dasasergeeva.focusleaf2.repository.ProjectRepository
import com.dasasergeeva.focusleaf2.utils.LocaleManager
import com.dasasergeeva.focusleaf2.utils.ThemeManager
import java.util.Locale

/**
 * Application класс для инициализации русского языка и темы по умолчанию
 */
class FocusLeafApplication : Application() {

    companion object {
        @JvmStatic
        lateinit var instance: FocusLeafApplication
            private set
    }

    override fun attachBaseContext(base: Context) {
        // Применяем русскую локаль при создании Application
        val localeManager = LocaleManager(base)
        val language = localeManager.getSavedLanguage()
        val locale = Locale(language)
        Locale.setDefault(locale)
        
        val configuration = Configuration(base.resources.configuration)
        configuration.setLocale(locale)
        
        val context = base.createConfigurationContext(configuration)
        super.attachBaseContext(context)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Применяем тему при запуске приложения
        ThemeManager.applyTheme(this)
        
        setRussianLocale()
        
        // Инициализация репозитория и загрузка проектов при запуске приложения
        ProjectRepository.init(this)
        ProjectRepository.loadProjects()
    }
    
    private fun setRussianLocale() {
        val locale = Locale("ru")
        Locale.setDefault(locale)
        
        val resources = resources
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        
        resources.updateConfiguration(configuration, resources.displayMetrics)
        
        // Сохраняем выбор языка
        val prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        prefs.edit().putString("app_language", "ru").apply()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val localeManager = LocaleManager(this)
        val language = localeManager.getSavedLanguage()
        val locale = Locale(language)
        Locale.setDefault(locale)
        newConfig.setLocale(locale)
        resources.updateConfiguration(newConfig, resources.displayMetrics)
    }
}

