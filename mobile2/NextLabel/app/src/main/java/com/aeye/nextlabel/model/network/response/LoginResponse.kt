package com.aeye.nextlabel.model.network.response

import com.aeye.nextlabel.feature.common.BaseResponse
import com.google.gson.annotations.SerializedName

class LoginResponse: BaseResponse() {
    @SerializedName("token")
    val token: String? = null
}