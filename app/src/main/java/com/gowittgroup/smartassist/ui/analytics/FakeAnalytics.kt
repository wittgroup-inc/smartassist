package com.gowittgroup.smartassist.ui.analytics

import android.os.Bundle

class FakeAnalytics : SmartAnalytics {
    override fun logEvent(name: String, param: Bundle) {

    }
}