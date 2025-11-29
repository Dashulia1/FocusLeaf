package com.dasasergeeva.focusleaf2

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dasasergeeva.focusleaf2.databinding.ActivityProjectsBinding
import com.dasasergeeva.focusleaf2.models.Project
import com.dasasergeeva.focusleaf2.projects.ProjectAdapter
import com.dasasergeeva.focusleaf2.repository.ProjectRepository
import com.dasasergeeva.focusleaf2.utils.StatisticsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Активность для управления проектами
 */
class ProjectsActivity : BaseActivity() {

    companion object {
        private const val TAG = "ProjectsActivity"
    }

    private lateinit var binding: ActivityProjectsBinding
    private lateinit var adapter: ProjectAdapter
    private var projectsJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Инициализация репозитория
        ProjectRepository.init(this)
        
        // Инициализация тестовых данных если нужно
        if (ProjectRepository.getAllProjects().isEmpty()) {
            ProjectRepository.initializeTestData(this)
        } else {
            // Загружаем проекты из SharedPreferences
            ProjectRepository.loadProjects()
        }
        
        setupWindow()
        
        binding = ActivityProjectsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupRecyclerView()
        setupListeners()
        setupTime()
        observeProjects()
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
     * Настройка RecyclerView
     */
    private fun setupRecyclerView() {
        adapter = ProjectAdapter(mutableListOf()) { project ->
            // Обработка клика по проекту
            onProjectClick(project)
        }
        
        binding.rvProjects.layoutManager = LinearLayoutManager(this)
        binding.rvProjects.adapter = adapter
    }
    
    /**
     * Наблюдение за изменениями списка проектов
     */
    private fun observeProjects() {
        projectsJob = lifecycleScope.launch {
            ProjectRepository.projects.collect { projects ->
                adapter.updateProjects(projects)
                updateEmptyState(projects.isEmpty())
                Log.d(TAG, "Projects updated: ${projects.size} projects")
            }
        }
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

        // Кнопка поиска - переход на экран поиска проектов
        binding.ibSearch.setOnClickListener {
            val intent = android.content.Intent(this, ProjectsSearchActivity::class.java)
            startActivity(intent)
        }

        // FAB для создания проекта - переход на экран создания проекта
        binding.fabCreateProject.setOnClickListener {
            val intent = android.content.Intent(this, CreateProjectActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Обновление состояния пустого списка
     */
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.tvEmptyState.visibility = if (isEmpty) {
            View.VISIBLE
        } else {
            View.GONE
        }
        binding.rvProjects.visibility = if (isEmpty) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Перезагружаем проекты при возврате на экран (чтобы увидеть изменения)
        // StateFlow автоматически обновит UI через observeProjects()
        ProjectRepository.loadProjects()
        
        // Принудительное обновление статистики при возврате на экран
        StatisticsManager.recalculateStats(this)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        projectsJob?.cancel()
    }

    /**
     * Обработка клика по проекту - показ деталей проекта
     */
    private fun onProjectClick(project: Project) {
        showProjectDetails(project)
    }

    /**
     * Показать детали проекта в диалоге
     */
    private fun showProjectDetails(project: Project) {
        val message = buildString {
            append(getString(R.string.priority_label, project.priority))
            append("\n")
            append(getString(R.string.estimated_time_label, project.estimatedTime))
        }

        AlertDialog.Builder(this)
            .setTitle(project.name)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

}

