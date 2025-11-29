package com.dasasergeeva.focusleaf2

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.dasasergeeva.focusleaf2.databinding.ActivityTimerSettingsBinding
import com.dasasergeeva.focusleaf2.utils.TimerSettingsManager

class TimerSettingsActivity : BaseActivity() {

    private lateinit var binding: ActivityTimerSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setupWindow()
        
        binding = ActivityTimerSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupListeners()
        loadSettings()
        setupNotificationSettingsButton()
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

        // Сохранение настроек при изменении переключателей
        binding.switchWorkTime.setOnCheckedChangeListener { _, _ -> saveSettings() }
        binding.switchShortBreak.setOnCheckedChangeListener { _, _ -> saveSettings() }
        binding.switchLongBreak.setOnCheckedChangeListener { _, _ -> saveSettings() }
        binding.cbAutoStartSessions.setOnCheckedChangeListener { _, _ -> saveSettings() }
        binding.cbAutomaticNotification.setOnCheckedChangeListener { _, _ -> saveSettings() }
    }

    /**
     * Загрузка текущих настроек
     */
    private fun loadSettings() {
        val settings = TimerSettingsManager.getSettings(this)
        
        binding.switchWorkTime.isChecked = settings.workTimeEnabled
        binding.switchShortBreak.isChecked = settings.shortBreakEnabled
        binding.switchLongBreak.isChecked = settings.longBreakEnabled
        binding.tvSessionsCount.text = "(${settings.sessionsBeforeLongBreak})"
        binding.cbAutoStartSessions.isChecked = settings.autoStartSessions
        binding.cbAutomaticNotification.isChecked = settings.automaticNotification
    }

    /**
     * Сохранение настроек
     */
    private fun saveSettings() {
        val settings = TimerSettingsManager.TimerSettings(
            workTimeEnabled = binding.switchWorkTime.isChecked,
            shortBreakEnabled = binding.switchShortBreak.isChecked,
            longBreakEnabled = binding.switchLongBreak.isChecked,
            sessionsBeforeLongBreak = 4, // Значение по умолчанию
            autoStartSessions = binding.cbAutoStartSessions.isChecked,
            automaticNotification = binding.cbAutomaticNotification.isChecked
        )
        
        TimerSettingsManager.saveSettings(this, settings)
    }

    /**
     * Настройка кнопки перехода в настройки уведомлений
     */
    private fun setupNotificationSettingsButton() {
        binding.btnNotificationSettings.setOnClickListener {
            val intent = android.content.Intent(this, NotificationSettingsActivity::class.java)
            startActivity(intent)
        }
    }
}

