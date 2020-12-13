package com.example.absensi.handler

import com.example.absensi.common.GlobalClass
import com.example.absensi.common.Preferences
import com.example.absensi.common.Utilities
import com.example.absensi.model.common.CustomLogger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

open class BaseHandler {

    val preferences = Preferences(GlobalClass.applicationContext()!!)
    var accessToken: String? = accessToken()

    fun getClient() : Retrofit {
        var retrofit: Retrofit? = null
        val httpClient = OkHttpClient.Builder()

        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val request = original?.newBuilder()!!
                .header("Accept", "application/json")
                .header("Authorization", "Bearer $accessToken")
                .method(original.method(), original.body())
                .build()
            chain.proceed(request)
        }
        enabledLog(httpClient)

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(Utilities.getApiUrlForHttps())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient.build())
                .build()
        }

        return retrofit!!
    }

    fun enabledLog(httpClient: OkHttpClient.Builder) {
        val httpLoggingInterceptor = HttpLoggingInterceptor(CustomLogger())
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        httpClient.addInterceptor(httpLoggingInterceptor)
    }

    fun accessToken(): String {
        return preferences.accessToken
    }
}