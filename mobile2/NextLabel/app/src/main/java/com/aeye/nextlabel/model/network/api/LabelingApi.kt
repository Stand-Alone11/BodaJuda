package com.aeye.nextlabel.model.network.api

import com.aeye.nextlabel.model.dto.Label
import com.aeye.nextlabel.model.network.response.LabelingResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

interface LabelingApi {
    // 1. image and labeling data
    @POST("/mo")
    suspend fun giveLabels(@Body label: Label): Response<LabelingResponse>
}