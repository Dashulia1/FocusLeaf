package com.dasasergeeva.focusleaf2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dasasergeeva.focusleaf2.databinding.ActivityStatisticsBinding
import com.dasasergeeva.focusleaf2.models.OverallStats
import com.dasasergeeva.focusleaf2.repository.ProjectRepository
import com.dasasergeeva.focusleaf2.utils.StatisticsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Активность для отображения статистики по всем проектам
 */
class StatisticsActivity : BaseActivity() {

    companion object {
        private const val TAG = "StatisticsActivity"
    }

    private lateinit var binding: ActivityStatisticsBinding
    private lateinit var statsReceiver: BroadcastReceiver
    private var statsJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Инициализация репозитория
        ProjectRepository.init(this)
        
        setupWindow()
        
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupListeners()
        setupTime()
        updateStatistics()
        setupStatsReceiver()
        observeStatistics()
    }
    
    /**
     * Наблюдение за изменениями статистики через StateFlow
     */
    private fun observeStatistics() {
        statsJob = lifecycleScope.launch {
            StatisticsManager.stats.collect { stats ->
                updateStatisticsUI(stats)
                Log.d(TAG, "Statistics updated via StateFlow: tasks=${stats.totalTasks}, time=${stats.totalTime}")
            }
        }
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
     * Настройка времени в статус баре
     */
    private fun setupTime() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = timeFormat.format(Date())
        binding.tvTime.text = currentTime
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
     * Настройка BroadcastReceiver для обновления статистики
     */
    private fun setupStatsReceiver() {
        statsReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d(TAG, "Broadcast received: action=${intent?.action}")
                // Всегда обновляем статистику при получении broadcast
                // Это гарантирует актуальность данных
                runOnUiThread {
                    updateStatistics()
                }
            }
        }

        try {
            // Регистрация receiver
            val filter = StatisticsManager.getStatsIntentFilter()
            LocalBroadcastManager.getInstance(this).registerReceiver(statsReceiver, filter)
            Log.d(TAG, "BroadcastReceiver registered successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error registering BroadcastReceiver", e)
        }
    }

    /**
     * Обновление статистики на основе данных проектов
     * Всегда пересчитывает статистику из актуальных данных репозитория
     */
    private fun updateStatistics() {
        try {
            // Перезагружаем проекты из репозитория для получения актуальных данных
            val projects = ProjectRepository.getAllProjects()
            val overallStats = StatisticsManager.calculateStats(projects)
            Log.d(TAG, "Statistics updated: ${projects.size} total projects, ${projects.count { !it.isCompleted }} active, tasks=${overallStats.totalTasks}, time=${overallStats.totalTime}")
            updateStatisticsUI(overallStats)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating statistics", e)
            // В случае ошибки показываем 0
            updateStatisticsUI(OverallStats(0, 0))
        }
    }

    /**
     * Обновление UI статистики
     */
    private fun updateStatisticsUI(stats: OverallStats) {
        try {
            // Обновление карточки "Количество задач"
            binding.tasksCount.text = stats.totalTasks.toString()

            // Обновление карточки "Общее время"
            binding.totalTime.text = getString(R.string.minutes_format, stats.totalTime)
            
            Log.d(TAG, "UI updated: tasks=${stats.totalTasks}, time=${stats.totalTime}")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating UI", e)
        }
    }

    override fun onStart() {
        super.onStart()
        // Обновляем статистику при старте активности
        Log.d(TAG, "onStart: updating statistics")
        updateStatistics()
    }

    override fun onResume() {
        super.onResume()
        // Обновляем статистику при возврате на экран (ВАЖНО: всегда обновляем)
        Log.d(TAG, "onResume: updating statistics")
        updateStatistics()
    }
    
    override fun onRestart() {
        super.onRestart()
        // Обновляем статистику при перезапуске активности
        Log.d(TAG, "onRestart: updating statistics")
        updateStatistics()
    }
    
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // Обновляем статистику когда окно получает фокус
        if (hasFocus) {
            Log.d(TAG, "onWindowFocusChanged: updating statistics")
            updateStatistics()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Отмена подписки на StateFlow
        statsJob?.cancel()
        
        // Отмена регистрации receiver
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(statsReceiver)
        } catch (e: Exception) {
            // Игнорируем ошибки отмены регистрации
            e.printStackTrace()
        }
    }
}

