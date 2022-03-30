package com.aeye.nextlabel.feature.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeye.nextlabel.model.network.response.LeaderBoardResponse
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
    private var isLast = false

    private val _leaderBoardResponseLiveData = MutableLiveData<Resource<LeaderBoardResponse>>()
    val leaderBoardResponseLiveData: LiveData<Resource<LeaderBoardResponse>>
        get() = _leaderBoardResponseLiveData

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
    private fun updateStatus(response: LeaderBoardResponse) {
        if(response.users.isNotEmpty()) {
            page++
        } else {
            isLast = true
        }
    }
}