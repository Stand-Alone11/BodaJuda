package com.aeye.nextlabel.feature.main.community

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.FragmentLeaderBoardBinding
import com.aeye.nextlabel.feature.common.BaseFragment
import com.aeye.nextlabel.feature.main.CommunityViewModel
import com.aeye.nextlabel.model.dto.RankUser
import com.aeye.nextlabel.util.Status

class LeaderBoardFragment : BaseFragment<FragmentLeaderBoardBinding>(FragmentLeaderBoardBinding::bind, R.layout.fragment_leader_board) {
    private val TAG = "LeaderBoardFragment_debuk"

    private val communityViewModel: CommunityViewModel by activityViewModels()
    private lateinit var leaderAdapter: LeaderBoardAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initLiveDataObserver()
        requestLeaderBoard()
    }

    private fun init() {
        leaderAdapter = LeaderBoardAdapter()
        binding.recyclerViewLeaderF.apply {
            adapter = leaderAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    private fun initLiveDataObserver() {
        communityViewModel.leaderBoardResponseLiveData.observe(requireActivity()) {
            if(communityViewModel.isFirstLoaded) {
                // 두번째 로딩부터
                when(it.status) {
                    Status.SUCCESS -> {
                        // TODO: adapter의 로딩 제거
                    }
                    Status.ERROR -> {
                        // TODO: adapter의 로딩 제거
                    }
                    Status.LOADING -> {
                        // TODO: adapter의 로딩 추가
                    }
                }
            } else {
                // 첫번째 로딩
                when(it.status) {
                    Status.SUCCESS -> {
                        // TODO: 전체 플레이스 홀더 제거
                    }
                    Status.ERROR -> {
                        // TODO: 전체 플레이스 홀더 제거
                    }
                    Status.LOADING -> {
                        // TODO: 전체 플레이스 홀더 추가
                    }
                }
            }
            
        }

        communityViewModel.leaderBoardItems.observe(requireActivity()) {
            updateDiff(it)
        }
    }

    /** 첫 Fragment 생성 시 request*/
    private fun requestLeaderBoard() {
        if(!communityViewModel.isFirstLoaded) {
            communityViewModel.getLeaderBoard()
        }
    }

    private fun updateDiff(list: List<RankUser>) {
        leaderAdapter.addUsers(list.subList(leaderAdapter.itemCount, list.lastIndex + 1))
    }
}