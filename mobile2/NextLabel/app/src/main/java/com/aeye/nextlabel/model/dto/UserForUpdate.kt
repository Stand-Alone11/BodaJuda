package com.aeye.nextlabel.model.dto

import com.google.gson.annotations.SerializedName

data class UserForUpdate(
    @SerializedName("email")
    val email: String,

    @SerializedName("nickName")
    val nickname: String,
)
