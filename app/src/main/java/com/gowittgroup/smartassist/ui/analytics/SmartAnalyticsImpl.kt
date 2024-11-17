package com.gowittgroup.smartassist.ui.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.gowittgroup.smartassistlib.domain.models.successOr
import com.gowittgroup.smartassistlib.domain.repositories.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class SmartAnalyticsImpl @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val settingsRepository: SettingsRepository
) : SmartAnalytics {
    override fun logEvent(name: String, param: Bundle) {
        val scope = CoroutineScope(SupervisorJob())
        scope.launch {
            val userId = settingsRepository.getUserId().successOr("")
            param.putString(SmartAnalytics.Param.USER_ID, userId)
            firebaseAnalytics.logEvent(name, param)
        }
    }
}
