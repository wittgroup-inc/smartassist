package com.wittgroup.smartassistlib.network

import com.wittgroup.smartassistlib.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object NetworkHelper {

    private fun loggingInterceptor() = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    private fun getOkHttClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor())
        .addInterceptor(HeaderInterceptor())
        .build()

    fun getRetrofit(): Retrofit = Retrofit.Builder()
        .client(getOkHttClient())
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

}
