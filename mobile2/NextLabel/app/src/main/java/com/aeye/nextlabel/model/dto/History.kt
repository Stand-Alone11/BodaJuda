package com.aeye.nextlabel.model.dto

data class History(
    val accepted: Int,
    val denied: Int,
    val project: Project,
    val total: Int,
    val waited: Int
)