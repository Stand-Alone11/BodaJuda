package com.aeye.nextlabel.repository

import com.aeye.nextlabel.global.ApplicationClass
import com.aeye.nextlabel.model.dto.Label
import com.aeye.nextlabel.model.network.api.LabelingApi
import com.aeye.nextlabel.model.network.response.LabelingResponse
import com.aeye.nextlabel.util.Resource

class LabelingRepository {
    var labelingApi: LabelingApi = ApplicationClass.sRetrofit.create(LabelingApi::class.java)

    suspend fun giveLabels(label: Label): Resource<LabelingResponse> {
        return try {
            val response = labelingApi.giveLabels(label)
            if (response.isSuccessful) {
                return if(response.code() == 200 && response.body()!!.output == 1) {
                    Resource.success(response.body()!!)
                } else {
                    Resource.error(null, response.body()!!.message!!)
                }
            } else {
                Resource.error(null, "알 수 없는 오류입니다.")
            }
        } catch (e: Exception) {
            Resource.error(null, "서버와 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.")
        }
    }
}