package com.gowittgroup.smartassist.ui.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class SmartAnalyticsImpl @Inject constructor(private val firebaseAnalytics: FirebaseAnalytics) : SmartAnalytics {
    override fun logEvent(name: String, param: Bundle) {
        firebaseAnalytics.logEvent(name, param)
    }
}
