package com.dasasergeeva.focusleaf2.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Менеджер для работы с SharedPreferences
 * Управляет состоянием онбординга и авторизации пользователя
 */
object PreferencesManager {

    private const val PREFS_NAME = "focusleaf_prefs"
    
    // Ключи для SharedPreferences
    private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    private const val KEY_USER_AUTHORIZED = "user_authorized"
    private const val KEY_USER_EMAIL = "user_email"

    /**
     * Получить SharedPreferences
     */
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Проверить, был ли завершен онбординг
     */
    fun isOnboardingCompleted(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    /**
     * Установить, что онбординг завершен
     */
    fun setOnboardingCompleted(context: Context, completed: Boolean = true) {
        getPrefs(context).edit()
            .putBoolean(KEY_ONBOARDING_COMPLETED, completed)
            .apply()
    }

    /**
     * Проверить, авторизован ли пользователь
     */
    fun isUserAuthorized(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_USER_AUTHORIZED, false)
    }

    /**
     * Установить, что пользователь авторизован
     */
    fun setUserAuthorized(context: Context, authorized: Boolean = true, email: String? = null) {
        getPrefs(context).edit().apply {
            putBoolean(KEY_USER_AUTHORIZED, authorized)
            if (email != null) {
                putString(KEY_USER_EMAIL, email)
            }
            if (!authorized) {
                remove(KEY_USER_EMAIL)
            }
            apply()
        }
    }

    /**
     * Получить email авторизованного пользователя
     */
    fun getUserEmail(context: Context): String? {
        return getPrefs(context).getString(KEY_USER_EMAIL, null)
    }

    /**
     * Выйти из аккаунта (сброс авторизации)
     */
    fun logout(context: Context) {
        getPrefs(context).edit()
            .putBoolean(KEY_USER_AUTHORIZED, false)
            .remove(KEY_USER_EMAIL)
            .apply()
    }

    /**
     * Полная очистка всех данных (для тестирования)
     */
    fun clearAll(context: Context) {
        getPrefs(context).edit().clear().apply()
    }

    /**
     * Сброс онбординга (для тестирования)
     */
    fun resetOnboarding(context: Context) {
        getPrefs(context).edit()
            .putBoolean(KEY_ONBOARDING_COMPLETED, false)
            .apply()
    }
}


