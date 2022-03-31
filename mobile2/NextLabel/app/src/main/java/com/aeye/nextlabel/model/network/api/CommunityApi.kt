package com.aeye.nextlabel.model.network.api

import com.aeye.nextlabel.model.dto.RankUser
import com.aeye.nextlabel.model.network.response.LeaderBoardResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CommunityApi {
    @GET("/accounts/rank")
    suspend fun getLeaderBoard(@Query("page") page: Int, @Query("size") size: Int): Response<List<RankUser>>
}