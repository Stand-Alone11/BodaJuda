package com.aeye.nextlabel.model.network.api

import com.aeye.nextlabel.feature.common.BaseResponse
import com.aeye.nextlabel.model.dto.*
import com.aeye.nextlabel.model.network.response.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface UserApi {
    // 1. Sign Up
    @POST("/accounts/signup")
    suspend fun join(@Body user: UserForJoin): Response<BaseResponse>

    // 2. Sign Out
    @DELETE("/accounts/signout")
    suspend fun signout(): Response<BaseResponse>

    // 3. Log In (일반)
    @POST("/accounts/login")
    suspend fun login(@Body user: UserForLogin): Response<LoginResponse>

    // 4. Log In (소셜)

    // 5. 회원정보 수정(without password)
    @Multipart
    @POST("/accounts/update/userinfo")
    suspend fun update(@Part email: MultipartBody.Part,
                       @Part nickName: MultipartBody.Part,
                       @Part image: MultipartBody.Part): Response<BaseResponse>

    // 6. 비밀번호 수정
    @POST("/accounts/update/password")
    suspend fun updatePassword(@Body password: Password): Response<BaseResponse>

    // 7. 프로필
    @GET("/accounts/info/{id}")
    suspend fun getProfile(@Path("id") id: Int): Response<ProfileResponse>
}