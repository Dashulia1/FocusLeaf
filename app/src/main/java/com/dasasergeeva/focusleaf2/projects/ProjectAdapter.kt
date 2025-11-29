package com.dasasergeeva.focusleaf2.projects

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dasasergeeva.focusleaf2.R
import com.dasasergeeva.focusleaf2.databinding.ItemProjectBinding
import com.dasasergeeva.focusleaf2.models.Project

/**
 * Адаптер для RecyclerView со списком проектов
 */
class ProjectAdapter(
    private val projects: MutableList<Project>,
    private val onProjectClick: (Project) -> Unit
) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    class ProjectViewHolder(
        private val binding: ItemProjectBinding,
        private val onProjectClick: (Project) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(project: Project) {
            binding.apply {
                // ТОЛЬКО название проекта
                tvProjectName.text = project.name
                
                // Установка цвета фона карточки
                root.setCardBackgroundColor(project.color)
                
                // Обработка клика
                root.setOnClickListener {
                    onProjectClick(project)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val binding = ItemProjectBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProjectViewHolder(binding, onProjectClick)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.bind(projects[position])
    }

    override fun getItemCount(): Int = projects.size

    /**
     * Обновление списка проектов
     */
    fun updateProjects(newProjects: List<Project>) {
        projects.clear()
        projects.addAll(newProjects)
        notifyDataSetChanged()
    }

    /**
     * Добавление нового проекта
     */
    fun addProject(project: Project) {
        projects.add(project)
        notifyItemInserted(projects.size - 1)
    }

    /**
     * Удаление проекта
     */
    fun removeProject(projectId: String) {
        val index = projects.indexOfFirst { it.id == projectId }
        if (index != -1) {
            projects.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}

