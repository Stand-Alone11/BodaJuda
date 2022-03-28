package com.aeye.nextlabel.util

import android.content.Context
import android.content.SharedPreferences
import com.aeye.nextlabel.global.ApplicationClass

class SharedPreferencesUtil(context: Context) {
    private var preferences: SharedPreferences = context.getSharedPreferences(ApplicationClass.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun setString(key: String, value: String) {
        val editor = preferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key:String): String? {
        return preferences.getString(key, null)
    }

    fun deleteString(key: String) {
        val editor = preferences.edit()
        editor.remove(key).apply()
    }
}