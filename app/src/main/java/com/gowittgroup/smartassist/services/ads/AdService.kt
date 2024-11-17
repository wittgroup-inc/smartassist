package com.gowittgroup.smartassist.services.ads

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.gowittgroup.core.logger.SmartLog
import com.gowittgroup.smartassist.util.Constants
import com.gowittgroup.smartassist.util.Session

class AdService {

    private var mInterstitialAd: InterstitialAd? = null
    private var isAdLoaded by mutableStateOf(false)

    fun loadInterstitialAd(context: Context) {
        if (!Session.subscriptionStatus){
            loadAd(context)
        }
    }

    private fun loadAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            Constants.ON_CLICK_INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    mInterstitialAd = interstitialAd
                    isAdLoaded = true
                    SmartLog.d(TAG, "Interstitial ad loaded")

                    // Optionally, you can set callbacks here
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent()
                                SmartLog.d(TAG, "Ad dismissed.")
                                // Reload the ad after dismissal
                                loadInterstitialAd(context)
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                super.onAdFailedToShowFullScreenContent(adError)
                                SmartLog.e(TAG, "Ad failed to show: ${adError?.message}")
                            }

                            override fun onAdShowedFullScreenContent() {
                                super.onAdShowedFullScreenContent()
                                SmartLog.d(TAG, "Ad showed.")
                            }
                        }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    SmartLog.e(TAG, "Failed to load interstitial ad: ${loadAdError.message}")
                }
            }
        )
    }

    fun showInterstitialAd(context: Context) {
        if (!Session.subscriptionStatus) {
            showAd(context)
        }
    }

    private fun showAd(context: Context) {
        if (Session.subscriptionStatus) return
        if (isAdLoaded && mInterstitialAd != null) {
            mInterstitialAd?.show(context as Activity)
        } else {
            SmartLog.d(TAG, "Interstitial ad wasn't ready yet.")
            Toast.makeText(context, "Ad not ready, please try again later.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    companion object {
        private var TAG = AdService::class.java.simpleName
    }
}