package com.aeye.nextlabel.feature.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aeye.nextlabel.databinding.ProjectItemBinding
import com.aeye.nextlabel.model.dto.Project

class ProjectAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = mutableListOf<Project>()

    inner class ViewHolder(val binding: ProjectItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project) {
            binding.textViewProjectItemName.text = "${project.provider} ${project.title}"
            binding.textViewProjectItemDesc.text = project.description
            val progress = project.accepted / project.goal
            binding.progressBarProjectItem.setProgressCompat(progress, true)
            binding.textViewProjectItemProgress.text = "${progress.toString()}%"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ProjectItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun addProject(list: List<Project>) {
        val lastIndex = items.lastIndex
        items.addAll(list)
        notifyItemInserted(lastIndex)
    }
}