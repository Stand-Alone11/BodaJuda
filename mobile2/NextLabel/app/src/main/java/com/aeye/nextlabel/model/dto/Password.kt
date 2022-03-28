package com.aeye.nextlabel.model.dto

import com.google.gson.annotations.SerializedName

data class Password(
    @SerializedName("userId")
    val userId: String,
)
