package com.dasasergeeva.focusleaf2

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dasasergeeva.focusleaf2.databinding.ActivityProjectsSearchBinding
import com.dasasergeeva.focusleaf2.models.Project
import com.dasasergeeva.focusleaf2.repository.ProjectRepository
import com.dasasergeeva.focusleaf2.search.ProjectsSearchAdapter
import com.dasasergeeva.focusleaf2.utils.StatisticsManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Активность для поиска и управления проектами
 */
class ProjectsSearchActivity : BaseActivity() {

    companion object {
        private const val TAG = "ProjectsSearchActivity"
    }

    /**
     * Тип фильтра для проектов
     */
    private enum class FilterType {
        ALL,      // Все проекты
        ACTIVE    // Только активные (не выполненные)
    }

    private lateinit var binding: ActivityProjectsSearchBinding
    private lateinit var adapter: ProjectsSearchAdapter
    private val allProjects = mutableListOf<Project>()
    private val filteredProjects = mutableListOf<Project>()
    private var currentFilter = FilterType.ALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Инициализация репозитория
        ProjectRepository.init(this)
        
        setupWindow()
        
        binding = ActivityProjectsSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupListeners()
        setupTime()
        setupRecyclerView()
        setupProjects()
        setupSearch()
        setupFilters()
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
     * Настройка проектов (загрузка из репозитория)
     */
    private fun setupProjects() {
        // Инициализация тестовых данных если нужно
        if (ProjectRepository.getAllProjects().isEmpty()) {
            ProjectRepository.initializeTestData(this)
        } else {
            // Принудительно обновляем данные с правильными приоритетами
            // (можно убрать после первого запуска)
            ProjectRepository.forceUpdateTestData(this)
        }
        
        // Загрузка проектов из репозитория
        allProjects.clear()
        allProjects.addAll(ProjectRepository.getAllProjects())
        
        filteredProjects.clear()
        filteredProjects.addAll(allProjects)
        
        adapter.submitList(filteredProjects.toList())
        updateEmptyState()
    }

    /**
     * Настройка поиска
     */
    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Не используется
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Не используется
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProjects(s?.toString() ?: "")
            }
        })
    }

    /**
     * Настройка фильтров (только "Все" и "Активные")
     */
    private fun setupFilters() {
        // По умолчанию выбран фильтр "Все"
        binding.cbFilterAll.isChecked = true
        binding.cbFilterActive.isChecked = false
        currentFilter = FilterType.ALL

        binding.cbFilterAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.cbFilterActive.isChecked = false
                currentFilter = FilterType.ALL
                filterProjects(binding.etSearch.text.toString())
                Log.d(TAG, "Filter changed to: ALL")
            }
        }

        binding.cbFilterActive.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.cbFilterAll.isChecked = false
                currentFilter = FilterType.ACTIVE
                filterProjects(binding.etSearch.text.toString())
                Log.d(TAG, "Filter changed to: ACTIVE")
            }
        }
    }

    /**
     * Настройка RecyclerView
     */
    private fun setupRecyclerView() {
        adapter = ProjectsSearchAdapter { project, isChecked ->
            updateProjectCompletion(project, isChecked)
        }

        binding.rvProjects.layoutManager = LinearLayoutManager(this)
        binding.rvProjects.adapter = adapter
        // Список будет установлен в setupProjects()
    }

    /**
     * Фильтрация проектов по запросу и фильтрам
     */
    private fun filterProjects(query: String = binding.etSearch.text.toString()) {
        // Перезагружаем проекты из репозитория для получения актуальных данных
        allProjects.clear()
        allProjects.addAll(ProjectRepository.getAllProjects())

        val filtered = allProjects.filter { project ->
            // Фильтр по поисковому запросу
            val matchesSearch = query.isEmpty() || 
                    project.name.contains(query, ignoreCase = true)

            // Фильтр по статусу
            val matchesFilter = when (currentFilter) {
                FilterType.ALL -> true // Показать все проекты
                FilterType.ACTIVE -> !project.isCompleted // Только НЕ выполненные проекты
            }

            matchesSearch && matchesFilter
        }

        filteredProjects.clear()
        filteredProjects.addAll(filtered)
        adapter.submitList(filteredProjects.toList())
        updateEmptyState()

        Log.d(TAG, "Filtered projects: ${filtered.size} (filter: $currentFilter, query: '$query')")
    }
    
    override fun onResume() {
        super.onResume()
        // Перезагружаем проекты при возврате на экран и применяем фильтр
        setupProjects()
        filterProjects()
    }

    /**
     * Обновление статуса завершения проекта
     */
    private fun updateProjectCompletion(project: Project, isCompleted: Boolean) {
        try {
            Log.d(TAG, "Updating project: ${project.name}, completed=$isCompleted")
            
            // Обновление через StatisticsManager (который обновит репозиторий и статистику)
            StatisticsManager.updateProjectCompletion(this, project.id, isCompleted)
            
            // Перезагружаем проекты из репозитория для получения актуальных данных
            allProjects.clear()
            allProjects.addAll(ProjectRepository.getAllProjects())
            
            // Немедленное обновление фильтрации (проект должен исчезнуть из "Активные")
            filterProjects()
            
            // Дополнительно пересчитываем статистику через небольшую задержку
            Handler(Looper.getMainLooper()).postDelayed({
                StatisticsManager.recalculateStats(this)
            }, 400)
            
            // Показываем уведомление
            val message = if (isCompleted) {
                "✓ Проект '${project.name}' завершен. Статистика обновлена."
            } else {
                "↻ Проект '${project.name}' снова активен. Статистика обновлена."
            }
            
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            
            Log.d(TAG, "Project updated successfully, filter reapplied, stats will update")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating project completion", e)
            Toast.makeText(
                this,
                "Ошибка при обновлении проекта",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Обновление состояния пустого списка
     */
    private fun updateEmptyState() {
        binding.tvEmptyState.visibility = if (filteredProjects.isEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}

