package com.aeye.nextlabel.model.network.api

import com.aeye.nextlabel.model.dto.Label
import com.aeye.nextlabel.model.network.response.LabelingResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.Part

interface LabelingApi {
    // 1. image and labeling data
    @Multipart
    @POST("/mo")
    suspend fun giveLabels(@Body label: RequestBody, @Part file: MultipartBody.Part): Response<LabelingResponse>
}