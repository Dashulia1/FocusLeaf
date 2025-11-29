package com.dasasergeeva.focusleaf2

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.dasasergeeva.focusleaf2.databinding.ActivityThemeSettingsBinding
import com.dasasergeeva.focusleaf2.utils.ThemeManager

class ThemeSettingsActivity : BaseActivity() {

    private lateinit var binding: ActivityThemeSettingsBinding
    private var currentTheme: ThemeManager.AppTheme = ThemeManager.AppTheme.SYSTEM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setupWindow()
        
        binding = ActivityThemeSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupListeners()
        loadCurrentTheme()
        setupThemeOptions()
    }

    /**
     * Настройка окна: светлый статус бар
     */
    private fun setupWindow() {
        window?.let { window ->
            WindowInsetsControllerCompat(window, window.decorView).apply {
                isAppearanceLightStatusBars = true
            }
        }
    }

    /**
     * Настройка статус бара
     */
    private fun setupStatusBar() {
        window?.statusBarColor = ContextCompat.getColor(this, R.color.pink_gradient_start)
    }

    /**
     * Настройка слушателей
     */
    private fun setupListeners() {
        // Кнопка назад
        binding.ibBack.setOnClickListener {
            finish()
        }

        // Кнопка домой (переход на главный экран)
        binding.ibHome.setOnClickListener {
            val intent = android.content.Intent(this, MainActivity::class.java)
            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    /**
     * Загрузка текущей темы
     */
    private fun loadCurrentTheme() {
        currentTheme = ThemeManager.getCurrentTheme(this)
        updateRadioButtons()
    }

    /**
     * Настройка опций выбора темы
     */
    private fun setupThemeOptions() {
        // Системная тема
        binding.optionSystemTheme.setOnClickListener {
            setTheme(ThemeManager.AppTheme.SYSTEM)
        }

        // Светлая тема
        binding.optionLightTheme.setOnClickListener {
            setTheme(ThemeManager.AppTheme.LIGHT)
        }

        // Тёмная тема
        binding.optionDarkTheme.setOnClickListener {
            setTheme(ThemeManager.AppTheme.DARK)
        }
    }

    /**
     * Установка темы
     */
    private fun setTheme(theme: ThemeManager.AppTheme) {
        currentTheme = theme
        updateRadioButtons()
        ThemeManager.setTheme(this, theme)

        // Перезапустить активность для применения темы
        recreate()
    }

    /**
     * Обновление состояния RadioButton'ов
     */
    private fun updateRadioButtons() {
        binding.radioSystemTheme.isChecked = currentTheme == ThemeManager.AppTheme.SYSTEM
        binding.radioLightTheme.isChecked = currentTheme == ThemeManager.AppTheme.LIGHT
        binding.radioDarkTheme.isChecked = currentTheme == ThemeManager.AppTheme.DARK
    }
}

