package com.aeye.nextlabel.model.network.response

import com.google.gson.annotations.SerializedName

class ProfileResponse {
    @SerializedName("profileImgUrl")
    val imgUrl: String? = null

    @SerializedName("email")
    val email: String? = null

    @SerializedName("nickName")
    val nickname: String? = null

    @SerializedName("imageTotal")
    val imageTotal: Int? = null

    @SerializedName("imageAccept")
    val imageAccept: Int? = null

    @SerializedName("imageDeny")
    val imageDeny: Int? = null

    @SerializedName("imageWait")
    val imageWait: Int? = null

    @SerializedName("rank")
    val rank: Int? = null
}