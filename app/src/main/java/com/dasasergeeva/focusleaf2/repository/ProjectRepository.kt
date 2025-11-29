package com.dasasergeeva.focusleaf2.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.dasasergeeva.focusleaf2.models.Project
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.reflect.Type

/**
 * Репозиторий для управления проектами с реактивными обновлениями
 */
object ProjectRepository {

    private const val PREFS_NAME = "projects_prefs"
    private const val KEY_PROJECTS = "projects"
    private const val TAG = "ProjectRepository"

    private var sharedPreferences: SharedPreferences? = null
    private val gson = Gson()
    private val projectType: Type = object : TypeToken<List<Project>>() {}.type
    
    // Реактивный поток для списка проектов
    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    /**
     * Инициализация репозитория с контекстом
     */
    fun init(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            loadProjects() // Загружаем проекты при инициализации
        }
    }

    private fun getPrefs(): SharedPreferences {
        return sharedPreferences ?: throw IllegalStateException("ProjectRepository not initialized. Call init(context) first.")
    }

    /**
     * Получить все проекты
     */
    fun getAllProjects(): List<Project> {
        return _projects.value
    }

    /**
     * Получить только активные (не выполненные) проекты
     */
    fun getActiveProjects(): List<Project> {
        return _projects.value.filter { !it.isCompleted }
    }

    /**
     * Загрузка проектов из SharedPreferences
     */
    fun loadProjects() {
        val json = getPrefs().getString(KEY_PROJECTS, "[]") ?: "[]"
        try {
            val loadedProjects = gson.fromJson<List<Project>>(json, projectType) ?: emptyList()
            _projects.value = loadedProjects
            Log.d(TAG, "Projects loaded: ${loadedProjects.size} projects")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading projects", e)
            _projects.value = emptyList()
        }
    }

    /**
     * Сохранить все проекты
     */
    private fun saveProjects(projects: List<Project>) {
        val json = gson.toJson(projects)
        // Используем commit() для синхронного сохранения (гарантия сохранения)
        val success = getPrefs().edit()
            .putString(KEY_PROJECTS, json)
            .commit()
        Log.d(TAG, "Projects saved: success=$success, count=${projects.size}")
    }

    /**
     * Обновить статус выполнения проекта
     */
    fun updateProjectCompletion(projectId: String, isCompleted: Boolean) {
        val projects = _projects.value.toMutableList()
        val index = projects.indexOfFirst { it.id == projectId }
        
        if (index != -1) {
            val project = projects[index]
            val updatedProject = project.copy(isCompleted = isCompleted)
            projects[index] = updatedProject
            _projects.value = projects
            saveProjects(projects)
            Log.d(TAG, "Project $projectId completion updated to $isCompleted")
        } else {
            Log.w(TAG, "Project $projectId not found for update")
        }
    }

    /**
     * Получить проект по ID
     */
    fun getProjectById(projectId: String): Project? {
        return getAllProjects().find { it.id == projectId }
    }

    /**
     * Добавить новый проект
     */
    fun addProject(project: Project) {
        val currentProjects = _projects.value.toMutableList()
        currentProjects.add(project)
        _projects.value = currentProjects
        
        // Сохранение в SharedPreferences
        saveProjects(currentProjects)
        
        Log.d(TAG, "Project added: ${project.name}, total: ${currentProjects.size}")
    }

    /**
     * Обновить проект
     */
    fun updateProject(project: Project) {
        val projects = _projects.value.toMutableList()
        val index = projects.indexOfFirst { it.id == project.id }
        
        if (index != -1) {
            projects[index] = project
            _projects.value = projects
            saveProjects(projects)
            Log.d(TAG, "Project updated: ${project.name}")
        }
    }

    /**
     * Принудительное обновление тестовых данных (с правильными приоритетами)
     */
    fun forceUpdateTestData(context: Context) {
        if (sharedPreferences == null) {
            init(context)
        }
        
        // Получаем существующие проекты для сохранения их статуса isCompleted
        val existingProjects = getAllProjects()
        val existingProjectsMap = existingProjects.associateBy { it.id }
        
        val testProjects = listOf(
            Project(
                id = "1",
                name = "Медитация",
                color = android.graphics.Color.parseColor("#ECD6FF"),
                priority = "Средний",
                estimatedTime = 25,
                taskCount = 1,
                projectTotalTime = 25, // 1 задача * 25 минут
                isCompleted = existingProjectsMap["1"]?.isCompleted ?: false,
                estimatedTomatoes = 2, // 2 помидора
                completedSessions = existingProjectsMap["1"]?.completedSessions ?: 0,
                totalFocusTime = existingProjectsMap["1"]?.totalFocusTime ?: 0
            ),
            Project(
                id = "2",
                name = "Уборка",
                color = android.graphics.Color.parseColor("#FFB6C1"),
                priority = "Высокий",
                estimatedTime = 25,
                taskCount = 1,
                projectTotalTime = 25, // 1 задача * 25 минут
                isCompleted = existingProjectsMap["2"]?.isCompleted ?: false,
                estimatedTomatoes = 1, // 1 помидор
                completedSessions = existingProjectsMap["2"]?.completedSessions ?: 0,
                totalFocusTime = existingProjectsMap["2"]?.totalFocusTime ?: 0
            ),
            Project(
                id = "3",
                name = "Танцы",
                color = android.graphics.Color.parseColor("#FFB6C1"),
                priority = "Высокий",
                estimatedTime = 25,
                taskCount = 1,
                projectTotalTime = 25, // 1 задача * 25 минут
                isCompleted = existingProjectsMap["3"]?.isCompleted ?: false,
                estimatedTomatoes = 1, // 1 помидор
                completedSessions = existingProjectsMap["3"]?.completedSessions ?: 0,
                totalFocusTime = existingProjectsMap["3"]?.totalFocusTime ?: 0
            )
        )
        _projects.value = testProjects
        saveProjects(testProjects)
    }

    /**
     * Инициализация тестовых данных
     */
    fun initializeTestData(context: Context) {
        // Инициализация если еще не была выполнена
        if (sharedPreferences == null) {
            init(context)
        }
        if (getAllProjects().isEmpty()) {
            val testProjects = listOf(
                Project(
                    id = "1",
                    name = "Медитация",
                    color = android.graphics.Color.parseColor("#ECD6FF"),
                    priority = "Средний",
                    estimatedTime = 25,
                    taskCount = 1,
                    projectTotalTime = 25, // 1 задача * 25 минут
                    isCompleted = false,
                    estimatedTomatoes = 2, // 2 помидора
                    completedSessions = 0,
                    totalFocusTime = 0
                ),
                Project(
                    id = "2",
                    name = "Уборка",
                    color = android.graphics.Color.parseColor("#FFB6C1"),
                    priority = "Высокий",
                    estimatedTime = 25,
                    taskCount = 1,
                    projectTotalTime = 25, // 1 задача * 25 минут
                    isCompleted = false,
                    estimatedTomatoes = 1, // 1 помидор
                    completedSessions = 0,
                    totalFocusTime = 0
                ),
                Project(
                    id = "3",
                    name = "Танцы",
                    color = android.graphics.Color.parseColor("#FFB6C1"),
                    priority = "Высокий",
                    estimatedTime = 25,
                    taskCount = 1,
                    projectTotalTime = 25, // 1 задача * 25 минут
                    isCompleted = false,
                    estimatedTomatoes = 1, // 1 помидор
                    completedSessions = 0,
                    totalFocusTime = 0
                )
            )
            _projects.value = testProjects
            saveProjects(testProjects)
            
            // Принудительное обновление статистики после инициализации
            // Это будет сделано автоматически при первом обращении к StatisticsManager
        }
    }
}

