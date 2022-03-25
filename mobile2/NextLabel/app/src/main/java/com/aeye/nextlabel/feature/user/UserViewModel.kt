package com.aeye.nextlabel.feature.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeye.nextlabel.util.Resource
import com.aeye.nextlabel.model.dto.UserForJoin
import com.aeye.nextlabel.model.dto.UserForLogin
import com.aeye.nextlabel.repository.UserRepository
import com.aeye.nextlabel.model.network.response.JoinResponse
import com.aeye.nextlabel.model.network.response.LoginResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel: ViewModel() {
    val userRepository = UserRepository()

    val joinRequestLiveData = MutableLiveData<Resource<JoinResponse?>>()
    val loginRequestLiveData = MutableLiveData<Resource<LoginResponse>>()

    fun join(user: UserForJoin) = viewModelScope.launch {
        joinRequestLiveData.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            joinRequestLiveData.postValue(userRepository.join(user))
        }
    }

    fun login(user: UserForLogin) = viewModelScope.launch {
        loginRequestLiveData.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            loginRequestLiveData.postValue(userRepository.login(user))
        }
    }
}