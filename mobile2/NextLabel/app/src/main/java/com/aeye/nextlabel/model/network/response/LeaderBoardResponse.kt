package com.aeye.nextlabel.model.network.response

import com.aeye.nextlabel.feature.common.BaseResponse
import com.aeye.nextlabel.model.dto.RankUser

class LeaderBoardResponse: BaseResponse() {
    var users: List<RankUser> = emptyList()
}