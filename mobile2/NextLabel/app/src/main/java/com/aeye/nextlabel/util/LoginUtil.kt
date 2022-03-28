package com.aeye.nextlabel.util

import com.aeye.nextlabel.global.ApplicationClass
import com.aeye.nextlabel.model.dto.UserInfo
import java.util.*

object LoginUtil {
    private val USER_ID = "userId"
    private val preferences = ApplicationClass.sSharedPreferences



    fun signOut() {
        preferences.deleteString(ApplicationClass.JWT)
        deleteUserInfo()
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