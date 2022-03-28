package com.aeye.nextlabel.model.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserInfo(
    @SerializedName("userId")
    val userId: String
): Parcelable
