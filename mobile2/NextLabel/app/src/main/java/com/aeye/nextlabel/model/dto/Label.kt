package com.aeye.nextlabel.model.dto

import com.google.gson.annotations.SerializedName

data class Label(
    @SerializedName("title")
    val title: String? = null,

    @SerializedName("provider")
    val provider: String? = null,

    // 소문자 'l(엘ㅋㅋ)'
    @SerializedName("l_X")
    val coorXOfLeftTop: Int? = null,

    @SerializedName("l_Y")
    val coorYOfLeftTop: Int? = null,

    @SerializedName("R_X")
    val coorXOfRightBottom: Int? = null,

    @SerializedName("R_Y")
    val coorYOfRightBottom: Int? = null
)
