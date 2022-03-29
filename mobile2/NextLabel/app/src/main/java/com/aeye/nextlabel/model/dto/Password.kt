package com.aeye.nextlabel.model.dto

import com.google.gson.annotations.SerializedName

data class Password(
    @SerializedName("password")
    val password: String,

    @SerializedName("passwordConfirm")
    val passwordConfirm: String,
)
