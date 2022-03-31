package com.aeye.nextlabel.model.dto

import com.google.gson.annotations.SerializedName

data class RankUser(
    @SerializedName("id")
    val id: Int,
    @SerializedName("imageAccepted")
    val imageAccepted: Int,
    @SerializedName("nickName")
    val nickName: String,
    @SerializedName("rank")
    val rank: Int
)