package com.gowittgroup.smartassist.util

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.util.Log

class NetworkUtilImpl(private val context: Context):NetworkUtil {
     override fun isDeviceOnline(): Boolean {
        val connManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connManager.getNetworkCapabilities(connManager.activeNetwork)
        return if (networkCapabilities == null) {
            Log.d(TAG, "Device Offline")
            false
        } else {
            Log.d(TAG, "Device Online")
            true
        }
    }

    companion object{
        private val TAG: String = NetworkUtil::class.java.simpleName
    }
}

interface NetworkUtil {
    fun isDeviceOnline(): Boolean
}
