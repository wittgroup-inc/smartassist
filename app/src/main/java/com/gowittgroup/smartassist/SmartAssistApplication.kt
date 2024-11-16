package com.gowittgroup.smartassist

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.gowittgroup.smartassistlib.datasources.subscription.SubscriptionDataSource
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SmartAssistApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
    }
}
