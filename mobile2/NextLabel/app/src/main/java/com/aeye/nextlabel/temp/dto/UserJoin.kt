package com.aeye.nextlabel.temp.dto

import com.google.gson.annotations.SerializedName

// 모든 키는 임시로 작성한 것들
// 차후 API 개발이 완료되면 수정할 예정
data class UserJoin(
    @SerializedName("id")
    val id: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("password_confirmation")
    val passwordConformation: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("nickname")
    val nickname: String
)
