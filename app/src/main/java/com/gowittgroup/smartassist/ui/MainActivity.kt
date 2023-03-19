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
import com.gowittgroup.smartassist.SmartAssistApplication
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContainer = (application as SmartAssistApplication).container
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission()
        }

        setContent {

            val widthSizeClass = calculateWindowSizeClass(this).widthSizeClass
            SmartAssistApp(appContainer = appContainer, widthSizeClass = widthSizeClass)
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




