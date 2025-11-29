package com.dasasergeeva.focusleaf2

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.dasasergeeva.focusleaf2.databinding.ActivityWeekAnalyticsBinding
import com.dasasergeeva.focusleaf2.repository.ProjectRepository
import com.dasasergeeva.focusleaf2.utils.AnalyticsManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Активность для отображения аналитики за неделю
 */
class WeekAnalyticsActivity : BaseActivity() {

    private lateinit var binding: ActivityWeekAnalyticsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Инициализация репозитория
        ProjectRepository.init(this)
        
        setupWindow()
        
        binding = ActivityWeekAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupListeners()
        setupTime()
        updateStatistics()
        setupCharts()
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
        val currentWeekStats = AnalyticsManager.getCurrentWeekStats()
        val previousWeekStats = AnalyticsManager.getPreviousWeekStats()
        
        binding.tvCurrentWeekTime.text = "${currentWeekStats.totalHours} h"
        binding.tvPreviousWeekTime.text = "${previousWeekStats.totalHours} h"
    }

    /**
     * Настройка диаграмм
     */
    private fun setupCharts() {
        setupCurrentWeekChart()
        setupPreviousWeekChart()
    }

    /**
     * Настройка диаграммы текущей недели
     */
    private fun setupCurrentWeekChart() {
        val weekData = AnalyticsManager.getCurrentWeekData()
        setupWeekChart(binding.llCurrentWeekChart, weekData, isCurrentWeek = true)
    }

    /**
     * Настройка диаграммы предыдущей недели
     */
    private fun setupPreviousWeekChart() {
        val weekData = AnalyticsManager.getPreviousWeekData()
        setupWeekChart(binding.llPreviousWeekChart, weekData, isCurrentWeek = false)
    }

    /**
     * Настройка диаграммы недели
     */
    private fun setupWeekChart(chartLayout: LinearLayout, weekData: List<Int>, isCurrentWeek: Boolean) {
        chartLayout.removeAllViews()
        
        val maxValue = weekData.maxOrNull() ?: 1
        val chartHeight = chartLayout.height
        
        weekData.forEachIndexed { index, value ->
            val column = createWeekChartColumn(value, maxValue, index, isCurrentWeek, chartHeight)
            chartLayout.addView(column)
        }
    }

    /**
     * Создание столбца диаграммы недели
     */
    private fun createWeekChartColumn(
        value: Int,
        maxValue: Int,
        dayIndex: Int,
        isCurrentWeek: Boolean,
        maxHeight: Int
    ): View {
        val column = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, 0).apply {
                weight = 1f
                marginEnd = dpToPx(4)
            }
            
            // Высота столбца пропорциональна максимальному значению
            val heightPercent = if (maxValue > 0) value.toFloat() / maxValue else 0f
            val minHeight = dpToPx(8) // Минимальная высота для видимости
            
            val calculatedHeight = if (maxHeight > 0) {
                (maxHeight * heightPercent).toInt()
            } else {
                dpToPx(40) // Значение по умолчанию
            }
            val finalHeight = maxOf(calculatedHeight, minHeight)
            
            layoutParams.height = finalHeight
            
            // Цвет столбца
            val color = if (isCurrentWeek) {
                when {
                    value >= maxValue * 0.8 -> Color.parseColor("#FFB6C1") // Розовый для высоких значений
                    value >= maxValue * 0.5 -> Color.parseColor("#ECD6FF") // Фиолетовый для средних значений
                    else -> Color.parseColor("#E0E0E0") // Серый для низких значений
                }
            } else {
                Color.parseColor("#B0B0B0") // Серый для предыдущей недели
            }
            
            // Закругленные углы сверху
            val drawable = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                setColor(color)
                cornerRadii = floatArrayOf(
                    dpToPx(4).toFloat(), dpToPx(4).toFloat(), // topLeft
                    dpToPx(4).toFloat(), dpToPx(4).toFloat(), // topRight
                    0f, 0f, // bottomRight
                    0f, 0f  // bottomLeft
                )
            }
            background = drawable
        }
        
        return column
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
        // Пересоздаем диаграммы после того, как view будет измерен
        binding.llCurrentWeekChart.post {
            setupCharts()
        }
    }
}

