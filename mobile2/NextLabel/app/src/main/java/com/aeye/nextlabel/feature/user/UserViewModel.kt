package com.aeye.nextlabel.feature.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aeye.nextlabel.util.Resource
import com.aeye.nextlabel.model.dto.UserJoin
import com.aeye.nextlabel.model.dto.UserLeave
import com.aeye.nextlabel.model.dto.UserLogin
import com.aeye.nextlabel.repository.UserRepository
import com.aeye.nextlabel.model.network.response.JoinResponse
import com.aeye.nextlabel.model.network.response.LeaveResponse
import com.aeye.nextlabel.model.network.response.LoginResponse
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