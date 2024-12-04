package com.gowittgroup.smartassistlib.network

import com.gowittgroup.smartassistlib.util.KeyManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class HeaderInterceptor @Inject constructor(private val keyManager: KeyManager) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer ${keyManager.getOpenAiKey()}")
            .addHeader("Content-Type", "application/json")
            .build()


        return chain.proceed(request)
    }

}
