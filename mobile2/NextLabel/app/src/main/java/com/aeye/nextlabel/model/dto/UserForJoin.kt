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
    val nickname: String
)