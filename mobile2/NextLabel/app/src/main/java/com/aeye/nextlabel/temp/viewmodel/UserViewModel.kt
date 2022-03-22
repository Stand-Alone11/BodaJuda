package com.aeye.nextlabel.temp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aeye.nextlabel.temp.Resource
import com.aeye.nextlabel.temp.dto.UserJoin
import com.aeye.nextlabel.temp.dto.UserLeave
import com.aeye.nextlabel.temp.dto.UserLogin
import com.aeye.nextlabel.temp.repository.UserRepository
import com.aeye.nextlabel.temp.response.JoinResponse
import com.aeye.nextlabel.temp.response.LeaveResponse
import com.aeye.nextlabel.temp.response.LoginResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel: ViewModel() {
    val userRepository = UserRepository()

    val joinRequestLiveData = MutableLiveData<Resource<JoinResponse?>>()
    val loginRequestLiveData = MutableLiveData<Resource<LoginResponse?>>()
    val leaveRequestLiveData = MutableLiveData<Resource<LeaveResponse?>>()

    fun join(user: UserJoin) = CoroutineScope(Dispatchers.Main).launch {
        joinRequestLiveData.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            joinRequestLiveData.postValue(userRepository.join(user))
        }
    }

    fun login(user: UserLogin) = CoroutineScope(Dispatchers.Main).launch {
        loginRequestLiveData.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            loginRequestLiveData.postValue(userRepository.login(user))
        }
    }

    fun update(user: UserJoin) = CoroutineScope(Dispatchers.Main).launch {
        joinRequestLiveData.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            joinRequestLiveData.postValue(userRepository.update(user))
        }
    }

    fun leave(user: UserLeave) = CoroutineScope(Dispatchers.Main).launch {
        leaveRequestLiveData.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            leaveRequestLiveData.postValue(userRepository.leave(user))
        }
    }
}