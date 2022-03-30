package com.aeye.nextlabel.util

import android.util.Log
import com.aeye.nextlabel.global.ApplicationClass
import com.aeye.nextlabel.global.ApplicationClass.Companion.JWT
import com.aeye.nextlabel.model.dto.UserInfo
import com.auth0.android.jwt.JWT
import java.util.*

object LoginUtil {
    private val preferences = ApplicationClass.sSharedPreferences
    var userId: Int? = null

    // TODO: Login 여부 확인
    fun isLogin(): Boolean {
        return !preferences.getString(ApplicationClass.JWT).isNullOrBlank()
    }

    fun logout() {
        preferences.deleteString(ApplicationClass.JWT)
        userId = null
    }

    // TODO: Token에서 "userId" 추출
    fun getUserId() {
        val token = preferences.getString(ApplicationClass.JWT)!!
        val jwt = JWT(token)

        userId = jwt.getClaim("id").asInt()
        Log.d("USER_ID", userId.toString())
    }
}