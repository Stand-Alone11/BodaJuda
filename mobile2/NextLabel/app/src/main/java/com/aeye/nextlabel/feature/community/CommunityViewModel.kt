package com.aeye.nextlabel.feature.community

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeye.nextlabel.model.network.response.LeaderBoardResponse
import com.aeye.nextlabel.repository.CommunityRepository
import com.aeye.nextlabel.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommunityViewModel: ViewModel() {

    val communityRepository = CommunityRepository()

    val leaderBoardLiveData = MutableLiveData<Resource<LeaderBoardResponse>>()

    fun getLeaderBoard(page: Int, size: Int) = viewModelScope.launch {
        leaderBoardLiveData.postValue(Resource.loading(null))
        withContext(Dispatchers.IO) {
            leaderBoardLiveData.postValue(communityRepository.getLeaderBoard(page, size))
        }
    }
}