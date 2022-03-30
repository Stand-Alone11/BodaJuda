package com.aeye.nextlabel.model.dto

import com.google.gson.annotations.SerializedName

data class Label(
    @SerializedName("title")
    val title: String? = null,

    @SerializedName("provider")
    val provider: String? = null,

    @SerializedName("L_X")
    val coorXOfLeftTop: Int? = null,

    @SerializedName("L_Y")
    val coorYOfLeftTop: Int? = null,

    @SerializedName("R_X")
    val coorXOfRightBottom: Int? = null,

    @SerializedName("R_Y")
    val coorYOfRightBottom: Int? = null
)