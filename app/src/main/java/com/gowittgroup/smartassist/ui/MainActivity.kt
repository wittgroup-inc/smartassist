@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.gowittgroup.smartassist.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme
import com.gowittgroup.smartassist.util.Session
import com.gowittgroup.smartassist.util.formatToViewDateTimeDefaults
import com.gowittgroup.smartassistlib.domain.models.successOr
import com.gowittgroup.smartassistlib.domain.repositories.authentication.AuthenticationRepository
import com.gowittgroup.smartassistlib.domain.repositories.subscription.SubscriptionRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var smartAnalytics: SmartAnalytics

    @Inject
    lateinit var subscriptionRepository: SubscriptionRepository

    @Inject
    lateinit var authenticationRepository: AuthenticationRepository

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        requestRecordAudioPermission()
        requestPostNotificationPermission()
        subscribeToNotificationTopic()
        init()

        setContent {
            logAppOpenEvent(smartAnalytics)
            val widthSizeClass = calculateWindowSizeClass(this).widthSizeClass
            SmartAssistApp(smartAnalytics = smartAnalytics, widthSizeClass = widthSizeClass)
        }
    }

    private fun init() {
        lifecycleScope.launch {
            authenticationRepository.currentUser.collect {
                Session.currentUser = it
            }
        }
        lifecycleScope.launch {
            Session.subscriptionStatus =
                subscriptionRepository.hasActiveSubscription().successOr(false)
        }
    }

    private fun requestRecordAudioPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_AUDIO_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun requestPostNotificationPermission() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    POST_NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if ((requestCode == POST_NOTIFICATION_PERMISSION_REQUEST_CODE || requestCode == RECORD_AUDIO_PERMISSION_REQUEST_CODE) && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) Toast.makeText(
                this, "Permission Granted", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun logAppOpenEvent(smartAnalytics: SmartAnalytics) {
        val bundle = Bundle()
        bundle.putString(SmartAnalytics.Param.TIME_STAMP, Date().formatToViewDateTimeDefaults())
        smartAnalytics.logEvent(SmartAnalytics.Event.APP_OPEN, bundle)
    }

    private fun logAppExitEvent(smartAnalytics: SmartAnalytics) {
        val bundle = Bundle()
        bundle.putString(SmartAnalytics.Param.TIME_STAMP, Date().formatToViewDateTimeDefaults())
        smartAnalytics.logEvent(SmartAnalytics.Event.APP_EXIT, bundle)
    }

    private fun subscribeToNotificationTopic() {
        lifecycleScope.launch {
            FirebaseMessaging.getInstance().subscribeToTopic(DEVELOPER_ANNOUNCEMENTS).await()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        logAppExitEvent(smartAnalytics)
    }



    companion object {
        private const val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 200
        private const val POST_NOTIFICATION_PERMISSION_REQUEST_CODE = 300
        private const val DEVELOPER_ANNOUNCEMENTS = "DEVELOPER_ANNOUNCEMENTS"
        private const val TAG = "SmartAssist:Home"
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SmartAssistTheme {
    }
}



