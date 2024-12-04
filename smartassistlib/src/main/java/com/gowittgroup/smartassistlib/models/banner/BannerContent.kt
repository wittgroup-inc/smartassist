package com.gowittgroup.smartassistlib.models.banner

import com.google.gson.annotations.SerializedName

data class BannerContent(
    @SerializedName("id") val id: String = "",
    @SerializedName("title") val title: String? = null,
    @SerializedName("subTitle") val subTitle: String? = null,
    @SerializedName("descriptions") val descriptions: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null

) {
    companion object {
        val EMPTY = BannerContent(
            id = "",
            title = "",
            subTitle = "",
            descriptions = "",
            imageUrl = ""
        )
    }
}