package com.dasasergeeva.focusleaf2.utils

object UserManager {

    /**
     * Получение текущего пользователя
     */
    fun getCurrentUser(): User {
        // TODO: Загружать из SharedPreferences или базы данных
        return User(
            name = "Дашуля",
            email = "daria.degtyarevaa@mail.ru",
            gender = "female"
        )
    }

    /**
     * Получение статистики пользователя
     */
    fun getUserStatistics(): UserStatistics {
        // TODO: Вычислять из данных сессий и проектов
        return UserStatistics(
            streakDays = 7,
            totalSessions = 42
        )
    }

    /**
     * Модель пользователя
     */
    data class User(
        val name: String,
        val email: String,
        val gender: String // "female", "male"
    )

    /**
     * Статистика пользователя
     */
    data class UserStatistics(
        val streakDays: Int,
        val totalSessions: Int
    )
}


