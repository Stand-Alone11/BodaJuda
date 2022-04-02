package com.aeye.nextlabel.feature.main.community

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aeye.nextlabel.R
import com.aeye.nextlabel.databinding.FragmentLeaderBoardBinding
import com.aeye.nextlabel.feature.common.BaseFragment
import com.aeye.nextlabel.feature.main.CommunityViewModel
import com.aeye.nextlabel.global.ApplicationClass
import com.aeye.nextlabel.model.dto.RankUser
import com.aeye.nextlabel.util.Status
import com.bumptech.glide.Glide

class LeaderBoardFragment : BaseFragment<FragmentLeaderBoardBinding>(FragmentLeaderBoardBinding::bind, R.layout.fragment_leader_board) {
    private val TAG = "LeaderBoardFragment_debuk"

    // TODO: adpater clicklistener 구현 
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
            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val lastVisibleItemPosition =
                        (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                    val itemTotalCount = recyclerView.adapter!!.itemCount-1

                    // 스크롤이 끝에 도달했는지 확인
                    if (!recyclerView.canScrollVertically(RecyclerView.VERTICAL) && lastVisibleItemPosition == itemTotalCount) {
                        communityViewModel.getLeaderBoard()
                    }
                }
            })
        }
    }

    private fun initLiveDataObserver() {
        communityViewModel.leaderBoardResponseLiveData.observe(viewLifecycleOwner) {
            if(communityViewModel.isFirstLoaded) {
                // 두번째 로딩부터
                when(it.status) {
                    Status.SUCCESS -> {
                        // TODO: adapter의 로딩 제거
                        leaderAdapter.dismissLoading()
                        Log.d(TAG, "initLiveDataObserver: second success")
                    }
                    Status.ERROR -> {
                        // TODO: adapter의 로딩 제거
                        leaderAdapter.dismissLoading()
                    }
                    Status.LOADING -> {
                        // TODO: adapter의 로딩 추가
                        leaderAdapter.showLoading()
                    }
                }
            } else {
                // 첫번째 로딩
                when(it.status) {
                    Status.SUCCESS -> {
                        // TODO: 전체 플레이스 홀더 제거
                        communityViewModel.isFirstLoaded = true
                        Log.d(TAG, "initLiveDataObserver: first success")
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

        communityViewModel.leaderBoardItems.observe(viewLifecycleOwner) {
            updateDiff(it)
        }

        communityViewModel.top3LiveData.observe(viewLifecycleOwner) { list ->
            list.forEach {
                updateTop3(it)
            }
        }
    }

    private fun updateTop3(user: RankUser) {
        val imageViews = arrayOf(binding.imageViewLeaderFFirst, binding.imageViewLeaderFSecond, binding.imageViewLeaderFThird)
        val names = arrayOf(binding.textViewLeaderFFirstName, binding.textViewLeaderFSecondName, binding.textViewLeaderFThirdName)
        val contributes = arrayOf(binding.textViewLeaderFFirstAccepted, binding.textViewLeaderFSecondAccepted, binding.textViewLeaderFThirdAccepted)

        if(user.rank < 4) {
            val index = user.rank - 1
            Glide.with(this).load("${ApplicationClass.IMAGE_BASE_URL}${user.profileImage}").error(R.drawable.bottom_nav_profile).circleCrop().into(imageViews[index])
            names[index].text = user.nickName
            contributes[index].text = user.imageAccepted.toString()
        }
    }

    /** 첫 Fragment 생성 시 request*/
    private fun requestLeaderBoard() {
        if(!communityViewModel.isFirstLoaded) {
            Log.d(TAG, "requestLeaderBoard: first request")
            communityViewModel.getLeaderBoard()
        }
    }

    private fun updateDiff(list: List<RankUser>) {
        leaderAdapter.addUsers(list.subList(leaderAdapter.itemCount, list.lastIndex + 1))
    }
}