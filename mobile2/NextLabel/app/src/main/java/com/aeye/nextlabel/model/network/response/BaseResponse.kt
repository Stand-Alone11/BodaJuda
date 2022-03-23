package com.aeye.nextlabel.model.network.response

import com.google.gson.annotations.SerializedName

open class BaseResponse {
    @SerializedName("output")
    val output: Int = 0
    @SerializedName("msg")
    val message: String? = null
}