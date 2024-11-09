package com.gowittgroup.smartassistlib.models

import com.google.gson.annotations.SerializedName

data class BannerContent(
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("subTitle")
    val subTitle: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("url")
    val url: String? = null
) {
    companion object {
        val EMPTY = BannerContent(
            title = "",
            subTitle = "",
            description = "",
            url = ""
        )
    }
}