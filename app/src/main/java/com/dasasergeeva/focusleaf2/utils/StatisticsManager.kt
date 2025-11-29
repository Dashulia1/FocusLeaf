package com.dasasergeeva.focusleaf2.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dasasergeeva.focusleaf2.models.OverallStats
import com.dasasergeeva.focusleaf2.repository.ProjectRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Менеджер для управления статистикой проектов с реактивными обновлениями
 */
object StatisticsManager {

    private const val TAG = "StatisticsManager"
    private const val ACTION_STATS_UPDATED = "com.dasasergeeva.focusleaf2.STATS_UPDATED"
    private const val EXTRA_TOTAL_TASKS = "total_tasks"
    private const val EXTRA_TOTAL_TIME = "total_time"
    
    // Реактивный поток для статистики
    private val _stats = MutableStateFlow(OverallStats(0, 0))
    val stats: StateFlow<OverallStats> = _stats.asStateFlow()
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    init {
        // Подписка на изменения списка проектов для автоматического пересчета статистики
        scope.launch {
            ProjectRepository.projects.collect { projects ->
                recalculateStats(null, projects)
            }
        }
    }

    /**
     * Пересчет и обновление статистики
     * @param context Контекст для отправки broadcast (может быть null)
     */
    fun recalculateStats(context: Context? = null, projects: List<com.dasasergeeva.focusleaf2.models.Project>? = null) {
        try {
            val projectsList = projects ?: ProjectRepository.getAllProjects()
            val calculatedStats = calculateStats(projectsList)
            
            // Обновляем реактивный поток статистики
            _stats.value = calculatedStats
            
            Log.d(TAG, "Recalculating stats: tasks=${calculatedStats.totalTasks}, time=${calculatedStats.totalTime}")
            
            // Отправка broadcast для обновления других экранов (если контекст предоставлен)
            if (context != null) {
                sendStatsBroadcast(context, calculatedStats)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error recalculating stats", e)
        }
    }

    /**
     * Расчет статистики на основе списка проектов
     * Учитываются только активные (НЕ выполненные) проекты
     */
    fun calculateStats(projects: List<com.dasasergeeva.focusleaf2.models.Project>): OverallStats {
        // Фильтруем только активные (не выполненные) проекты
        val activeProjects = projects.filter { !it.isCompleted }
        val totalTasks = activeProjects.sumOf { it.taskCount }
        val totalTime = activeProjects.sumOf { it.projectTotalTime }
        Log.d(TAG, "Calculated stats: ${projects.size} total projects, ${activeProjects.size} active, $totalTasks tasks, $totalTime minutes")
        return OverallStats(totalTasks, totalTime)
    }

    /**
     * Обновление статуса проекта и пересчет статистики
     */
    fun updateProjectCompletion(context: Context, projectId: String, isCompleted: Boolean) {
        try {
            Log.d(TAG, "Updating project completion: id=$projectId, completed=$isCompleted")
            
            // Обновление в репозитории
            ProjectRepository.updateProjectCompletion(projectId, isCompleted)
            
            // Немедленный пересчет статистики после сохранения
            // Используем небольшую задержку для гарантии, что данные сохранились
            Handler(Looper.getMainLooper()).postDelayed({
                recalculateStats(context)
                Log.d(TAG, "Statistics recalculated after project update")
            }, 300) // Увеличена задержка до 300мс для гарантии сохранения
            
            // Дополнительно отправляем broadcast сразу после небольшой задержки
            Handler(Looper.getMainLooper()).postDelayed({
                recalculateStats(context)
                Log.d(TAG, "Statistics recalculated again for reliability")
            }, 500)
            
            Log.d(TAG, "Project completion updated successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating project completion", e)
        }
    }

    /**
     * Отправка broadcast о обновлении статистики
     */
    private fun sendStatsBroadcast(context: Context, stats: OverallStats) {
        try {
            val intent = Intent(ACTION_STATS_UPDATED).apply {
                putExtra(EXTRA_TOTAL_TASKS, stats.totalTasks)
                putExtra(EXTRA_TOTAL_TIME, stats.totalTime)
            }
            val sent = LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            Log.d(TAG, "Broadcast sent: success=$sent, tasks=${stats.totalTasks}, time=${stats.totalTime}")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending broadcast", e)
        }
    }

    /**
     * Создание IntentFilter для подписки на обновления статистики
     */
    fun getStatsIntentFilter(): IntentFilter {
        return IntentFilter(ACTION_STATS_UPDATED)
    }

    /**
     * Получение статистики из Intent
     */
    fun getStatsFromIntent(intent: Intent): OverallStats? {
        if (intent.action == ACTION_STATS_UPDATED) {
            val totalTasks = intent.getIntExtra(EXTRA_TOTAL_TASKS, 0)
            val totalTime = intent.getIntExtra(EXTRA_TOTAL_TIME, 0)
            return OverallStats(totalTasks, totalTime)
        }
        return null
    }
}

