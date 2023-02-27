package com.wittgroup.smartassistlib.models

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("id") val id: String,
    @SerializedName("object") val objectType: String,
    @SerializedName("created") val created: Int,
    @SerializedName("owned_by") val ownedBy: String,
    @SerializedName("permission") val permission: List<Permission>,
    @SerializedName("root") val root: String,
    @SerializedName("parent") val parent: String
)
