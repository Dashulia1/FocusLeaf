package com.dasasergeeva.focusleaf2

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.dasasergeeva.focusleaf2.databinding.ActivityNotificationSettingsBinding
import com.dasasergeeva.focusleaf2.utils.NotificationSettingsManager

class NotificationSettingsActivity : BaseActivity() {

    private lateinit var binding: ActivityNotificationSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setupWindow()
        
        binding = ActivityNotificationSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupListeners()
        loadSettings()
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

        // Автосохранение при изменении чекбоксов
        binding.cbSessionEnd.setOnCheckedChangeListener { _, _ -> saveSettings() }
        binding.cbBreakReminders.setOnCheckedChangeListener { _, _ -> saveSettings() }
        binding.cbAchievements.setOnCheckedChangeListener { _, _ -> saveSettings() }
        binding.cbFriendsOnline.setOnCheckedChangeListener { _, _ -> saveSettings() }
    }

    /**
     * Загрузка текущих настроек
     */
    private fun loadSettings() {
        val settings = NotificationSettingsManager.getSettings(this)
        
        binding.cbSessionEnd.isChecked = settings.sessionEndNotifications
        binding.cbBreakReminders.isChecked = settings.breakReminderNotifications
        binding.cbAchievements.isChecked = settings.achievementNotifications
        binding.cbFriendsOnline.isChecked = settings.friendsOnlineNotifications
    }

    /**
     * Сохранение настроек
     */
    private fun saveSettings() {
        val newSettings = NotificationSettingsManager.NotificationSettings(
            sessionEndNotifications = binding.cbSessionEnd.isChecked,
            breakReminderNotifications = binding.cbBreakReminders.isChecked,
            achievementNotifications = binding.cbAchievements.isChecked,
            friendsOnlineNotifications = binding.cbFriendsOnline.isChecked
        )
        
        NotificationSettingsManager.saveSettings(this, newSettings)
    }
}

