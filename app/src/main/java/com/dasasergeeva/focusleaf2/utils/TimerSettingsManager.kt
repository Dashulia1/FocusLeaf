package com.dasasergeeva.focusleaf2.utils

import android.content.Context
import android.content.SharedPreferences

object TimerSettingsManager {
    
    private const val PREFS_NAME = "timer_settings"
    private const val KEY_WORK_TIME_ENABLED = "work_time_enabled"
    private const val KEY_SHORT_BREAK_ENABLED = "short_break_enabled"
    private const val KEY_LONG_BREAK_ENABLED = "long_break_enabled"
    private const val KEY_SESSIONS_BEFORE_LONG_BREAK = "sessions_before_long_break"
    private const val KEY_AUTO_START_SESSIONS = "auto_start_sessions"
    private const val KEY_AUTOMATIC_NOTIFICATION = "automatic_notification"

    /**
     * Модель настроек таймера
     */
    data class TimerSettings(
        val workTimeEnabled: Boolean = true,
        val shortBreakEnabled: Boolean = false,
        val longBreakEnabled: Boolean = true,
        val sessionsBeforeLongBreak: Int = 4,
        val autoStartSessions: Boolean = false,
        val automaticNotification: Boolean = true
    )

    /**
     * Получение настроек таймера
     */
    fun getSettings(context: Context): TimerSettings {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        return TimerSettings(
            workTimeEnabled = prefs.getBoolean(KEY_WORK_TIME_ENABLED, true),
            shortBreakEnabled = prefs.getBoolean(KEY_SHORT_BREAK_ENABLED, false),
            longBreakEnabled = prefs.getBoolean(KEY_LONG_BREAK_ENABLED, true),
            sessionsBeforeLongBreak = prefs.getInt(KEY_SESSIONS_BEFORE_LONG_BREAK, 4),
            autoStartSessions = prefs.getBoolean(KEY_AUTO_START_SESSIONS, false),
            automaticNotification = prefs.getBoolean(KEY_AUTOMATIC_NOTIFICATION, true)
        )
    }

    /**
     * Сохранение настроек таймера
     */
    fun saveSettings(context: Context, settings: TimerSettings) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean(KEY_WORK_TIME_ENABLED, settings.workTimeEnabled)
            putBoolean(KEY_SHORT_BREAK_ENABLED, settings.shortBreakEnabled)
            putBoolean(KEY_LONG_BREAK_ENABLED, settings.longBreakEnabled)
            putInt(KEY_SESSIONS_BEFORE_LONG_BREAK, settings.sessionsBeforeLongBreak)
            putBoolean(KEY_AUTO_START_SESSIONS, settings.autoStartSessions)
            putBoolean(KEY_AUTOMATIC_NOTIFICATION, settings.automaticNotification)
            apply()
        }
    }
}


