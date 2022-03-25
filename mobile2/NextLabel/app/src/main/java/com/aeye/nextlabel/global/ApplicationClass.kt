package com.aeye.nextlabel.global

import android.app.Application
import android.content.ContentResolver
import com.aeye.nextlabel.feature.common.AddCookiesInterceptor
import com.aeye.nextlabel.feature.common.ReceivedCookiesInterceptor
import com.aeye.nextlabel.feature.common.XAccessTokenInterceptor
import com.aeye.nextlabel.util.SharedPreferencesUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApplicationClass: Application() {
    val BASE_URL = "http://j6s004.p.ssafy.io:8080/"
    val TIME_OUT = 5000L
    val SP_NAME = "fcm_message"  // 추가한 부분

    companion object {
        lateinit var sRetrofit: Retrofit
        lateinit var sSharedPreferences: SharedPreferencesUtil
        lateinit var sContentResolver: ContentResolver

        // TODO: 차후 수정해야 함!!
        // JWT Token Header 키 값
        const val X_AUTH_TOKEN = "X-AUTH-TOKEN"
        const val SHARED_PREFERENCES_NAME = "IV_BLANC"
        const val COOKIES_KEY_NAME = "cookies"
        const val AUTO_LOGIN = "auto_login_flag"
        const val JWT = "JWT"
    }

    override fun onCreate() {
        super.onCreate()
        sSharedPreferences = SharedPreferencesUtil(applicationContext)  // 추가한 부분
        sContentResolver = contentResolver
        initRetrofit()
    }

    private fun initRetrofit() {
        val client: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            // 로그캣에 okhttp.OkHttpClient로 검색하면 http 통신 내용 로깅.
            .addInterceptor(
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS).setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .addNetworkInterceptor(XAccessTokenInterceptor()) // JWT 자동 헤더 전송(추가한 부분)
            .addInterceptor(AddCookiesInterceptor())  // 쿠키 전송(추가한 부분)
            .addInterceptor(ReceivedCookiesInterceptor()) // 쿠키 추출(추가한 부분)
            .build()

        sRetrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 추가한 부분
    private fun readSharedPreference(key:String): ArrayList<String>{
        val sp = getSharedPreferences(SP_NAME, MODE_PRIVATE)
        val gson = Gson()
        val json = sp.getString(key, "") ?: ""
        val type = object : TypeToken<ArrayList<String>>() {}.type
        val obj: ArrayList<String> = gson.fromJson(json, type) ?: ArrayList()
        return obj
    }
}