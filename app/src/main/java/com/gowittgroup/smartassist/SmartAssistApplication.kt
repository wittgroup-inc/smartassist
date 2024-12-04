package com.gowittgroup.smartassist

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.gowittgroup.core.logger.SmartLog
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SmartAssistApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        SmartLog.initLogger(BuildConfig.DEBUG)
    }
}
