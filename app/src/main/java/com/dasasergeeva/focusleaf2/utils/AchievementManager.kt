package com.dasasergeeva.focusleaf2.utils

import com.dasasergeeva.focusleaf2.R

object AchievementManager {

    /**
     * Получение последних достижений
     */
    fun getRecentAchievements(limit: Int = 3): List<Achievement> {
        return listOf(
            Achievement(
                id = 1,
                title = "100 часов в фокусе",
                description = "Проведено 100 часов в режиме фокусировки",
                iconRes = R.drawable.ic_achievement_hours,
                unlockedAt = System.currentTimeMillis() - 86400000, // 1 день назад
                isUnlocked = true
            ),
            Achievement(
                id = 2,
                title = "Максимальное количество выполненных задач",
                description = "Выполнено рекордное количество задач за день",
                iconRes = R.drawable.ic_achievement_tasks,
                unlockedAt = System.currentTimeMillis() - 172800000, // 2 дня назад
                isUnlocked = true
            ),
            Achievement(
                id = 3,
                title = "7 дней подряд",
                description = "Непрерывная серия дней с выполнением задач",
                iconRes = R.drawable.ic_achievement_streak,
                unlockedAt = System.currentTimeMillis() - 259200000, // 3 дня назад
                isUnlocked = true
            )
        ).take(limit)
    }

    /**
     * Получение всех достижений
     */
    fun getAllAchievements(): List<Achievement> {
        return listOf(
            Achievement(
                id = 1,
                title = "100 часов в фокусе",
                description = "Проведено 100 часов в режиме фокусировки",
                iconRes = R.drawable.ic_achievement_hours,
                progress = 100,
                target = 100,
                isUnlocked = true
            ),
            Achievement(
                id = 2,
                title = "Максимальное количество выполненных задач",
                description = "Выполнено рекордное количество задач за день",
                iconRes = R.drawable.ic_achievement_tasks,
                progress = 15,
                target = 10,
                isUnlocked = true
            ),
            Achievement(
                id = 3,
                title = "7 дней подряд",
                description = "Непрерывная серия дней с выполнением задач",
                iconRes = R.drawable.ic_achievement_streak,
                progress = 7,
                target = 7,
                isUnlocked = true
            )
        )
    }

    /**
     * Модель достижения
     */
    data class Achievement(
        val id: Int,
        val title: String,
        val description: String,
        val iconRes: Int,
        val progress: Int = 0,
        val target: Int = 0,
        val unlockedAt: Long = 0,
        val isUnlocked: Boolean = false
    )
}

