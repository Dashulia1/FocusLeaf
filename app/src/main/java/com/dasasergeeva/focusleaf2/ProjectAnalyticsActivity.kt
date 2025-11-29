package com.dasasergeeva.focusleaf2

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.dasasergeeva.focusleaf2.databinding.ActivityProjectAnalyticsBinding
import com.dasasergeeva.focusleaf2.repository.ProjectRepository
import com.dasasergeeva.focusleaf2.utils.AnalyticsManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Активность для отображения аналитики проектов
 */
class ProjectAnalyticsActivity : BaseActivity() {

    private lateinit var binding: ActivityProjectAnalyticsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Инициализация репозитория
        ProjectRepository.init(this)
        
        // Загрузка проектов из SharedPreferences
        ProjectRepository.loadProjects()
        
        // Инициализация тестовых данных если нужно
        if (ProjectRepository.getAllProjects().isEmpty()) {
            ProjectRepository.initializeTestData(this)
        }
        
        setupWindow()
        
        binding = ActivityProjectAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupListeners()
        updateTotalTime()
        setupProjectsChart()
        setupProgressBars()
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
     * Обновление общего времени всех проектов
     */
    private fun updateTotalTime() {
        val projects = ProjectRepository.getAllProjects()
        android.util.Log.d("ProjectAnalytics", "Projects count: ${projects.size}")
        projects.forEach { project ->
            android.util.Log.d("ProjectAnalytics", "Project: ${project.name}, projectTotalTime: ${project.projectTotalTime}, estimatedTime: ${project.estimatedTime}, taskCount: ${project.taskCount}")
        }
        
        val totalTime = AnalyticsManager.getTotalProjectsTime()
        android.util.Log.d("ProjectAnalytics", "Total time: $totalTime")
        binding.tvTotalTime.text = totalTime
    }

    /**
     * Настройка сравнительной диаграммы проектов
     */
    private fun setupProjectsChart() {
        val chartData = AnalyticsManager.getProjectsChartData()
        setupChartColumns(chartData)
    }

    /**
     * Настройка колонок диаграммы
     */
    private fun setupChartColumns(data: List<Int>) {
        binding.llProjectsChart.removeAllViews()
        
        val maxValue = data.maxOfOrNull { kotlin.math.abs(it) } ?: 1
        val chartHeight = binding.llProjectsChart.height
        
        // Группируем данные по 4 колонки (q1, q2, q3, q4)
        // Каждая группа содержит 4 столбца
        val groups = data.chunked(4)
        
        groups.forEachIndexed { groupIndex, group ->
            val groupLayout = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                    weight = 1f
                    if (groupIndex < groups.size - 1) {
                        marginEnd = dpToPx(8)
                    }
                }
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.BOTTOM
            }
            
            group.forEachIndexed { index, value ->
                val column = createChartColumn(value, maxValue, chartHeight)
                groupLayout.addView(column)
            }
            
            binding.llProjectsChart.addView(groupLayout)
        }
        
        // Если данных меньше 16 (4 группы по 4), добавляем пустые группы
        val totalGroups = 4
        while (groups.size < totalGroups) {
            val emptyGroup = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                    weight = 1f
                    marginEnd = dpToPx(8)
                }
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.BOTTOM
            }
            
            // Добавляем 4 пустых столбца
            repeat(4) {
                val emptyColumn = View(this@ProjectAnalyticsActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                        weight = 1f
                        marginEnd = dpToPx(2)
                    }
                }
                emptyGroup.addView(emptyColumn)
            }
            
            binding.llProjectsChart.addView(emptyGroup)
        }
    }

    /**
     * Создание колонки диаграммы
     */
    private fun createChartColumn(value: Int, maxValue: Int, maxHeight: Int): View {
        val columnContainer = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT
            ).apply {
                weight = 1f
                marginEnd = dpToPx(2)
            }
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.BOTTOM
        }
        
        // Числовое значение
        val valueText = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            text = value.toString()
            setTextColor(if (value >= 0) {
                ContextCompat.getColor(this@ProjectAnalyticsActivity, R.color.project_card_text_primary)
            } else {
                Color.parseColor("#FF6B6B")
            })
            textSize = 10f
            gravity = Gravity.CENTER
        }
        
        // Колонка диаграммы
        val heightPercent = if (maxValue > 0) kotlin.math.abs(value).toFloat() / maxValue else 0f
        val minHeight = dpToPx(8)
        val calculatedHeight = if (maxHeight > 0) {
            (maxHeight * heightPercent).toInt()
        } else {
            dpToPx(40)
        }
        val finalHeight = maxOf(calculatedHeight, minHeight)
        
        val column = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                finalHeight
            ).apply {
                topMargin = dpToPx(4)
            }
            
            // Цвет колонки в зависимости от значения
            val color = if (value >= 0) {
                when {
                    value >= maxValue * 0.8 -> Color.parseColor("#FFB6C1") // Розовый для высоких значений
                    value >= maxValue * 0.5 -> Color.parseColor("#ECD6FF") // Фиолетовый для средних значений
                    else -> Color.parseColor("#E0E0E0") // Серый для низких значений
                }
            } else {
                Color.parseColor("#B0B0B0") // Серый для отрицательных
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
        
        columnContainer.addView(valueText)
        columnContainer.addView(column)
        
        return columnContainer
    }

    /**
     * Настройка прогресс-баров проектов
     */
    private fun setupProgressBars() {
        val projects = ProjectRepository.getAllProjects()
        val analyticsData = AnalyticsManager.getProjectAnalyticsData()
        
        android.util.Log.d("ProjectAnalytics", "Setting up progress bars: ${projects.size} projects, ${analyticsData.size} analytics")
        
        binding.llProjectsProgressContainer.removeAllViews()
        
        if (projects.isEmpty()) {
            // Показываем сообщение, если проектов нет
            val emptyText = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                text = "Нет проектов"
                setTextColor(ContextCompat.getColor(this@ProjectAnalyticsActivity, R.color.project_card_text_secondary))
                textSize = 16f
                gravity = Gravity.CENTER
                setPadding(0, dpToPx(32), 0, 0)
            }
            binding.llProjectsProgressContainer.addView(emptyText)
            return
        }
        
        projects.forEachIndexed { index, project ->
            val analytics = analyticsData.getOrNull(index)
            if (analytics != null) {
                android.util.Log.d("ProjectAnalytics", "Creating progress bar for: ${project.name}, completed: ${analytics.completed}, estimated: ${analytics.estimated}")
                val progressBarView = createProjectProgressBar(project, analytics)
                binding.llProjectsProgressContainer.addView(progressBarView)
            } else {
                android.util.Log.w("ProjectAnalytics", "No analytics data for project: ${project.name}")
            }
        }
    }

    /**
     * Создание прогресс-бара для проекта
     */
    private fun createProjectProgressBar(
        project: com.dasasergeeva.focusleaf2.models.Project,
        analytics: AnalyticsManager.ProjectAnalytics
    ): View {
        // Расчет времени проекта
        val projectTime = if (project.projectTotalTime > 0) {
            project.projectTotalTime
        } else {
            project.estimatedTime * project.taskCount
        }
        val projectHours = projectTime / 60
        val projectMinutes = projectTime % 60
        val projectTimeText = String.format(Locale.getDefault(), "%02d.%02d", projectHours, projectMinutes)
        
        return LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(16)
            }
            orientation = LinearLayout.VERTICAL
            
            // Название проекта
            val projectName = TextView(this@ProjectAnalyticsActivity).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                text = project.name
                setTextColor(ContextCompat.getColor(this@ProjectAnalyticsActivity, R.color.project_card_text_primary))
                textSize = 16f
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            
            // Время проекта
            val projectTimeView = TextView(this@ProjectAnalyticsActivity).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dpToPx(4)
                }
                text = projectTimeText
                setTextColor(ContextCompat.getColor(this@ProjectAnalyticsActivity, R.color.project_card_text_secondary))
                textSize = 14f
            }
            
            // Прогресс (завершено/оценено)
            val progressText = TextView(this@ProjectAnalyticsActivity).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dpToPx(4)
                }
                text = "${analytics.completed}/${analytics.estimated} помидоров"
                setTextColor(ContextCompat.getColor(this@ProjectAnalyticsActivity, R.color.project_card_text_secondary))
                textSize = 12f
            }
            
            // Прогресс-бар
            val progressBar = ProgressBar(
                this@ProjectAnalyticsActivity,
                null,
                android.R.attr.progressBarStyleHorizontal
            ).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dpToPx(8)
                ).apply {
                    topMargin = dpToPx(8)
                }
                
                max = if (analytics.estimated > 0) analytics.estimated else 100
                progress = analytics.completed
                
                // Стиль прогресс-бара
                progressDrawable = ContextCompat.getDrawable(
                    this@ProjectAnalyticsActivity,
                    R.drawable.progress_bar_horizontal
                )
            }
            
            addView(projectName)
            addView(projectTimeView)
            addView(progressText)
            addView(progressBar)
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
        // Загружаем проекты при возврате на экран
        ProjectRepository.loadProjects()
        // Обновляем данные каждый раз при открытии экрана
        updateTotalTime()
        setupProgressBars()
        // Пересоздаем диаграмму после того, как view будет измерен
        binding.llProjectsChart.post {
            setupProjectsChart()
        }
    }
}

