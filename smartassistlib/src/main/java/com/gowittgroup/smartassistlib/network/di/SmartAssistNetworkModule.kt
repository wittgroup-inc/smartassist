package com.gowittgroup.smartassistlib.network.di

import com.gowittgroup.smartassistlib.network.ChatGptHeaderInterceptor
import com.gowittgroup.smartassistlib.network.ChatGptService
import com.gowittgroup.smartassistlib.util.Constants
import com.gowittgroup.smartassistlib.util.KeyManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SmartAssistNetworkModule {

    @Provides
    @Singleton
    fun providesLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    fun providesHeaderInterceptor(keyManager: KeyManager): ChatGptHeaderInterceptor = ChatGptHeaderInterceptor(keyManager)

    @Provides
    @Singleton
    fun providesOkHttClient(
        loggingInterceptor: HttpLoggingInterceptor,
        chatGptHeaderInterceptor: ChatGptHeaderInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.MINUTES)
        .writeTimeout(10, TimeUnit.MINUTES)
        .addInterceptor(loggingInterceptor)
        .addInterceptor(chatGptHeaderInterceptor)
        .build()

    @Provides
    @Singleton
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(Constants.CHAT_GPT_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun providesChatGptService(retrofit: Retrofit): ChatGptService =
        retrofit.create(ChatGptService::class.java)
}