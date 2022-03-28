package com.aeye.nextlabel.util

import com.aeye.nextlabel.global.ApplicationClass
import com.aeye.nextlabel.model.dto.UserInfo

object LoginUtil {
    val USER_ID = "userId"

    private val preferences = ApplicationClass.sSharedPreferences

    fun isAutoLogin(): Boolean {
        return preferences.getAutoLoginFlag()
    }

    fun signOut() {
        preferences.deleteString(ApplicationClass.JWT)
        deleteUserInfo()
    }

    fun setAutoLogin(flag: Boolean) {
        preferences.setAutoLogin(flag)
    }

    fun isTokenExisted(): Boolean {
        return !preferences.getString(ApplicationClass.JWT).isNullOrBlank()
    }

    fun saveUserInfo(userInfo: UserInfo) {
        preferences.setString(USER_ID, userInfo.userId.toString())
    }

    fun deleteUserInfo() {
        preferences.deleteString(USER_ID)
    }

    fun getUserInfo(): UserInfo? {
        val userId = preferences.getString(USER_ID)?.toString()

        return if (userId == null) {
            null
        } else {
            UserInfo(userId!!)
        }
    }
}