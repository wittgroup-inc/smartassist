package com.gowittgroup.smartassistlib.models.banner

import com.google.gson.annotations.SerializedName

data class Banner(
    @SerializedName("showBanner") val showBanner: Boolean = false,
    @SerializedName("content") val content: BannerContent? = null

) {
    companion object {
        val EMPTY = Banner(showBanner = false, content = null)
    }
}