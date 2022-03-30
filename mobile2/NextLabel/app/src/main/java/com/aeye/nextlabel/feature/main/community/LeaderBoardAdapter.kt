package com.aeye.nextlabel.feature.main.community

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.LeaderboardItemBinding
import com.aeye.nextlabel.model.dto.RankUser

class LeaderBoardAdapter: RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder>() {
    private val items: MutableList<RankUser> = mutableListOf()
    private lateinit var binding: LeaderboardItemBinding

    inner class ViewHolder(val binding: LeaderboardItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(user: RankUser) {
            binding.user = user
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.leaderboard_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun addUsers(list: List<RankUser>) {
        val lastIndex = items.lastIndex
        items.addAll(list)
        notifyItemInserted(lastIndex)
    }
}