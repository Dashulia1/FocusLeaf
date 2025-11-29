package com.dasasergeeva.focusleaf2

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewpager2.widget.ViewPager2
import com.dasasergeeva.focusleaf2.analytics.AnalyticsPagerAdapter
import com.dasasergeeva.focusleaf2.databinding.ActivityMainBinding
import com.dasasergeeva.focusleaf2.utils.PreferencesManager
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    
    // Таймер Pomodoro
    private var timer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 25 * 60 * 1000 // 25 минут (25 * 60 * 1000 мс)
    private val totalTimeInMillis: Long = 25 * 60 * 1000
    private var isTimerRunning = false
    
    // Статистика
    private var completedSessions = 0
    
    // Состояние таймера
    private var currentTimerType = TimerType.WORK // Work или Meditation

    enum class TimerType {
        WORK,
        MEDITATION
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Проверяем, авторизован ли пользователь
        if (!PreferencesManager.isUserAuthorized(this)) {
            // Если пользователь не авторизован, проверяем онбординг
            if (PreferencesManager.isOnboardingCompleted(this)) {
                // Онбординг завершен - переходим на LoginActivity
                navigateToLogin()
            } else {
                // Онбординг не завершен - переходим на OnboardingActivity
                navigateToOnboarding()
            }
            return
        }
        
        setupWindow()
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupTimer()
        setupButtons()
        setupIndicators()
        setupAnalytics()
        setupFabMenu()
        updateStatistics()
        updateTimerText()
    }

    /**
     * Настройка окна: светлый статус бар
     */
    private fun setupWindow() {
        window?.let { window ->
            WindowInsetsControllerCompat(window, window.decorView).apply {
                isAppearanceLightStatusBars = true
            }
            window.statusBarColor = ContextCompat.getColor(this, R.color.main_background)
        }
    }

    /**
     * Настройка кастомного статус бара
     */
    private fun setupStatusBar() {
        // Обновление времени в статус баре
        updateStatusBarTime()
        
        // Кнопка назад
        binding.ivBack.setOnClickListener {
            finish()
        }
        
        // Кнопка дома - переход на статистику
        binding.ivHome.setOnClickListener {
            navigateToStatistics()
        }
    }

    /**
     * Обновление времени в статус баре
     */
    private fun updateStatusBarTime() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = timeFormat.format(Date())
        binding.tvTime.text = currentTime
    }

    /**
     * Настройка таймера
     */
    private fun setupTimer() {
        updateTimerText()
        updateTimerProgress()
        
        // Метка таймера
        binding.tvTimerLabel.text = if (currentTimerType == TimerType.WORK) {
            getString(R.string.timer_work)
        } else {
            getString(R.string.timer_meditation)
        }
    }

    /**
     * Настройка кнопок управления таймером
     */
    private fun setupButtons() {
        // Кнопка Pause/Resume
        binding.btnPause.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        // Кнопка Skip
        binding.btnSkip.setOnClickListener {
            skipSession()
        }

        // Кнопка Quit
        binding.btnQuit.setOnClickListener {
            showQuitDialog()
        }

        // Иконка редактирования - переход на экран проектов
        binding.ivEdit.setOnClickListener {
            navigateToProjects()
        }
    }

    /**
     * Запуск таймера
     */
    private fun startTimer() {
        if (timer != null) {
            timer?.cancel()
        }

        timer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerText()
                updateTimerProgress()
            }

            override fun onFinish() {
                // Уведомление о завершении сессии
                completeSession()
            }
        }.start()

        isTimerRunning = true
        updatePauseButton()
    }

    /**
     * Пауза таймера
     */
    private fun pauseTimer() {
        timer?.cancel()
        isTimerRunning = false
        updatePauseButton()
    }

    /**
     * Пропуск сессии
     */
    private fun skipSession() {
        timer?.cancel()
        completeSession()
    }

    /**
     * Завершение сессии
     */
    private fun completeSession() {
        timer?.cancel()
        isTimerRunning = false
        
        // Увеличиваем количество завершенных сессий
        completedSessions++
        updateStatistics()
        
        // Сброс таймера
        resetTimer()
        
        // Показываем уведомление
        Toast.makeText(this, getString(R.string.session_completed), Toast.LENGTH_SHORT).show()
        
        updatePauseButton()
    }

    /**
     * Сброс таймера
     */
    private fun resetTimer() {
        timeLeftInMillis = totalTimeInMillis
        updateTimerText()
        updateTimerProgress()
    }

    /**
     * Обновление текста таймера
     */
    private fun updateTimerText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        val timeFormatted = String.format(Locale.getDefault(), getString(R.string.timer_format), minutes, seconds)
        binding.tvTimer.text = timeFormatted
    }

    /**
     * Обновление прогресса таймера
     */
    private fun updateTimerProgress() {
        val progress = ((totalTimeInMillis - timeLeftInMillis).toFloat() / totalTimeInMillis.toFloat()) * 100f
        binding.circleTimerView.progress = progress
    }

    /**
     * Обновление кнопки Pause/Resume
     */
    private fun updatePauseButton() {
        if (isTimerRunning) {
            binding.btnPause.text = getString(R.string.button_pause)
            binding.btnPause.setIconResource(android.R.drawable.ic_media_pause)
        } else {
            binding.btnPause.text = "Resume"
            binding.btnPause.setIconResource(android.R.drawable.ic_media_play)
        }
    }

    /**
     * Показать диалог подтверждения выхода
     */
    private fun showQuitDialog() {
        AlertDialog.Builder(this)
            .setTitle("Выход из сессии")
            .setMessage("Вы уверены, что хотите выйти? Прогресс будет потерян.")
            .setPositiveButton("Выйти") { _, _ ->
                quitSession()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    /**
     * Выход из сессии
     */
    private fun quitSession() {
        timer?.cancel()
        isTimerRunning = false
        resetTimer()
        updatePauseButton()
        Toast.makeText(this, "Сессия отменена", Toast.LENGTH_SHORT).show()
    }

    /**
     * Настройка индикаторов прогресса сессий (4 круга)
     */
    private fun setupIndicators() {
        binding.llSessionIndicators.removeAllViews()
        
        val dotSizePx = (12 * resources.displayMetrics.density).toInt()
        val marginPx = (8 * resources.displayMetrics.density).toInt()
        
        for (i in 0 until 4) {
            val dotView = View(this)
            val params = android.view.ViewGroup.MarginLayoutParams(dotSizePx, dotSizePx)
            params.setMargins(marginPx, 0, marginPx, 0)
            dotView.layoutParams = params
            
            val drawable = android.graphics.drawable.GradientDrawable()
            drawable.shape = android.graphics.drawable.GradientDrawable.OVAL
            drawable.setSize(dotSizePx, dotSizePx)
            
            // Заполненные круги для завершенных сессий
            if (i < completedSessions) {
                drawable.setColor(ContextCompat.getColor(this, R.color.main_progress_done))
                dotView.alpha = 1.0f
            } else {
                drawable.setColor(ContextCompat.getColor(this, R.color.main_progress_not_done))
                dotView.alpha = 0.5f
            }

            dotView.background = drawable
            binding.llSessionIndicators.addView(dotView)
        }
    }

    /**
     * Обновление статистики
     */
    private fun updateStatistics() {
        binding.tvCompletedTasks.text = getString(R.string.completed_tasks, completedSessions)
        setupIndicators()
    }

    /**
     * Настройка аналитики с TabLayout и ViewPager2
     */
    private fun setupAnalytics() {
        val adapter = AnalyticsPagerAdapter(this)
        binding.viewPagerAnalytics.adapter = adapter
        
        TabLayoutMediator(binding.tabLayoutAnalytics, binding.viewPagerAnalytics) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.analytics_today)
                1 -> getString(R.string.analytics_week)
                else -> ""
            }
        }.attach()
        
        // Обработчик клика на вкладки аналитики
        binding.tabLayoutAnalytics.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        // При клике на вкладку "Аналитика за СЕГОДНЯ" открываем экран аналитики
                        val intent = android.content.Intent(this@MainActivity, TodayAnalyticsActivity::class.java)
                        startActivity(intent)
                    }
                    1 -> {
                        // При клике на вкладку "Аналитика за НЕДЕЛЮ" открываем экран недельной аналитики
                        val intent = android.content.Intent(this@MainActivity, WeekAnalyticsActivity::class.java)
                        startActivity(intent)
                    }
                }
            }

            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                // Не требуется
            }

            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        // При повторном клике на вкладку "Аналитика за СЕГОДНЯ" открываем экран аналитики
                        val intent = android.content.Intent(this@MainActivity, TodayAnalyticsActivity::class.java)
                        startActivity(intent)
                    }
                    1 -> {
                        // При повторном клике на вкладку "Аналитика за НЕДЕЛЮ" открываем экран недельной аналитики
                        val intent = android.content.Intent(this@MainActivity, WeekAnalyticsActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

    override fun onPause() {
        super.onPause()
        // Сохраняем состояние таймера при паузе активности
        if (isTimerRunning) {
            pauseTimer()
        }
    }

    /**
     * Переход на экран входа (LoginActivity)
     */
    private fun navigateToLogin() {
        val intent = android.content.Intent(this, com.dasasergeeva.focusleaf2.LoginActivity::class.java)
        intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Переход на экран онбординга (OnboardingActivity)
     */
    private fun navigateToOnboarding() {
        val intent = android.content.Intent(this, com.dasasergeeva.focusleaf2.OnboardingActivity::class.java)
        intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Переход на экран проектов (ProjectsActivity)
     */
    private fun navigateToProjects() {
        val intent = android.content.Intent(this, ProjectsActivity::class.java)
        startActivity(intent)
    }

    /**
     * Переход на экран статистики (StatisticsActivity)
     */
    private fun navigateToStatistics() {
        val intent = android.content.Intent(this, StatisticsActivity::class.java)
        startActivity(intent)
    }

    // Состояние раскрывающегося меню FAB
    private var isFabMenuExpanded = false

    /**
     * Настройка раскрывающегося меню FAB кнопок
     */
    private fun setupFabMenu() {
        // Главная FAB кнопка - переключает меню
        binding.fabMainMenu.setOnClickListener {
            toggleFabMenu()
        }

        // FAB кнопка настроек
        binding.fabSettings.setOnClickListener {
            closeFabMenu()
            val intent = android.content.Intent(this, TimerSettingsActivity::class.java)
            startActivity(intent)
        }

        // FAB кнопка профиля
        binding.fabProfile.setOnClickListener {
            closeFabMenu()
            val intent = android.content.Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // FAB кнопка поиска друзей
        binding.fabFriendsSearch.setOnClickListener {
            closeFabMenu()
            val intent = android.content.Intent(this, FriendsSearchActivity::class.java)
            startActivity(intent)
        }

        // FAB кнопка аналитики проектов
        binding.fabProjectAnalytics.setOnClickListener {
            closeFabMenu()
            val intent = android.content.Intent(this, ProjectAnalyticsActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Переключение состояния раскрывающегося меню
     */
    private fun toggleFabMenu() {
        if (isFabMenuExpanded) {
            closeFabMenu()
        } else {
            expandFabMenu()
        }
    }

    /**
     * Раскрытие меню FAB кнопок
     */
    private fun expandFabMenu() {
        isFabMenuExpanded = true

        // Меняем иконку главной кнопки на крестик с анимацией
        binding.fabMainMenu.animate()
            .rotation(180f)
            .setDuration(200)
            .withEndAction {
                binding.fabMainMenu.setImageResource(R.drawable.ic_close)
                binding.fabMainMenu.rotation = 0f
            }
            .start()

        // Показываем все FAB кнопки с анимацией (сверху вниз: настройки, профиль, друзья, проекты)
        animateFab(binding.fabSettings, true, 0)
        animateFab(binding.fabProfile, true, 1)
        animateFab(binding.fabFriendsSearch, true, 2)
        animateFab(binding.fabProjectAnalytics, true, 3)
    }

    /**
     * Закрытие меню FAB кнопок
     */
    private fun closeFabMenu() {
        isFabMenuExpanded = false

        // Скрываем все FAB кнопки с анимацией (снизу вверх: проекты, друзья, профиль, настройки)
        animateFab(binding.fabProjectAnalytics, false, 0)
        animateFab(binding.fabFriendsSearch, false, 1)
        animateFab(binding.fabProfile, false, 2)
        animateFab(binding.fabSettings, false, 3)

        // Возвращаем иконку главной кнопки с анимацией
        binding.fabMainMenu.animate()
            .rotation(180f)
            .setDuration(200)
            .setStartDelay(300)
            .withEndAction {
                binding.fabMainMenu.setImageResource(R.drawable.ic_settings)
                binding.fabMainMenu.rotation = 0f
            }
            .start()
    }

    /**
     * Анимация FAB кнопки
     */
    private fun animateFab(
        fab: com.google.android.material.floatingactionbutton.FloatingActionButton,
        show: Boolean,
        delay: Int
    ) {
        fab.visibility = android.view.View.VISIBLE

        val animDuration = 200L
        val startDelay = delay * 50L

        if (show) {
            // Показываем с масштабированием и прозрачностью
            fab.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setStartDelay(startDelay)
                .setDuration(animDuration)
                .setListener(null)
        } else {
            // Скрываем с масштабированием и прозрачностью
            fab.animate()
                .scaleX(0f)
                .scaleY(0f)
                .alpha(0f)
                .setStartDelay(startDelay)
                .setDuration(animDuration)
                .setListener(object : android.animation.AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: android.animation.Animator) {
                        fab.visibility = android.view.View.GONE
                    }
                })
        }
    }
}
