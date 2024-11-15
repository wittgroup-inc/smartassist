package com.gowittgroup.smartassist.util

object Constants {
    /**
     * Test Interstitial Ad Unit Id: "ca-app-pub-3940256099942544/1033173712"
     * Test Banner Ad Unit Id: "ca-app-pub-3940256099942544/6300978111"
     */
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-0664761685979414/8608646625"
    const val ON_CLICK_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-0664761685979414/4151490890"
    const val HOME_TOP_BANNER_AD_UNIT_ID = "ca-app-pub-0664761685979414/7078302483"
    const val COMMON_BANNER_AD_UNIT_ID = "ca-app-pub-0664761685979414/9640886647"
    const val AD_INTERVAL: Long = 3 * 60 * 1000

    object SubscriptionSKUs{
        const val SUB_DAILY = "SUB_DAILY"
        const val SUB_MONTHLY = "SUB_MONTHLY"
        const val SUB_YEARLY = "SUB_YEARLY"
    }
}