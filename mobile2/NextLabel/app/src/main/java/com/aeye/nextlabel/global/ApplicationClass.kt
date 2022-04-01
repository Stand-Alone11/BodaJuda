package com.aeye.nextlabel.global

import android.app.Application
import android.content.ContentResolver
import com.aeye.nextlabel.util.EmptyResponseOnEmptyConverterFactory
import com.aeye.nextlabel.util.SharedPreferencesUtil
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApplicationClass: Application() {

    val TIME_OUT = 10000L

    companion object {
        lateinit var sRetrofit: Retrofit
        lateinit var sSharedPreferences: SharedPreferencesUtil
        lateinit var sContentResolver: ContentResolver

        const val BASE_URL = "http://j6s004.p.ssafy.io:8080/"
        const val IMAGE_BASE_URL = "https://storage.googleapis.com/thirdeye_profile"
        const val JWT = "JWT"
        const val AUTHORIZATION = "Authorization"
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
            .addConverterFactory(EmptyResponseOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}