package com.aeye.nextlabel.model.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class History(
    val accepted: Int,
    val denied: Int,
    val project: Project,
    val total: Int,
    val waited: Int
) : Parcelable