package com.aeye.nextlabel.model.dto

import com.google.gson.annotations.SerializedName

data class UserForJoin(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("nickname")
    val nickname: String,
    
    // 일단은 null로 설정해서 통신하기
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("modifiedAt")
    val modifiedAt: String? = null
)
