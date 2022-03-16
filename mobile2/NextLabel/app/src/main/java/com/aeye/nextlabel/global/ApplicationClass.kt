package com.aeye.nextlabel.global

import android.app.Application
import android.content.ContentResolver
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApplicationClass: Application() {
    // TODO: Base url 세팅
    val BASE_URL = ""
    val TIME_OUT = 5000L

    companion object {
        lateinit var sRetrofit: Retrofit
        lateinit var sContentResolver: ContentResolver
    }

    override fun onCreate() {
        super.onCreate()
        initRetrofit()

        sContentResolver = contentResolver
    }

    private fun initRetrofit() {
        val client: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            // 로그캣에 okhttp.OkHttpClient로 검색하면 http 통신 내용 로깅.
            .addInterceptor(
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS).setLevel(
                    HttpLoggingInterceptor.Level.BODY))
            .build()

        sRetrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}