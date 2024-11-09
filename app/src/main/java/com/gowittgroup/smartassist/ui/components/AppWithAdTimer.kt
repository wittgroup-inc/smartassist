package com.gowittgroup.smartassist.ui.components

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.gowittgroup.smartassist.services.ads.AdState
import com.gowittgroup.smartassist.util.Constants.AD_INTERVAL
import kotlinx.coroutines.delay
private const val TAG = "AppWithAdTimer"
@Composable
fun AppWithAdTimer() {
    val context = LocalContext.current
    val adState = rememberAdState(context)

    LaunchedEffect(key1 = adState.isAppInForeground) {
        if (adState.isAppInForeground) {
            while (true) {
                delay(AD_INTERVAL)
                if (adState.isAppInForeground) {
                    Log.d(TAG, "Add showing")
                    adState.showAd()
                }
            }
        }
    }
}


@Composable
fun rememberAdState(context: Context): AdState {
    return remember { AdState(context) }
}