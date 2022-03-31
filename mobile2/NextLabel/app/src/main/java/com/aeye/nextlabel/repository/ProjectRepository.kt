package com.aeye.nextlabel.repository

import android.util.Log
import com.aeye.nextlabel.global.ApplicationClass
import com.aeye.nextlabel.model.dto.Project
import com.aeye.nextlabel.model.network.api.ProjectApi
import com.aeye.nextlabel.util.Resource
import java.lang.Exception

class ProjectRepository {
    private val TAG = "ProjectRepository_debuk"
    private val projectApi = ApplicationClass.sRetrofit.create(ProjectApi::class.java)

    suspend fun getProjectByPage(page: Int): Resource<List<Project>> {
        return try {
            val response = projectApi.getProjectByPage(page)
            if(response.isSuccessful) {
                Resource.success(response.body()!!)
            } else {
                when(response.code()) {
                    400 -> Resource.error(null, "프로젝트 목록이 없습니다.")
                    else -> Resource.error(null, "알 수 없는 오류입니다.")
                }

            }
        } catch (e: Exception) {
            Log.d(TAG, "getProjectByPage: $e")
            Resource.error(null, "오류가 발생했습니다. 다시 시도해 주세요.")
        }
    }
}