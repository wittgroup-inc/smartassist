package com.gowittgroup.smartassistlib.network

import com.gowittgroup.smartassistlib.KeyManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class HeaderInterceptor @Inject constructor(private val keyManager: KeyManager) : Interceptor {
    //throw an exception to cancel request
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
            .newBuilder() // returns Request.Builder
            .addHeader("Authorization", "Bearer ${keyManager.getOpenAiKey()}")
            .addHeader("Content-Type", "application/json")
            .build()

        //proceed with the request
        return chain.proceed(request)
    }

}
