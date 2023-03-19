package com.gowittgroup.smartassistlib.models

import com.google.gson.annotations.SerializedName

data class ModelResponse(
    @SerializedName("object") val objectType: String,
    @SerializedName("data") val data: ArrayList<Data>
)
