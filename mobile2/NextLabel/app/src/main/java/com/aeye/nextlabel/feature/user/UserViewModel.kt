package com.aeye.nextlabel.feature.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeye.nextlabel.feature.common.BaseResponse
import com.aeye.nextlabel.global.ApplicationClass.Companion.JWT
import com.aeye.nextlabel.global.ApplicationClass.Companion.sSharedPreferences
import com.aeye.nextlabel.model.dto.Password
import com.aeye.nextlabel.util.Resource
import com.aeye.nextlabel.model.dto.UserForJoin
import com.aeye.nextlabel.model.dto.UserForLogin
import com.aeye.nextlabel.model.dto.UserForUpdate
import com.aeye.nextlabel.model.network.response.*
import com.aeye.nextlabel.repository.UserRepository
import com.aeye.nextlabel.util.LoginUtil.USER_EMAIL
import com.aeye.nextlabel.util.LoginUtil.USER_IMG_ACCEPT
import com.aeye.nextlabel.util.LoginUtil.USER_IMG_DENY
import com.aeye.nextlabel.util.LoginUtil.USER_IMG_TOTAL
import com.aeye.nextlabel.util.LoginUtil.USER_IMG_URL
import com.aeye.nextlabel.util.LoginUtil.USER_IMG_WAIT
import com.aeye.nextlabel.util.LoginUtil.USER_NICKNAME
import com.aeye.nextlabel.util.LoginUtil.USER_RANK
import com.aeye.nextlabel.util.LoginUtil.getUserId
import com.aeye.nextlabel.util.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UserViewModel: ViewModel() {
    val userRepository = UserRepository()

    val joinRequestLiveData = MutableLiveData<Resource<BaseResponse>>()
    val leaveRequestLiveData = MutableLiveData<Resource<BaseResponse>>()
    val loginRequestLiveData = MutableLiveData<Resource<LoginResponse>>()
    val updateRequestLiveData = MutableLiveData<Resource<BaseResponse>>()
    val passwordRequestLiveData = MutableLiveData<Resource<BaseResponse>>()
    val profileRequestLiveData = MutableLiveData<Resource<ProfileResponse>>()


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
            if (resource.status == Status.SUCCESS) {
                // token 저장
                sSharedPreferences.setString(JWT, resource.data?.token.toString())
                getUserId()
            }
            loginRequestLiveData.postValue(resource)
        }
    }

    fun update(user: UserForUpdate, absolutePath: String) = viewModelScope.launch {
        updateRequestLiveData.postValue(Resource.loading(null))
        val imageMultiPartBody = makeMultiPartBody("image", absolutePath, "image/*")

        CoroutineScope(Dispatchers.IO).launch {
            updateRequestLiveData.postValue(userRepository.update(user, imageMultiPartBody))
        }
    }

    fun updatePassword(password: Password) = viewModelScope.launch {
        passwordRequestLiveData.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            passwordRequestLiveData.postValue(userRepository.updatePassword(password))
        }
    }

    fun getProfile(userId: Int) = viewModelScope.launch {
        profileRequestLiveData.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            val resource = userRepository.getProfile(userId)
            if (resource.status == Status.SUCCESS) {
                // userInfo 저장
                sSharedPreferences.setString(USER_IMG_URL, resource.data?.imgUrl.toString())
                sSharedPreferences.setString(USER_EMAIL, resource.data?.email.toString())
                sSharedPreferences.setString(USER_NICKNAME, resource.data?.nickname.toString())
                sSharedPreferences.setString(USER_IMG_TOTAL, resource.data?.imageTotal.toString())
                sSharedPreferences.setString(USER_IMG_ACCEPT, resource.data?.imageAccept.toString())
                sSharedPreferences.setString(USER_IMG_DENY, resource.data?.imageDeny.toString())
                sSharedPreferences.setString(USER_IMG_WAIT, resource.data?.imageWait.toString())
                sSharedPreferences.setString(USER_RANK, resource.data?.rank.toString())
            }

            profileRequestLiveData.postValue(resource)
        }
    }

    fun makeMultiPartBody(name: String, absolutePath: String, mediaType: String): MultipartBody.Part {
        val file = File(absolutePath)
        return MultipartBody.Part.createFormData(name, file.name, file.asRequestBody(mediaType.toMediaType()))
    }
}