package com.aeye.nextlabel.model.network.response

import com.aeye.nextlabel.model.dto.UserInfo
import com.google.gson.annotations.SerializedName

class LoginResponse: BaseResponse() {
    @SerializedName("data")
    val dataSet: UserInfo? = null
}