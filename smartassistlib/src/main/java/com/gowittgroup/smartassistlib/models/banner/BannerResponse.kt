package com.gowittgroup.smartassistlib.models.banner

import com.google.gson.annotations.SerializedName

data class BannerResponse (

    @SerializedName("data" ) val data : Banner = Banner()

)