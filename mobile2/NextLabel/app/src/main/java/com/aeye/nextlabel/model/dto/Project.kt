package com.aeye.nextlabel.model.dto

data class Project(
    val accepted: Int,
    val description: String,
    val goal: Int,
    val id: Int,
    val provider: String,
    val title: String
)