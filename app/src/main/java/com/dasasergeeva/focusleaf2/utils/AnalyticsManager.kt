package com.dasasergeeva.focusleaf2.utils

import com.dasasergeeva.focusleaf2.models.Project
import com.dasasergeeva.focusleaf2.repository.ProjectRepository
import java.util.Locale

/**
 * Менеджер для управления аналитикой
 */
object AnalyticsManager {

    /**
     * Получить аналитику за сегодня
     */
    fun getTodayAnalytics(): TodayAnalytics {
        val projects = ProjectRepository.getAllProjects()
        
        // Расчет статистики из проектов
        val totalSessions = calculateTotalSessions(projects)
        val focusTime = calculateFocusTime(projects)
        val averageConcentration = calculateAverageConcentration(projects)
        val hourlyData = generateHourlyData(projects)
        
        return TodayAnalytics(
            totalSessions = totalSessions,
            focusTime = focusTime,
            averageConcentration = averageConcentration,
            hourlyData = hourlyData
        )
    }
    
    /**
     * Расчет общего количества сессий
     * Количество сессий = сумма estimatedTomatoes всех проектов
     */
    private fun calculateTotalSessions(projects: List<Project>): Int {
        return projects.fold(0) { acc, project -> acc + project.estimatedTomatoes }
    }
    
    /**
     * Расчет фокус-времени
     * Фокус-время = сумма estimatedTomatoes × 25 минут
     */
    private fun calculateFocusTime(projects: List<Project>): Int {
        return projects.fold(0) { acc, project -> acc + project.estimatedTomatoes } * 25
    }
    
    /**
     * Расчет средней концентрации на основе приоритетов проектов
     */
    private fun calculateAverageConcentration(projects: List<Project>): Int {
        if (projects.isEmpty()) return 0
        
        // Средняя концентрация на основе приоритетов проектов
        val concentrationSum = projects.fold(0) { acc, project ->
            acc + when (project.priority) {
                "Высокий" -> 90
                "Средний" -> 75
                "Низкий" -> 60
                else -> 70
            }
        }
        
        return concentrationSum / projects.size
    }
    
    /**
     * Генерация данных для диаграммы по часам
     */
    private fun generateHourlyData(projects: List<Project>): List<Int> {
        // Базовые данные для диаграммы (примерные значения)
        val baseData = listOf(30, 75, 70, 85, 95, 75, 70, 85, 50)
        
        // Модифицируем базовые данные в зависимости от количества проектов
        return if (projects.isEmpty()) {
            baseData
        } else {
            val projectFactor = minOf(projects.size / 2.0, 2.0)
            baseData.map { value ->
                minOf(100, (value * projectFactor).toInt())
            }
        }
    }
    
    /**
     * Обновление статистики при завершении сессии
     */
    fun updateSessionCompletion(projectId: String, sessionsCompleted: Int) {
        val project = ProjectRepository.getProjectById(projectId)
        project?.let {
            val updatedProject = it.copy(
                completedSessions = sessionsCompleted,
                totalFocusTime = sessionsCompleted * 25
            )
            ProjectRepository.updateProject(updatedProject)
        }
    }
    
    /**
     * Получить статистику текущей недели
     */
    fun getCurrentWeekStats(): WeekStats {
        val projects = ProjectRepository.getAllProjects()
        val totalMinutes = projects.fold(0) { acc, project -> acc + project.estimatedTomatoes } * 25
        val totalHours = totalMinutes / 60
        
        return WeekStats(
            totalHours = totalHours,
            dailyData = getCurrentWeekData()
        )
    }
    
    /**
     * Получить статистику предыдущей недели
     */
    fun getPreviousWeekStats(): WeekStats {
        // Для демонстрации - немного меньше текущей недели
        val currentStats = getCurrentWeekStats()
        return WeekStats(
            totalHours = maxOf(0, currentStats.totalHours - 2),
            dailyData = getPreviousWeekData()
        )
    }
    
    /**
     * Получить данные текущей недели (часы продуктивности по дням)
     */
    fun getCurrentWeekData(): List<Int> {
        // Примерные данные для текущей недели (часы продуктивности по дням)
        return listOf(2, 4, 3, 5, 4, 2, 3) // Пн-Вс
    }
    
    /**
     * Получить данные предыдущей недели (часы продуктивности по дням)
     */
    fun getPreviousWeekData(): List<Int> {
        // Примерные данные для предыдущей недели
        return listOf(1, 3, 2, 4, 3, 1, 2) // Пн-Вс
    }
    
    /**
     * Модель данных аналитики за сегодня
     */
    data class TodayAnalytics(
        val totalSessions: Int,
        val focusTime: Int, // в минутах
        val averageConcentration: Int, // в процентах
        val hourlyData: List<Int> // данные по часам для диаграммы (проценты)
    )
    
    /**
     * Модель данных аналитики за неделю
     */
    data class WeekStats(
        val totalHours: Int,
        val dailyData: List<Int> // данные по дням недели (часы продуктивности)
    )
    
    /**
     * Получить данные аналитики проектов
     */
    fun getProjectAnalyticsData(): List<ProjectAnalytics> {
        val projects = ProjectRepository.getAllProjects()
        return projects.map { project ->
            val completed = project.completedSessions
            val estimated = project.estimatedTomatoes
            val progress = if (estimated > 0) {
                (completed.toFloat() / estimated) * 100f
            } else {
                0f
            }
            
            ProjectAnalytics(
                name = project.name,
                completed = completed,
                estimated = estimated,
                progress = progress
            )
        }
    }
    
    /**
     * Получить общее время всех проектов (в формате ЧЧ.ММ.СС)
     * Использует projectTotalTime (общее время проекта) или estimatedTime * taskCount
     */
    fun getTotalProjectsTime(): String {
        val projects = ProjectRepository.getAllProjects()
        // Используем projectTotalTime, если он есть, иначе estimatedTime * taskCount
        val totalMinutes = projects.fold(0) { acc, project ->
            acc + if (project.projectTotalTime > 0) {
                project.projectTotalTime
            } else {
                project.estimatedTime * project.taskCount
            }
        }
        
        val hours = totalMinutes / 60
        val minutes = (totalMinutes % 60)
        val seconds = 0 // Для упрощения используем 0 секунд
        
        return String.format(Locale.getDefault(), "%02d.%02d.%02d", hours, minutes, seconds)
    }
    
    /**
     * Получить данные для сравнительной диаграммы проектов
     * Возвращает 16 значений (4 группы по 4 столбца для q1, q2, q3, q4)
     */
    fun getProjectsChartData(): List<Int> {
        // Примерные данные для диаграммы (положительные и отрицательные значения)
        // 4 группы по 4 столбца = 16 значений
        val baseData = listOf(60, 20, -20, -60, 80, 100, 120, -20, -60, 40, 30, -10, 50, 70, 90, -30)
        return baseData.take(16) // Гарантируем ровно 16 значений
    }
    
    /**
     * Модель данных аналитики проекта
     */
    data class ProjectAnalytics(
        val name: String,
        val completed: Int,
        val estimated: Int,
        val progress: Float // в процентах
    )
}

