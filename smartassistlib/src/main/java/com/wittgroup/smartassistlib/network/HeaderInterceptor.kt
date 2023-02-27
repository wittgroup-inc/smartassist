package com.wittgroup.smartassistlib.network

import com.wittgroup.smartassistlib.Constants.API_KEY
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class HeaderInterceptor : Interceptor {
    //throw an exception to cancel request
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
            .newBuilder() // returns Request.Builder
            .addHeader("Authorization", "Bearer $API_KEY")
            .addHeader("Content-Type", "application/json")
            .build()

        //proceed with the request
        return chain.proceed(request)
    }

}
