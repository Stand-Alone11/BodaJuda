package com.aeye.nextlabel.util

import com.aeye.nextlabel.global.ApplicationClass
import com.aeye.nextlabel.model.dto.UserInfo
import com.auth0.android.jwt.JWT

object LoginUtil {
    private val preferences = ApplicationClass.sSharedPreferences

    var USER_ID: Int? = null

    val USER_IMG_URL = "imgUrl"
    val USER_EMAIL = "email"
    val USER_NICKNAME = "nickname"
    val USER_IMG_TOTAL = "imageTotal"
    val USER_IMG_ACCEPT = "imageAccept"
    val USER_IMG_DENY = "imageDeny"
    val USER_IMG_WAIT = "imageWait"
    val USER_RANK = "rank"

    // Login 여부 확인
    fun isLogin(): Boolean {
        return !preferences.getString(ApplicationClass.JWT).isNullOrBlank()
    }

    fun logout() {
        preferences.deleteString(ApplicationClass.JWT)
        USER_ID = null

        // logout 하면 user info 지우기
        preferences.deleteString(USER_IMG_URL)
        preferences.deleteString(USER_EMAIL)
        preferences.deleteString(USER_NICKNAME)
        preferences.deleteString(USER_IMG_TOTAL)
        preferences.deleteString(USER_IMG_ACCEPT)
        preferences.deleteString(USER_IMG_DENY)
        preferences.deleteString(USER_IMG_WAIT)
        preferences.deleteString(USER_RANK)
    }

    // Token에서 "userId" 추출
    fun getUserId() {
        val token = preferences.getString(ApplicationClass.JWT)!!
        val jwt = JWT(token)

        USER_ID = jwt.getClaim("id").asInt()
    }

    // user info 불러오기
    fun getUserInfo(): UserInfo {
        val imgUrl = preferences.getString(USER_IMG_URL)
        val email = preferences.getString(USER_EMAIL)
        val nickname = preferences.getString(USER_NICKNAME)
        val imageTotal = preferences.getString(USER_IMG_TOTAL)?.toInt()
        val imageAccept = preferences.getString(USER_IMG_ACCEPT)?.toInt()
        val imageDeny = preferences.getString(USER_IMG_DENY)?.toInt()
        val imageWait = preferences.getString(USER_IMG_WAIT)?.toInt()
        val rank = preferences.getString(USER_RANK)?.toInt()

        return UserInfo(imgUrl, email, nickname, imageTotal, imageAccept, imageDeny, imageWait, rank)
    }
}