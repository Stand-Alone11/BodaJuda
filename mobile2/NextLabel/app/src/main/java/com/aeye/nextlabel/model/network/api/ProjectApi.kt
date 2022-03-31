package com.aeye.nextlabel.model.network.api

import com.aeye.nextlabel.model.dto.Project
import retrofit2.Response
import retrofit2.http.GET

interface ProjectApi {
    @GET("/admin/project/list")
    fun getProjectByPage(page: Int): Response<List<Project>>
}