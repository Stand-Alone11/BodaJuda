package com.aeye.nextlabel.model.network.api

import com.aeye.nextlabel.model.dto.UserJoin
import com.aeye.nextlabel.model.dto.UserLeave
import com.aeye.nextlabel.model.dto.UserLogin
import com.aeye.nextlabel.model.network.response.JoinResponse
import com.aeye.nextlabel.model.network.response.LeaveResponse
import com.aeye.nextlabel.model.network.response.LoginResponse
import retrofit2.Response
import retrofit2.http.*

interface UserApi {
    // 1. Sign Up
    @POST("/accounts/signup")
    suspend fun join(@Body user: UserForJoin): Response<JoinResponse>

    // 2. Sign Out
    @POST("/accounts/signout")
    suspend fun emailLogin(@Body user: UserForLogin): Response<LoginResponse>

    // 3. Log In (일반)
    @POST("/accounts/login")
    suspend fun emailLogin(@Body user: UserForLogin): Response<LoginResponse>

    // 4. Log In (소셜)


}