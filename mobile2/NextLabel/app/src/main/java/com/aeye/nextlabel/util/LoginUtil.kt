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

    fun isTokenExisted(): Boolean {
        return !preferences.getString(ApplicationClass.JWT).isNullOrBlank()
    }

    fun logOut() {
        preferences.deleteString(ApplicationClass.JWT)
        userId = null
    }

    fun getUserId() {
        val token = preferences.getString(ApplicationClass.JWT)!!
        val jwt = JWT(token)

        userId = jwt.getClaim("id").asInt()
        Log.d("USER_ID", userId.toString())
    }
}