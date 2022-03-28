package com.aeye.nextlabel.model.dto

import com.google.gson.annotations.SerializedName

data class UserForJoin(
//    {
//        "userId": "coffcat14",
//        "password": "lemon123",
//        "email": "2dend0714@gmail.com",
//        "nickname": "대지를가르는기러기"
//    }

    @SerializedName("userId")
    val userId: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("nickname")
    val nickname: String,
)
