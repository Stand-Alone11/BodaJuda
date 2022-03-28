package com.aeye.nextlabel.model.network.api

import com.aeye.nextlabel.model.dto.Password
import com.aeye.nextlabel.model.dto.UserForJoin
import com.aeye.nextlabel.model.dto.UserForLogin
import com.aeye.nextlabel.model.dto.UserForUpdate
import com.aeye.nextlabel.model.network.response.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface UserApi {
    // 1. Sign Up <- 동작 확인
    @POST("/accounts/signup")
    suspend fun join(@Body user: UserForJoin): Response<JoinResponse>

    // 2. Sign Out <- Postman으로 확인
    @DELETE("/accounts/signout")
    suspend fun signout(): Response<LeaveResponse>

    // 3. Log In (일반) <- 동작 확인
    @POST("/accounts/login")
    suspend fun login(@Body user: UserForLogin): Response<LoginResponse>

    // 4. Log In (소셜)

    // 5. 회원정보 수정(without password)
    @Multipart
    @POST("/accounts/update/userinfo")
    suspend fun update(@Part email: MultipartBody.Part,
                       @Part nickName: MultipartBody.Part,
                       @Part image: MultipartBody.Part): Response<UpdateResponse>

    // 6. 비밀번호 수정
    @POST("/accounts/update/password")
    suspend fun updatePassword(@Body pasword: Password): Response<PasswordResponse>

    // 7. 프로필
//    @GET("/accounts/info/{id}")
//    suspend fun getProfile(@Path("id") id: Int): Response<ProfileResponse>
}