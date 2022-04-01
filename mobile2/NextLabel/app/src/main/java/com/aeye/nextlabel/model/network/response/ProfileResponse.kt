package com.aeye.nextlabel.model.network.response

import com.aeye.nextlabel.model.dto.History
import com.google.gson.annotations.SerializedName

class ProfileResponse {
    @SerializedName("profileImgUrl")
    val imgUrl: String? = null

    @SerializedName("email")
    val email: String? = null

    @SerializedName("nickName")
    val nickname: String? = null

    @SerializedName("imageTotal")
    val imageTotal: Int = 0

    @SerializedName("imageAccept")
    val imageAccept: Int = 0

    @SerializedName("imageDeny")
    val imageDeny: Int = 0

    @SerializedName("imageWait")
    val imageWait: Int = 0

    @SerializedName("rank")
    val rank: Int? = null

    @SerializedName("history")
    val historyList = mutableListOf<History>()
}