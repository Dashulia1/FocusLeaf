package com.dasasergeeva.focusleaf2

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.dasasergeeva.focusleaf2.databinding.ActivityGroupSessionBinding
import java.util.Locale

class GroupSessionActivity : BaseActivity() {

    private lateinit var binding: ActivityGroupSessionBinding
    private var sessionTimer: CountDownTimer? = null
    private var isSessionActive = false
    private var timeLeftInMillis: Long = 25 * 60 * 60 * 1000L // 25 часов

    data class Participant(
        val name: String,
        val avatarRes: Int,
        val isOnline: Boolean
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setupWindow()
        
        binding = ActivityGroupSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupListeners()
        setupSessionTimer()
        setupJoinButton()
        setupParticipants()
        setupQuickSupport()
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
     * Настройка таймера сессии
     */
    private fun setupSessionTimer() {
        sessionTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerText(millisUntilFinished)
            }

            override fun onFinish() {
                binding.tvSessionTimer.text = "00:00:00"
                binding.tvSessionTimer.setTextColor(Color.parseColor("#F44336"))
            }
        }
        
        // Запуск таймера
        sessionTimer?.start()
    }

    /**
     * Обновление текста таймера
     */
    private fun updateTimerText(millisUntilFinished: Long) {
        val hours = millisUntilFinished / (1000 * 60 * 60)
        val minutes = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = (millisUntilFinished % (1000 * 60)) / 1000

        val timeText = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
        binding.tvSessionTimer.text = timeText

        // Изменение цвета при малом времени
        if (hours == 0L && minutes < 5) {
            binding.tvSessionTimer.setTextColor(Color.parseColor("#FF9800"))
        } else {
            binding.tvSessionTimer.setTextColor(ContextCompat.getColor(this, R.color.main_accent_purple))
        }
    }

    /**
     * Настройка кнопки присоединения
     */
    private fun setupJoinButton() {
        binding.fabJoinSession.setOnClickListener {
            if (isSessionActive) {
                leaveSession()
            } else {
                joinSession()
            }
        }
    }

    /**
     * Присоединение к сессии
     */
    private fun joinSession() {
        isSessionActive = true
        binding.fabJoinSession.icon = ContextCompat.getDrawable(this, R.drawable.ic_leave)
        binding.fabJoinSession.text = getString(R.string.leave_session)
        binding.fabJoinSession.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F44336"))

        // Показать уведомление о присоединении
        Toast.makeText(this, "Вы присоединились к сессии", Toast.LENGTH_SHORT).show()

        // Запустить таймер фокуса для пользователя
        startPersonalFocusTimer()
    }

    /**
     * Выход из сессии
     */
    private fun leaveSession() {
        isSessionActive = false
        binding.fabJoinSession.icon = ContextCompat.getDrawable(this, R.drawable.ic_join)
        binding.fabJoinSession.text = getString(R.string.join_session)
        binding.fabJoinSession.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F44336"))

        // Показать уведомление о выходе
        Toast.makeText(this, "Вы вышли из сессии", Toast.LENGTH_SHORT).show()

        // Остановить персональный таймер
        stopPersonalFocusTimer()
    }

    /**
     * Запуск персонального таймера фокуса
     */
    private fun startPersonalFocusTimer() {
        // TODO: Запуск 25-минутного таймера фокуса
        // Можно интегрировать с основным таймером приложения
    }

    /**
     * Остановка персонального таймера
     */
    private fun stopPersonalFocusTimer() {
        // TODO: Остановка персонального таймера
    }

    /**
     * Настройка участников
     */
    private fun setupParticipants() {
        val participants = listOf(
            Participant("Alice", R.drawable.ic_female_avatar, true),
            Participant("Bob", R.drawable.ic_male_avatar, true),
            Participant("Charlie", R.drawable.ic_default_avatar, false),
            Participant("Diana", R.drawable.ic_female_avatar, true)
        )

        // Установка аватаров
        binding.ivAvatar1.setImageResource(participants[0].avatarRes)
        binding.ivAvatar2.setImageResource(participants[1].avatarRes)
        binding.ivAvatar3.setImageResource(participants[2].avatarRes)
        binding.ivAvatar4.setImageResource(participants[3].avatarRes)

        // Установка индикаторов онлайн-статуса
        val onlineViewIds = listOf(R.id.vOnline1, R.id.vOnline2, R.id.vOnline3, R.id.vOnline4)
        
        participants.forEachIndexed { index, participant ->
            binding.root.findViewById<View>(onlineViewIds[index])?.visibility = 
                if (participant.isOnline) View.VISIBLE else View.GONE
        }
    }

    /**
     * Обработка кнопки быстрой поддержки
     */
    private fun setupQuickSupport() {
        binding.fabQuickSupport.setOnClickListener {
            val intent = android.content.Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sessionTimer?.cancel()
        stopPersonalFocusTimer()
    }
}

