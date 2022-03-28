package com.aeye.nextlabel.feature.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeye.nextlabel.global.ApplicationClass.Companion.JWT
import com.aeye.nextlabel.global.ApplicationClass.Companion.sSharedPreferences
import com.aeye.nextlabel.util.Resource
import com.aeye.nextlabel.model.dto.UserForJoin
import com.aeye.nextlabel.model.dto.UserForLogin
import com.aeye.nextlabel.model.network.response.*
import com.aeye.nextlabel.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel: ViewModel() {
    val userRepository = UserRepository()

    val joinRequestLiveData = MutableLiveData<Resource<JoinResponse>>()
//    val leaveRequestLiveData = MutableLiveData<Resource<LeaveResponse>>()
    val loginRequestLiveData = MutableLiveData<Resource<LoginResponse>>()
//    val profileRequestLiveData = MutableLiveData<Resource<ProfileResponse>>()
    val leaderBoardLiveData = MutableLiveData<Resource<LeaderBoardResponse>>()

    fun join(user: UserForJoin) = viewModelScope.launch {
        joinRequestLiveData.postValue(Resource.loading(null))
        withContext(Dispatchers.IO) {
            joinRequestLiveData.postValue(userRepository.join(user))
        }
    }

//    fun signout() = viewModelScope.launch {
//        leaveRequestLiveData.postValue(Resource.loading(null))
//        CoroutineScope(Dispatchers.IO).launch {
//            leaveRequestLiveData.postValue(userRepository.signout())
//        }
//    }

    fun login(user: UserForLogin) = viewModelScope.launch {
        loginRequestLiveData.postValue(Resource.loading(null))
        withContext(Dispatchers.IO) {
            val resource = userRepository.login(user)

            // token 저장
            sSharedPreferences.setString(JWT, resource.data?.token.toString())
            loginRequestLiveData.postValue(resource)
        }
    }

//    fun getProfile(userId: Int) = viewModelScope.launch {
//        profileRequestLiveData.postValue(Resource.loading(null))
//        CoroutineScope(Dispatchers.IO).launch {
//            profileRequestLiveData.postValue(userRepository.getProfile(userId))
//        }
//    }

    fun getLeaderBoard(page: Int, size: Int) = viewModelScope.launch {
        leaderBoardLiveData.postValue(Resource.loading(null))
        withContext(Dispatchers.IO) {
            leaderBoardLiveData.postValue(userRepository.getLeaderBoard(page, size))
        }
    }
}