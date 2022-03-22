package com.aeye.nextlabel.temp.viewmodel

import androidx.lifecycle.ViewModel
import com.aeye.nextlabel.temp.Resource
import com.aeye.nextlabel.temp.dto.UserJoin
import com.aeye.nextlabel.temp.dto.UserLeave
import com.aeye.nextlabel.temp.dto.UserLogin
import com.aeye.nextlabel.temp.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel: ViewModel() {
    val userRepository = UserRepository()

    fun join(user: UserJoin) = CoroutineScope(Dispatchers.Main).launch {
        joinRequestLiveData.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            joinRequestLiveData.postValue(userRepository.join(user))
        }
    }

    fun login(user: UserLogin) = CoroutineScope(Dispatchers.Main).launch {
        joinRequestLiveData.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            joinRequestLiveData.postValue(userRepository.login(user))
        }
    }

    fun update(user: UserJoin) = CoroutineScope(Dispatchers.Main).launch {
        joinRequestLiveData.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            joinRequestLiveData.postValue(userRepository.update(user))
        }
    }

    fun leave(user: UserLeave) = CoroutineScope(Dispatchers.Main).launch {
        joinRequestLiveData.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            joinRequestLiveData.postValue(userRepository.leave(user))
        }
    }
}