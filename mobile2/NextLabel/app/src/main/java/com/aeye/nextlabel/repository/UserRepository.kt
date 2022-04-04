package com.aeye.nextlabel.repository

import android.util.Log
import com.aeye.nextlabel.feature.common.BaseResponse
import com.aeye.nextlabel.global.ApplicationClass
import com.aeye.nextlabel.model.dto.Password
import com.aeye.nextlabel.util.Resource
import com.aeye.nextlabel.model.network.api.UserApi
import com.aeye.nextlabel.model.dto.UserForJoin
import com.aeye.nextlabel.model.dto.UserForLogin
import com.aeye.nextlabel.model.dto.UserForUpdate
import com.aeye.nextlabel.model.network.response.*
import okhttp3.MultipartBody
import java.lang.Exception

class UserRepository {
    private val TAG = "UserRepository_debug"
    var userApi: UserApi = ApplicationClass.sRetrofit.create(UserApi::class.java)

    suspend fun join(user: UserForJoin): Resource<BaseResponse> {
        return try {
            val response = userApi.join(user)
            if (response.isSuccessful) {
                Resource.success(response.body()!!)
            } else {
                when(response.code()) {
                    400 -> Resource.error(null, "Validation 에러")
                    else -> Resource.error(null, "알 수 없는 오류입니다.")
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "join: $e")
            Resource.error(null, "서버와 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.")
        }
    }

    suspend fun signout(): Resource<BaseResponse> {
        return try {
            val response = userApi.signout()
            if (response.isSuccessful) {
                Resource.success(response.body()!!)
            } else {
                when(response.code()) {
                    401 -> Resource.error(null, "Token 인증 실패")
                    else -> Resource.error(null, "알 수 없는 오류입니다.")
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "join: $e")
            Resource.error(null, "서버와 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.")
        }
    }

    suspend fun login(user: UserForLogin): Resource<LoginResponse> {
        return try {
            val response = userApi.login(user)
            if (response.isSuccessful) {
                Resource.success(response.body()!!)
            } else {
                when(response.code()) {
                    401 -> Resource.error(null, "비밀번호가 틀렸습니다.")
                    404 -> Resource.error(null, "존재하지 않는 계정입니다.")
                    else -> Resource.error(null, "알 수 없는 오류입니다.")
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "join: $e")
            Resource.error(null, "서버와 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.")
        }
    }

    suspend fun update(user: UserForUpdate, image: MultipartBody.Part): Resource<BaseResponse> {
        return try {
            val email = getBody("email", user.email)
            val nickname = getBody("nickName", user.nickname)
            val response = userApi.update(email=email, nickName=nickname, image=image)

            if (response.isSuccessful) {
                Resource.success(response.body()!!)
            } else {
                when(response.code()) {
                    400 -> Resource.error(null, "Validation 에러")
                    401 -> Resource.error(null, "Token 인증 실패")
                    else -> Resource.error(null, "알 수 없는 오류입니다.")
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "join: $e")
            Resource.error(null, "서버와 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.")
        }
    }

    suspend fun updatePassword(password: Password): Resource<BaseResponse> {
        return try {
            val response = userApi.updatePassword(password)
            if (response.isSuccessful) {
                Resource.success(response.body()!!)
            } else {
                when(response.code()) {
                    401 -> Resource.error(null, "Token 인증 실패")
                    406 -> Resource.error(null, "비밀번호 Validation 에러")
                    else -> Resource.error(null, "알 수 없는 오류입니다.")
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "join: $e")
            Resource.error(null, "서버와 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.")
        }
    }

    suspend fun getProfile(userId: Int): Resource<ProfileResponse> {
        return try {
            val response = userApi.getProfile(userId)
            if (response.isSuccessful) {
                Resource.success(response.body()!!)
            } else {
                when(response.code()) {
                    404 -> Resource.error(null, "해당 유저 없음")
                    else -> Resource.error(null, "알 수 없는 오류입니다.")
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "join: $e")
            Resource.error(null, "서버와 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.")
        }
    }

    private fun getBody(name: String, value: Any): MultipartBody.Part {
        return MultipartBody.Part.createFormData(name, value.toString())
    }
}