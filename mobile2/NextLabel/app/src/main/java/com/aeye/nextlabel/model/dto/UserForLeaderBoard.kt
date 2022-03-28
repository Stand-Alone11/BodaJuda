package com.aeye.nextlabel.model.dto

import com.google.gson.annotations.SerializedName

data class UserForLeaderBoard(

    @SerializedName("id")
    val id: Int,

    @SerializedName("imageTotal")
    val imageTotal: Int,

    @SerializedName("nickName")
    val nickName: String,

    @SerializedName("rank")
    val rank: Int,
)
