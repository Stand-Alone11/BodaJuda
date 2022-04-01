package com.aeye.nextlabel.model.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Project(
    val accepted: Int,
    val description: String,
    val goal: Int,
    val id: Int,
    val provider: String,
    val title: String
) : Parcelable