package com.aeye.nextlabel.feature.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aeye.nextlabel.databinding.ProjectItemBinding
import com.aeye.nextlabel.model.dto.Project

class ProjectAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = mutableListOf<Project>()
    lateinit var itemClickListener: ItemClickListener

    inner class ViewHolder(val binding: ProjectItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            if(this@ProjectAdapter::itemClickListener.isInitialized) {
                binding.root.setOnClickListener {
                    itemClickListener.onClick(it)
                }
            }
        }

        fun bind(project: Project) {
            binding.root.tag = project
            binding.textViewProjectItemName.text = "${project.provider} ${project.title}"
            binding.textViewProjectItemDesc.text = project.description
            val progress = (project.accepted.toFloat() / project.goal * 100).toInt()
            binding.progressBarProjectItem.setProgressCompat(progress, true)
            binding.textViewProjectItemProgress.text = "${progress}%"
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
        if(lastIndex == -1) {
            items.addAll(list)
            notifyItemInserted(0)
        } else {
            val sublist = list.subList(lastIndex + 1, list.lastIndex)
            items.addAll(sublist)
            notifyItemInserted(lastIndex + 1)
        }
    }

    interface ItemClickListener {
        fun onClick(view: View)
    }
}