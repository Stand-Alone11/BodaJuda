package com.aeye.nextlabel.repository

import android.util.Log
import androidx.lifecycle.ViewModel
import com.aeye.nextlabel.global.ApplicationClass
import com.aeye.nextlabel.model.dto.RankUser
import com.aeye.nextlabel.model.network.api.CommunityApi
import com.aeye.nextlabel.model.network.response.LeaderBoardResponse
import com.aeye.nextlabel.util.Resource
import java.lang.Exception

class CommunityRepository {
    private val TAG = "CommunityRepository_debuk"
    var communityApi: CommunityApi = ApplicationClass.sRetrofit.create(CommunityApi::class.java)

    suspend fun getLeaderBoard(page: Int, size: Int): Resource<List<RankUser>> {
        return try {
            val response = communityApi.getLeaderBoard(page, size)
            if (response.isSuccessful) {
                return if(response.code() == 200) {
                    Resource.success(response.body()!!)
                } else {
                    Resource.error(null, "message 1")
                }
            } else {
                Resource.error(null, "message 2")
            }
        } catch (e: Exception) {
            Log.d(TAG, "getLeaderBoard: $e")
            Resource.error(null, "message 3")
        }
    }
}