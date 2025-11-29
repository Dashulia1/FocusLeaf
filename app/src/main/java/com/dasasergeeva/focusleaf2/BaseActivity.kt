package com.dasasergeeva.focusleaf2

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import com.dasasergeeva.focusleaf2.utils.LocaleManager
import com.dasasergeeva.focusleaf2.utils.ThemeManager
import java.util.Locale

/**
 * Базовая активность для поддержки русского языка и тем во всём приложении
 */
open class BaseActivity : AppCompatActivity() {

    /**
     * Применяем сохраненную локаль к контексту перед созданием активности
     */
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(updateLocale(newBase))
    }
    
    private fun updateLocale(context: Context): Context {
        val locale = Locale("ru")
        Locale.setDefault(locale)
        
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        
        return context.createConfigurationContext(configuration)
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        // Применяем тему перед созданием активности
        ThemeManager.applyTheme(this)
        
        super.onCreate(savedInstanceState)
        
        // Принудительно устанавливаем русскую локаль при создании активности
        val localeManager = LocaleManager(this)
        val language = localeManager.getSavedLanguage()
        val locale = Locale(language)
        Locale.setDefault(locale)
        
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    override fun onResume() {
        super.onResume()
        // Применяем тему при возвращении в активность
        ThemeManager.applyTheme(this)
    }

    /**
     * Применяем локаль при изменении конфигурации
     */
    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        super.applyOverrideConfiguration(overrideConfiguration)
        val localeManager = LocaleManager(this)
        val language = localeManager.getSavedLanguage()
        val locale = Locale(language)
        Locale.setDefault(locale)
        overrideConfiguration?.setLocale(locale)
    }
}

