package com.aeye.nextlabel.model.dto

import com.google.gson.annotations.SerializedName

data class UserForLogin(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("password")
    val password: String
)
