@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.gowittgroup.smartassist.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme
import com.gowittgroup.smartassist.util.formatToViewDateTimeDefaults
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var  smartAnalytics: SmartAnalytics

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission()
        }
        setContent {
            logAppOpenEvent(smartAnalytics)
            val widthSizeClass = calculateWindowSizeClass(this).widthSizeClass
            SmartAssistApp(smartAnalytics = smartAnalytics, widthSizeClass = widthSizeClass)
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_REQUEST_CODE && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
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

    override fun onDestroy() {
        super.onDestroy()
        logAppExitEvent(smartAnalytics)
    }


    companion object {
        private const val RECORD_AUDIO_REQUEST_CODE = 200
        private const val TAG = "SmartAssist:Home"
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SmartAssistTheme {
        // HomeScreen()
    }
}




