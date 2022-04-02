package com.aeye.nextlabel.repository

import android.util.Log
import com.aeye.nextlabel.global.ApplicationClass
import com.aeye.nextlabel.model.dto.Label
import com.aeye.nextlabel.model.network.api.LabelingApi
import com.aeye.nextlabel.model.network.response.LabelingResponse
import com.aeye.nextlabel.util.Resource
import okhttp3.MultipartBody
import okhttp3.RequestBody

class LabelingRepository {
    private val TAG = "LabelingRepository_debuk"
    var labelingApi: LabelingApi = ApplicationClass.sRetrofit.create(LabelingApi::class.java)

    suspend fun uploadLabels(label: RequestBody, img: MultipartBody.Part, projectId: Int): Resource<LabelingResponse> {
        return try {
            val response = labelingApi.giveLabels(label, img, projectId)
            if (response.isSuccessful) {
                Resource.success(response.body()!!)
            } else {
                when(response.code()) {
                    401 -> Resource.error(null, "유효하지 않은 유저입니다.")
                    406 -> Resource.error(null, "잘못된 정보를 보냈습니다.")
                    else -> Resource.error(null, "알 수 없는 오류입니다.")
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "uploadLabels: $e")
            Resource.error(null, "서버와 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.")
        }
    }
}