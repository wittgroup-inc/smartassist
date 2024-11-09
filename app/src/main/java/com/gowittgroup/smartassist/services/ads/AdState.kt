package com.gowittgroup.smartassist.services.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.gowittgroup.smartassist.util.Constants
import com.gowittgroup.smartassist.util.Session

class AdState(private val context: Context) : DefaultLifecycleObserver {
    var isAppInForeground = false
    private var mInterstitialAd: InterstitialAd? = null
    private var isAdLoaded by mutableStateOf(false)

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.d(TAG, "App Started")
        isAppInForeground = true
        loadInterstitialAd()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.d(TAG, "App Stopped")
        isAppInForeground = false
    }

    private fun loadInterstitialAd() {
        if (!Session.subscriptionStatus) {
            loadAd()
        }
    }

    private fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context,
            Constants.INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    mInterstitialAd = interstitialAd
                    isAdLoaded = true  // Mark the ad as loaded
                    Log.d(TAG, "Interstitial ad loaded")

                    // Optionally, you can set callbacks here
                    interstitialAd.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent()
                                Log.d(TAG, "Ad dismissed.")
                                loadInterstitialAd()
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                super.onAdFailedToShowFullScreenContent(adError)
                                Log.e("AdMob", "Ad failed to show: ${adError.message}")
                            }

                            override fun onAdShowedFullScreenContent() {
                                super.onAdShowedFullScreenContent()
                                Log.d("AdMob", "Ad showed.")
                            }
                        }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    Log.e(TAG, "Failed to load interstitial ad: ${loadAdError.message}")
                }
            })
    }

    fun showInterstitialAd() {
        if (!Session.subscriptionStatus) {
            showAd()
        }
    }

    private fun showAd() {
        if (Session.subscriptionStatus) return
        if (isAdLoaded && mInterstitialAd != null) {
            mInterstitialAd?.show(context as Activity)
        } else {
            Log.d(TAG, "Interstitial ad wasn't ready yet.")
            Toast.makeText(context, "Ad not ready, please try again later.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    companion object {
        private val TAG: String = AdState::class.java.simpleName
    }
}