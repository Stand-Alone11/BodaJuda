package com.aeye.nextlabel.feature.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeye.nextlabel.global.ApplicationClass.Companion.JWT
import com.aeye.nextlabel.global.ApplicationClass.Companion.sSharedPreferences
import com.aeye.nextlabel.model.dto.Password
import com.aeye.nextlabel.util.Resource
import com.aeye.nextlabel.model.dto.UserForJoin
import com.aeye.nextlabel.model.dto.UserForLogin
import com.aeye.nextlabel.model.dto.UserForUpdate
import com.aeye.nextlabel.model.network.response.*
import com.aeye.nextlabel.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UserViewModel: ViewModel() {
    val userRepository = UserRepository()

    val joinRequestLiveData = MutableLiveData<Resource<JoinResponse>>()
    val leaveRequestLiveData = MutableLiveData<Resource<LeaveResponse>>()
    val loginRequestLiveData = MutableLiveData<Resource<LoginResponse>>()
    val updateRequestLiveData = MutableLiveData<Resource<UpdateResponse>>()
    val passwordRequestLiveData = MutableLiveData<Resource<PasswordResponse>>()
//    val profileRequestLiveData = MutableLiveData<Resource<ProfileResponse>>()

    var absoluteImgPath: String? = null

    fun join(user: UserForJoin) = viewModelScope.launch {
        joinRequestLiveData.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            joinRequestLiveData.postValue(userRepository.join(user))
        }
    }

    fun signout() = viewModelScope.launch {
        leaveRequestLiveData.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            leaveRequestLiveData.postValue(userRepository.signout())
        }
    }

    fun login(user: UserForLogin) = viewModelScope.launch {
        loginRequestLiveData.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            val resource = userRepository.login(user)

            // token 저장
            sSharedPreferences.setString(JWT, resource.data?.token.toString())
            loginRequestLiveData.postValue(resource)
        }
    }

    fun update(user: UserForUpdate) = viewModelScope.launch {
        updateRequestLiveData.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            updateRequestLiveData.postValue(userRepository.update(user, makeMultiPart(this@UserViewModel.absoluteImgPath!!)))
        }
    }

    fun updatePassword(password: Password) = viewModelScope.launch {
        passwordRequestLiveData.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            passwordRequestLiveData.postValue(userRepository.updatePassword(password))
        }
    }

//    fun getProfile(userId: Int) = viewModelScope.launch {
//        profileRequestLiveData.postValue(Resource.loading(null))
//        CoroutineScope(Dispatchers.IO).launch {
//            profileRequestLiveData.postValue(userRepository.getProfile(userId))
//        }
//    }

    private fun makeMultiPart(path: String): MultipartBody.Part {
        val imgFile = File(path)
        return MultipartBody.Part.createFormData("image", imgFile.name, imgFile.asRequestBody("image/*".toMediaType()))
    }
}