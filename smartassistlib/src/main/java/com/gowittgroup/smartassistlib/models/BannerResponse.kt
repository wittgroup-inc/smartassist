package com.gowittgroup.smartassistlib.models

import com.google.gson.annotations.SerializedName

data class BannerResponse(
    @SerializedName("showBanner")
    val shouldShowBanner: Boolean = false,
    @SerializedName("bannerContent")
    val bannerContent: BannerContent? = null
) {
    companion object {
        val EMPTY = BannerResponse(shouldShowBanner = false, bannerContent = null)
    }
}