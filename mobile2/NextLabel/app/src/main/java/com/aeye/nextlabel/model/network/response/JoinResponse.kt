package com.aeye.nextlabel.model.network.response

import com.aeye.nextlabel.feature.common.BaseResponse
import com.google.gson.annotations.SerializedName

class JoinResponse: BaseResponse() {
    @SerializedName("body")
    val dataSet: String? = null

    @SerializedName("statusCode")
    val statusCode: String? = null

    @SerializedName("statusCodeValue")
    val statusCodeValue: Int? = null
}