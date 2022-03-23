package com.aeye.nextlabel.repository

import com.aeye.nextlabel.global.ApplicationClass
import com.aeye.nextlabel.util.Resource
import com.aeye.nextlabel.model.network.api.UserApi
import com.aeye.nextlabel.model.dto.UserJoin
import com.aeye.nextlabel.model.dto.UserLeave
import com.aeye.nextlabel.model.dto.UserLogin
import com.aeye.nextlabel.model.network.response.JoinResponse
import com.aeye.nextlabel.model.network.response.LeaveResponse
import com.aeye.nextlabel.model.network.response.LoginResponse
import java.lang.Exception

class UserRepository {
    var userApi: UserApi = ApplicationClass.sRetrofit.create(UserApi::class.java)

    suspend fun join(user: UserJoin): Resource<JoinResponse> {
        return try {
            val response = userApi.join(user)
            if(response.isSuccessful) {
                return if(response.code() == 200 && response.body()!!.output == 1) {
                    Resource.success(response.body()!!)
                } else {
                    Resource.error(null, response.body()!!.message!!)
                }
            } else {
                Resource.error(null, "알 수 없는 오류입니다.")
            }
        } catch (e: Exception) {
            Resource.error(null, "서버와 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.")
        }
    }

    suspend fun login(user: UserLogin): Resource<LoginResponse> {
        return try {
            val response = userApi.login(user)
            if(response.isSuccessful) {
                return if(response.code() == 200 && response.body()!!.output == 1) {
                    Resource.success(response.body()!!)
                } else {
                    Resource.error(null, response.body()!!.message!!)
                }
            } else {
                Resource.error(null, "알 수 없는 오류입니다.")
            }
        } catch (e: Exception) {
            Resource.error(null, "서버와 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.")
        }
    }

    suspend fun update(user: UserJoin): Resource<JoinResponse> {
        return try {
            val response = userApi.update(user)
            if(response.isSuccessful) {
                return if(response.code() == 200 && response.body()!!.output == 1) {
                    Resource.success(response.body()!!)
                } else {
                    Resource.error(null, response.body()!!.message!!)
                }
            } else {
                Resource.error(null, "알 수 없는 오류입니다.")
            }
        } catch (e: Exception) {
            Resource.error(null, "서버와 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.")
        }
    }

    suspend fun leave(user: UserLeave): Resource<LeaveResponse> {
        return try {
            val response = userApi.leave(user)
            if(response.isSuccessful) {
                return if(response.code() == 200 && response.body()!!.output == 1) {
                    Resource.success(response.body()!!)
                } else {
                    Resource.error(null, response.body()!!.message!!)
                }
            } else {
                Resource.error(null, "알 수 없는 오류입니다.")
            }
        } catch (e: Exception) {
            Resource.error(null, "서버와 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.")
        }
    }
}