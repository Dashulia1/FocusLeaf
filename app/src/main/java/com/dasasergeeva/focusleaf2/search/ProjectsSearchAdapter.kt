package com.dasasergeeva.focusleaf2.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dasasergeeva.focusleaf2.databinding.ItemProjectSearchBinding
import com.dasasergeeva.focusleaf2.models.Project

/**
 * Адаптер для RecyclerView со списком проектов в режиме поиска
 */
class ProjectsSearchAdapter(
    private val onProjectChecked: (Project, Boolean) -> Unit
) : ListAdapter<Project, ProjectsSearchAdapter.ProjectSearchViewHolder>(ProjectDiffCallback()) {

    class ProjectSearchViewHolder(
        val binding: ItemProjectSearchBinding,
        private val onProjectChecked: (Project, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(project: Project) {
            binding.apply {
                tvProjectName.text = project.name
                
                // Убираем предыдущий listener чтобы избежать бесконечного цикла
                cbProject.setOnCheckedChangeListener(null)
                cbProject.isChecked = project.isCompleted

                // Обработка изменения чекбокса
                cbProject.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked != project.isCompleted) {
                        // Уведомление о изменении
                        onProjectChecked(project, isChecked)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectSearchViewHolder {
        val binding = ItemProjectSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProjectSearchViewHolder(binding, onProjectChecked)
    }

    override fun onBindViewHolder(holder: ProjectSearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * DiffUtil Callback для оптимизации обновления списка
     */
    class ProjectDiffCallback : DiffUtil.ItemCallback<Project>() {
        override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean {
            return oldItem == newItem
        }
    }
}

