package com.dasasergeeva.focusleaf2

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.dasasergeeva.focusleaf2.databinding.ActivityTodayAnalyticsBinding
import com.dasasergeeva.focusleaf2.repository.ProjectRepository
import com.dasasergeeva.focusleaf2.utils.AnalyticsManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Активность для отображения аналитики за сегодня
 */
class TodayAnalyticsActivity : BaseActivity() {

    private lateinit var binding: ActivityTodayAnalyticsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Инициализация репозитория
        ProjectRepository.init(this)
        
        setupWindow()
        
        binding = ActivityTodayAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupListeners()
        setupTime()
        updateStatistics()
        setupChart()
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
     * Настройка времени в статус баре
     */
    private fun setupTime() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = timeFormat.format(Date())
        binding.tvTime.text = currentTime
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
     * Обновление статистики
     */
    private fun updateStatistics() {
        val analyticsData = AnalyticsManager.getTodayAnalytics()
        
        binding.tvSessionsCount.text = analyticsData.totalSessions.toString()
        binding.tvTotalSessions.text = getString(R.string.total_sessions)
        binding.tvFocusTime.text = getString(R.string.focus_time, analyticsData.focusTime)
        binding.tvConcentration.text = getString(R.string.average_concentration, analyticsData.averageConcentration)
    }

    /**
     * Настройка диаграммы
     */
    private fun setupChart() {
        val analyticsData = AnalyticsManager.getTodayAnalytics()
        setupChartWithData(analyticsData.hourlyData)
    }

    /**
     * Настройка диаграммы с данными
     */
    private fun setupChartWithData(chartData: List<Int>) {
        binding.llChartColumns.removeAllViews()
        
        val maxHeight = binding.llChartContainer.height
        val chartHeight = maxHeight - dpToPx(32) // Вычитаем padding
        
        chartData.forEachIndexed { index, value ->
            val column = createChartColumn(value, index, chartHeight)
            binding.llChartColumns.addView(column)
        }
    }

    /**
     * Создание столбца диаграммы
     */
    private fun createChartColumn(value: Int, index: Int, maxHeight: Int): View {
        val columnContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.BOTTOM
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                weight = 1f
                marginEnd = dpToPx(4)
            }
        }
        
        // Высота столбца в процентах от максимальной высоты
        val heightPercent = value / 100f
        val minHeight = dpToPx(20) // Минимальная высота для видимости
        val calculatedHeight = (maxHeight * heightPercent).toInt()
        val finalHeight = maxOf(calculatedHeight, minHeight)
        
        val column = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                finalHeight
            )
            setBackgroundColor(getColumnColor(value))
        }
        
        // Добавляем значение над столбцом
        val label = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(4)
            }
            text = "$value%"
            setTextColor(ContextCompat.getColor(this@TodayAnalyticsActivity, R.color.project_card_text_secondary))
            textSize = 10f
            gravity = Gravity.CENTER
        }
        
        columnContainer.addView(label)
        columnContainer.addView(column)
        
        return columnContainer
    }

    /**
     * Получение цвета столбца в зависимости от значения
     */
    private fun getColumnColor(value: Int): Int {
        return when {
            value >= 80 -> Color.parseColor("#FFB6C1") // Розовый для высоких значений
            value >= 60 -> Color.parseColor("#ECD6FF") // Фиолетовый для средних значений
            else -> Color.parseColor("#E0E0E0") // Серый для низких значений
        }
    }

    /**
     * Конвертация dp в пиксели
     */
    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    override fun onResume() {
        super.onResume()
        // Обновляем данные каждый раз при открытии экрана
        updateStatistics()
        // Пересоздаем диаграмму после того, как view будет измерен
        binding.llChartContainer.post {
            setupChart()
        }
    }
}

