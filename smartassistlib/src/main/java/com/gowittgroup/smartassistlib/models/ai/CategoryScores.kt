package com.gowittgroup.smartassistlib.models.ai


import com.google.gson.annotations.SerializedName

data class CategoryScores(
    @SerializedName("harassment")
    val harassment: Double,
    @SerializedName("harassment/threatening")
    val harassmentthreatening: Double,
    @SerializedName("hate")
    val hate: Double,
    @SerializedName("hate/threatening")
    val hatethreatening: Double,
    @SerializedName("illicit")
    val illicit: Double,
    @SerializedName("illicit/violent")
    val illicitviolent: Double,
    @SerializedName("self-harm")
    val selfHarm: Double,
    @SerializedName("self-harm/instructions")
    val selfHarminstructions: Double,
    @SerializedName("self-harm/intent")
    val selfHarmintent: Double,
    @SerializedName("sexual")
    val sexual: Double,
    @SerializedName("sexual/minors")
    val sexualminors: Double,
    @SerializedName("violence")
    val violence: Double,
    @SerializedName("violence/graphic")
    val violencegraphic: Double
)