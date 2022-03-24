package com.aeye.nextlabel.feature.common

import com.google.gson.annotations.SerializedName

open class BaseResponse {
    // response에 무조건 넣어달라고 벡앤드하고 이야기 해보기??
    @SerializedName("output")
    val output: Int = 0
    @SerializedName("msg")
    val message: String? = null
}