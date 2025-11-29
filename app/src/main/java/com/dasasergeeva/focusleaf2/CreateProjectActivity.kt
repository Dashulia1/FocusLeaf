package com.dasasergeeva.focusleaf2

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.dasasergeeva.focusleaf2.databinding.ActivityCreateProjectBinding
import com.dasasergeeva.focusleaf2.models.Project
import com.dasasergeeva.focusleaf2.repository.ProjectRepository
import com.dasasergeeva.focusleaf2.utils.StatisticsManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Активность для создания нового проекта
 */
class CreateProjectActivity : BaseActivity() {

    companion object {
        private const val TAG = "CreateProjectActivity"
        
        // Цвета для палитры проектов
        private val PROJECT_COLORS = listOf(
            Color.parseColor("#FFB6C1"), // Нежно-розовый
            Color.parseColor("#ECD6FF"), // Нежно-фиолетовый
            Color.parseColor("#87CEEB"), // Голубой
            Color.parseColor("#B0B0B0")  // Серый
        )
        
        // Соответствие позиций SeekBar к минутам
        private val TOMATO_VALUES = intArrayOf(0, 25, 50, 75) // минут на помидор
    }

    private lateinit var binding: ActivityCreateProjectBinding
    private var selectedColor: Int = PROJECT_COLORS[0] // Нежно-розовый по умолчанию
    private var selectedTomatoes: Int = 1 // 1 помидор = 25 минут по умолчанию
    private var selectedColorView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Инициализация репозитория
        ProjectRepository.init(this)
        
        setupWindow()
        
        binding = ActivityCreateProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupTime()
        setupListeners()
        setupColorPalette()
        setupPriorityRadioGroup()
        setupTimeSeekBar()
        setupSaveButton()
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
    }

    /**
     * Настройка палитры цветов
     */
    private fun setupColorPalette() {
        PROJECT_COLORS.forEachIndexed { index, color ->
            val colorView = createColorCircle(color, index == 0) // Первый выбран по умолчанию
            binding.llColorPalette.addView(colorView)
        }
    }

    /**
     * Создание круга цвета
     */
    private fun createColorCircle(color: Int, isSelected: Boolean): View {
        val size = dpToPx(48)
        val margin = dpToPx(16)
        val containerSize = size + dpToPx(8) // Дополнительное место для обводки
        
        val container = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(containerSize, containerSize).apply {
                if (binding.llColorPalette.childCount > 0) {
                    marginStart = margin
                }
            }
        }
        
        // Внутренний круг с цветом
        val innerCircle = View(this).apply {
            layoutParams = FrameLayout.LayoutParams(size, size).apply {
                gravity = Gravity.CENTER
            }
            
            val drawable = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.OVAL
                setColor(color)
            }
            background = drawable
        }
        
        // Обводка для выбранного цвета
        val strokeView = View(this).apply {
            layoutParams = FrameLayout.LayoutParams(containerSize, containerSize).apply {
                gravity = Gravity.CENTER
            }
            visibility = if (isSelected) View.VISIBLE else View.GONE
            
            val strokeDrawable = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.OVAL
                setColor(Color.TRANSPARENT)
                setStroke(dpToPx(3), ContextCompat.getColor(this@CreateProjectActivity, R.color.project_card_text_primary))
            }
            background = strokeDrawable
        }
        
        container.addView(innerCircle)
        container.addView(strokeView)
        
        container.setOnClickListener {
            // Снимаем выделение с предыдущего выбранного цвета
            (selectedColorView as? FrameLayout)?.getChildAt(1)?.visibility = View.GONE
            
            // Выделяем новый выбранный цвет
            selectedColorView = container
            selectedColor = color
            strokeView.visibility = View.VISIBLE
            
            Log.d(TAG, "Color selected: ${Integer.toHexString(color)}")
        }
        
        if (isSelected) {
            selectedColorView = container
            strokeView.visibility = View.VISIBLE
        }
        
        return container
    }

    /**
     * Настройка RadioGroup для приоритета
     */
    private fun setupPriorityRadioGroup() {
        // По умолчанию выбран "Средний"
        binding.rbMediumPriority.isChecked = true
    }

    /**
     * Настройка SeekBar для оценки времени
     */
    private fun setupTimeSeekBar() {
        binding.sbTime.max = 3 // 0, 1, 2, 3 позиции = 0, 25, 50, 75 минут
        binding.sbTime.progress = 1 // По умолчанию 1 помидор (25 минут)
        
        // Обновляем текст при изменении
        updateTomatoCountText()
        
        binding.sbTime.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                selectedTomatoes = progress
                updateTomatoCountText()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Не используется
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Не используется
            }
        })
    }

    /**
     * Обновление текста с количеством помидоров
     */
    private fun updateTomatoCountText() {
        val minutes = TOMATO_VALUES[selectedTomatoes]
        binding.tvTomatoCount.text = getString(R.string.tomatoes_count_format, selectedTomatoes)
    }

    /**
     * Настройка кнопки сохранения
     */
    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            if (validateForm()) {
                saveProject()
            }
        }
    }

    /**
     * Валидация формы
     */
    private fun validateForm(): Boolean {
        val taskName = binding.etTaskName.text.toString().trim()
        
        if (taskName.isEmpty()) {
            binding.tilTaskName.error = getString(R.string.error_email_required)
            binding.etTaskName.requestFocus()
            return false
        }
        
        binding.tilTaskName.error = null
        return true
    }

    /**
     * Сохранение проекта
     */
    private fun saveProject() {
        val taskName = binding.etTaskName.text.toString().trim()
        
        // Определяем приоритет
        val priority = when (binding.rgPriority.checkedRadioButtonId) {
            R.id.rbLowPriority -> getString(R.string.priority_low)
            R.id.rbMediumPriority -> getString(R.string.priority_medium)
            R.id.rbHighPriority -> getString(R.string.priority_high)
            else -> getString(R.string.priority_medium)
        }
        
        // Получаем выбранное время в минутах
        val estimatedTime = TOMATO_VALUES[selectedTomatoes] // минуты
        val taskCount = 1 // По умолчанию 1 задача при создании проекта
        val projectTotalTime = estimatedTime * taskCount
        
        // Создаем новый проект
        val newProject = Project(
            id = UUID.randomUUID().toString(),
            name = taskName,
            color = selectedColor,
            priority = priority,
            estimatedTime = estimatedTime,
            taskCount = taskCount,
            projectTotalTime = projectTotalTime,
            isCompleted = false,
            estimatedTomatoes = selectedTomatoes, // количество помидоров
            completedSessions = 0,
            totalFocusTime = 0
        )
        
        // Сохраняем проект в репозиторий
        ProjectRepository.addProject(newProject)
        
        // Обновляем статистику (проект уже добавлен в репозиторий, статистика обновится автоматически)
        StatisticsManager.recalculateStats(this)
        
        Log.d(TAG, "Project created: ${newProject.name}, priority=${newProject.priority}, time=${newProject.estimatedTime} min")
        
        // Показываем уведомление
        Toast.makeText(
            this,
            "Проект '$taskName' создан!",
            Toast.LENGTH_SHORT
        ).show()
        
        // Закрываем экран и возвращаемся на экран проектов
        finish()
    }

    /**
     * Настройка поддержки русского ввода
     */
    private fun setupRussianInputSupport() {
        binding.etTaskName.apply {
            // Настройка для поддержки русского языка
            inputType = InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or
                    InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
            imeOptions = EditorInfo.IME_ACTION_DONE
            // Убираем любые ограничения для поддержки всех языков
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
}

