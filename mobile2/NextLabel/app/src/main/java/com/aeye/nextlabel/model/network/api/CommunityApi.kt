package com.aeye.nextlabel.model.network.api

import com.aeye.nextlabel.model.dto.UserForJoin
import com.aeye.nextlabel.model.dto.UserForLogin
import com.aeye.nextlabel.model.network.response.JoinResponse
import com.aeye.nextlabel.model.network.response.LoginResponse
import com.aeye.nextlabel.model.network.response.ProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CommunityApi {
    // 1. Sign Up
    @POST("/accounts/signup")
    suspend fun join(@Body user: UserForJoin): Response<JoinResponse>

    // 2. Sign Out

    // 3. Log In (일반)
    @POST("/accounts/login")
    suspend fun login(@Body user: UserForLogin): Response<LoginResponse>

    // 4. Log In (소셜)

    // 5. 회원정보 수정(without password)

    // 6. 비밀번호 수정

    // 7. 프로필
    @GET("/accounts/info/")
    suspend fun getProfile(@Query("id") id: Int): Response<ProfileResponse>
}