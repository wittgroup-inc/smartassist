package com.gowittgroup.smartassist.ui.components

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.gowittgroup.smartassist.services.ads.AdState
import com.gowittgroup.smartassist.util.Constants.AD_INTERVAL
import com.gowittgroup.smartassist.util.Session
import kotlinx.coroutines.delay

private const val TAG = "StartAdTimer"

@Composable
fun StartAdTimer() {
    val context = LocalContext.current

    if(!Session.subscriptionStatus){
        val adState = rememberAdState(context)
        LaunchedEffect(key1 = adState.isAppInForeground) {
            if (adState.isAppInForeground) {
                while (true) {
                    delay(AD_INTERVAL)
                    if (adState.isAppInForeground) {
                        Log.d(TAG, "Add showing")
                        adState.showInterstitialAd()
                    }
                }
            }
        }
    }
}


@Composable
fun rememberAdState(context: Context): AdState {
    return remember { AdState(context) }
}