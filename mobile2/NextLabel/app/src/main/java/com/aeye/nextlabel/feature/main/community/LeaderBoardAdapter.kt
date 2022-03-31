package com.aeye.nextlabel.feature.main.community

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.LeaderboardItemBinding
import com.aeye.nextlabel.databinding.LeaderboardItemLoadingBinding
import com.aeye.nextlabel.model.dto.RankUser

class LeaderBoardAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items: MutableList<RankUser> = mutableListOf()
    private lateinit var binding: LeaderboardItemBinding
    private val TYPE_LOADING = -1

    inner class ViewHolder(val binding: LeaderboardItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(user: RankUser) {
            binding.user = user
        }
    }

    inner class LoadingViewHolder(binding: LeaderboardItemLoadingBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            TYPE_LOADING -> LoadingViewHolder(LeaderboardItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> {
                binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.leaderboard_item, parent, false)
                return ViewHolder(binding)
            }
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ViewHolder) holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun addUsers(list: List<RankUser>) {
        val lastIndex = items.lastIndex
        items.addAll(list)
        notifyItemInserted(lastIndex)
    }

    override fun getItemViewType(position: Int): Int {
        if(items[position].id == -1) return TYPE_LOADING
        return super.getItemViewType(position)
    }

    fun showLoading() {
        items.add(RankUser(id = -1, 0, "", 0))
        notifyItemInserted(items.lastIndex)
    }

    fun dismissLoading() {
        val lastIndex = items.lastIndex
        if(lastIndex > -1) {
            items.removeAt(lastIndex)
            notifyItemRemoved(lastIndex)
        }
    }
}