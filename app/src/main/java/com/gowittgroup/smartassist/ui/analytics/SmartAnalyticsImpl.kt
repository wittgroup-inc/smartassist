package com.gowittgroup.smartassist.ui.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class SmartAnalyticsImpl(private val firebaseAnalytics: FirebaseAnalytics) : SmartAnalytics {
    override fun logEvent(name: String, param: Bundle) {
        firebaseAnalytics.logEvent(name, param)
    }
}
