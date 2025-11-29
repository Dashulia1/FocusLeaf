package com.dasasergeeva.focusleaf2.models

/**
 * Модель данных проекта
 */
data class Project(
    val id: String,
    val name: String,
    val color: Int, // цвет в формате HEX (Color.parseColor)
    val priority: String, // "Низкий", "Средний", "Высокий"
    val estimatedTime: Int, // время в минутах для одной задачи
    val taskCount: Int, // количество задач в ЭТОМ проекте
    val projectTotalTime: Int, // общее время для ЭТОГО проекта (estimatedTime * taskCount)
    val createdAt: Long = System.currentTimeMillis(),
    var isCompleted: Boolean = false, // статус выполнения проекта
    val estimatedTomatoes: Int = 0, // количество помидоров (сессий) для проекта
    var completedSessions: Int = 0, // количество завершенных сессий
    var totalFocusTime: Int = 0 // общее время фокуса в минутах
)

