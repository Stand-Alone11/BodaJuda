package com.aeye.nextlabel.temp.response

import com.aeye.nextlabel.temp.dto.UserInfo
import com.google.gson.annotations.SerializedName

class LoginResponse: BaseResponse() {
    @SerializedName("data")
    val dataSet: UserInfo? = null
}