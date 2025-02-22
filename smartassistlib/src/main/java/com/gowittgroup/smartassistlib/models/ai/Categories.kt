package com.gowittgroup.smartassistlib.models.ai


import com.google.gson.annotations.SerializedName

data class Categories(
    @SerializedName("harassment")
    val harassment: Boolean,
    @SerializedName("harassment/threatening")
    val harassmentthreatening: Boolean,
    @SerializedName("hate")
    val hate: Boolean,
    @SerializedName("hate/threatening")
    val hatethreatening: Boolean,
    @SerializedName("illicit")
    val illicit: Boolean,
    @SerializedName("illicit/violent")
    val illicitviolent: Boolean,
    @SerializedName("self-harm")
    val selfHarm: Boolean,
    @SerializedName("self-harm/instructions")
    val selfHarminstructions: Boolean,
    @SerializedName("self-harm/intent")
    val selfHarmintent: Boolean,
    @SerializedName("sexual")
    val sexual: Boolean,
    @SerializedName("sexual/minors")
    val sexualminors: Boolean,
    @SerializedName("violence")
    val violence: Boolean,
    @SerializedName("violence/graphic")
    val violencegraphic: Boolean
)