package com.aeye.nextlabel.model.network.api

import com.aeye.nextlabel.model.dto.Label
import com.aeye.nextlabel.model.network.response.LabelingResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface LabelingApi {
    // 1. image and labeling data
    @Multipart
    @POST("/mo")
    suspend fun giveLabels(@Part("label") label: RequestBody, @Part file: MultipartBody.Part, @Query("projectId") projectId: Int): Response<LabelingResponse>
}