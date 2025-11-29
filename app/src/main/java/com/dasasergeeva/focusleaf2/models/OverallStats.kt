package com.dasasergeeva.focusleaf2.models

/**
 * Модель данных общей статистики по всем проектам
 */
data class OverallStats(
    val totalTasks: Int, // общее количество задач из ВСЕХ проектов
    val totalTime: Int // общее время из ВСЕХ проектов (в минутах)
)


