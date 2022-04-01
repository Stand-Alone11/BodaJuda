package com.aeye.nextlabel.feature.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeye.nextlabel.model.dto.RankUser
import com.aeye.nextlabel.repository.CommunityRepository
import com.aeye.nextlabel.util.Resource
import com.aeye.nextlabel.util.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommunityViewModel: ViewModel() {
    private val communityRepository = CommunityRepository()
    private var page = 0
    private val size = 20
    // 리더보드의 마지막 체크
    private var isLast = false
    // 첫 로딩 여부 체크
    var isFirstLoaded = false

    private val _leaderBoardResponseLiveData = MutableLiveData<Resource<List<RankUser>>>()
    val leaderBoardResponseLiveData: LiveData<Resource<List<RankUser>>>
        get() = _leaderBoardResponseLiveData

    // 요청으로 가져오는 page 데이터
    private val _leaderBoardItems = MutableLiveData<List<RankUser>>()
    val leaderBoardItems: LiveData<List<RankUser>>
        get() = _leaderBoardItems

    //1, 2, 3위
    private val _top3LiveData = MutableLiveData<List<RankUser>>()
    val top3LiveData: LiveData<List<RankUser>>
        get() = _top3LiveData

    fun getLeaderBoard() = viewModelScope.launch {
        if(!isLast) {
            _leaderBoardResponseLiveData.postValue(Resource.loading(null))
            withContext(Dispatchers.IO) {
                val response = communityRepository.getLeaderBoard(page, size)
                if(response.status == Status.SUCCESS) {
                    response.data?.let {
                        updateStatus(it)
                    }
                }
                _leaderBoardResponseLiveData.postValue(response)
            }
        }
    }

    /** pagination 처리를 위한 status 관리
     *  리스트가 비어있으면 isLast = true
     */
    private fun updateStatus(list: List<RankUser>) {
        if(list.size < 20) {
            isLast = true
        }
        page++
        updateLiveData(list)
    }

    private fun updateLiveData(list: List<RankUser>) {
        val origin = _leaderBoardItems.value?.toMutableList()?: mutableListOf()
        if(!isFirstLoaded && list.isNotEmpty()) {
            val top3List = mutableListOf<RankUser>()

            try {
                for(i in 0..2) {
                    if(list[i].rank < 4) {
                        top3List.add(list[i])
                    }
                }
            } catch (e: Exception) {

            } finally {
                _top3LiveData.postValue(top3List)
            }

        } else {
            origin.addAll(list)
            _leaderBoardItems.postValue(origin)
        }
    }
}