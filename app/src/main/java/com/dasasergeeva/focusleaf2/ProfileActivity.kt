package com.dasasergeeva.focusleaf2

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.dasasergeeva.focusleaf2.databinding.ActivityProfileBinding
import com.dasasergeeva.focusleaf2.utils.PreferencesManager
import com.dasasergeeva.focusleaf2.utils.UserManager

class ProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setupWindow()
        
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupListeners()
        loadUserData()
        setupStatistics()
        setupThemeSettingsButton()
        setupLogoutButton()
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

        // Кнопка настроек
        binding.ibSettings.setOnClickListener {
            // TODO: Открыть экран настроек
            // val intent = Intent(this, SettingsActivity::class.java)
            // startActivity(intent)
        }
    }

    /**
     * Загрузка данных пользователя
     */
    private fun loadUserData() {
        val user = UserManager.getCurrentUser()
        
        binding.tvUserName.text = user.name
        binding.tvUserEmail.text = user.email
        
        // Установка аватара в зависимости от пола
        val avatarRes = when (user.gender) {
            "female" -> R.drawable.ic_female_avatar
            "male" -> R.drawable.ic_male_avatar
            else -> R.drawable.ic_default_avatar
        }
        binding.ivUserAvatar.setImageResource(avatarRes)
        
        // Показываем индикатор онлайн
        binding.vOnlineStatus.visibility = View.VISIBLE
    }

    /**
     * Настройка статистики
     */
    private fun setupStatistics() {
        val stats = UserManager.getUserStatistics()
        
        binding.tvStreakDays.text = stats.streakDays.toString()
        binding.tvTotalSessions.text = stats.totalSessions.toString()
    }

    /**
     * Настройка кнопки перехода в настройки темы
     */
    private fun setupThemeSettingsButton() {
        // Обновляем отображение текущей темы
        binding.tvCurrentTheme.text = com.dasasergeeva.focusleaf2.utils.ThemeManager.getThemeName(this)
        
        // Настройка перехода к настройкам темы
        binding.btnThemeSettings.setOnClickListener {
            val intent = android.content.Intent(this, ThemeSettingsActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Настройка кнопки выхода
     */
    private fun setupLogoutButton() {
        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
        
        // Длинное нажатие для сброса онбординга (для разработки)
        binding.btnLogout.setOnLongClickListener {
            showResetOnboardingDialog()
            true
        }
    }
    
    /**
     * Показать диалог сброса онбординга
     */
    private fun showResetOnboardingDialog() {
        AlertDialog.Builder(this)
            .setTitle("Сброс онбординга")
            .setMessage("Сбросить онбординг? При следующем запуске приложения вы увидите экраны знакомства.")
            .setPositiveButton("Сбросить") { _, _ ->
                PreferencesManager.resetOnboarding(this)
                Toast.makeText(this, "Онбординг сброшен. Перезапустите приложение.", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    /**
     * Показать диалог подтверждения выхода
     */
    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.logout_confirmation))
            .setPositiveButton(getString(R.string.logout)) { _, _ ->
                performLogout()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    /**
     * Выполнить выход из аккаунта
     */
    private fun performLogout() {
        // Очищаем авторизацию
        PreferencesManager.logout(this)
        
        // Показываем сообщение
        Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
        
        // Переходим на экран входа
        val intent = android.content.Intent(this, LoginActivity::class.java)
        intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        // Обновляем отображение текущей темы при возвращении на экран
        if (::binding.isInitialized) {
            binding.tvCurrentTheme.text = com.dasasergeeva.focusleaf2.utils.ThemeManager.getThemeName(this)
        }
    }
}

