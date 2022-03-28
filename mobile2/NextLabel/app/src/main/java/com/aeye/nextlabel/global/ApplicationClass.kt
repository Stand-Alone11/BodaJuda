package com.aeye.nextlabel.global

import android.app.Application
import android.content.ContentResolver
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

    companion object {
        lateinit var sRetrofit: Retrofit
        lateinit var sSharedPreferences: SharedPreferencesUtil
        lateinit var sContentResolver: ContentResolver

        const val JWT = "JWT"
        const val X_AUTH_TOKEN = "X-AUTH-TOKEN"
        const val SHARED_PREFERENCES_NAME = "NEXT_LABEL"
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
            // Logcat에 'okhttp.OkHttpClient'로 검색하면 http 통신 내용 확인 가능
            .addInterceptor(
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS).setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .addNetworkInterceptor(XAccessTokenInterceptor())  // JWT 헤더 전송(추가한 부분)
            .build()

        sRetrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}