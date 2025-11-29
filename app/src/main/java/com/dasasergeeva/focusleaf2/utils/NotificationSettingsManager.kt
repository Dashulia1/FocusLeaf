package com.dasasergeeva.focusleaf2.utils

import android.content.Context
import android.content.SharedPreferences

object NotificationSettingsManager {
    
    private const val PREFS_NAME = "notification_settings"
    private const val KEY_SESSION_END = "session_end"
    private const val KEY_BREAK_REMINDERS = "break_reminders"
    private const val KEY_ACHIEVEMENTS = "achievements"
    private const val KEY_FRIENDS_ONLINE = "friends_online"

    /**
     * Модель настроек уведомлений
     */
    data class NotificationSettings(
        val sessionEndNotifications: Boolean = false,
        val breakReminderNotifications: Boolean = true,
        val achievementNotifications: Boolean = true,
        val friendsOnlineNotifications: Boolean = false
    )

    /**
     * Получение настроек уведомлений
     */
    fun getSettings(context: Context): NotificationSettings {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        return NotificationSettings(
            sessionEndNotifications = prefs.getBoolean(KEY_SESSION_END, false),
            breakReminderNotifications = prefs.getBoolean(KEY_BREAK_REMINDERS, true),
            achievementNotifications = prefs.getBoolean(KEY_ACHIEVEMENTS, true),
            friendsOnlineNotifications = prefs.getBoolean(KEY_FRIENDS_ONLINE, false)
        )
    }

    /**
     * Сохранение настроек уведомлений
     */
    fun saveSettings(context: Context, settings: NotificationSettings) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean(KEY_SESSION_END, settings.sessionEndNotifications)
            putBoolean(KEY_BREAK_REMINDERS, settings.breakReminderNotifications)
            putBoolean(KEY_ACHIEVEMENTS, settings.achievementNotifications)
            putBoolean(KEY_FRIENDS_ONLINE, settings.friendsOnlineNotifications)
            apply()
        }
    }
}


