package com.gowittgroup.smartassistlib.models.ai


import com.google.gson.annotations.SerializedName

data class CategoryAppliedInputTypes(
    @SerializedName("harassment")
    val harassment: List<Any>,
    @SerializedName("harassment/threatening")
    val harassmentthreatening: List<Any>,
    @SerializedName("hate")
    val hate: List<Any>,
    @SerializedName("hate/threatening")
    val hatethreatening: List<Any>,
    @SerializedName("illicit")
    val illicit: List<Any>,
    @SerializedName("illicit/violent")
    val illicitviolent: List<Any>,
    @SerializedName("self-harm")
    val selfHarm: List<String>,
    @SerializedName("self-harm/instructions")
    val selfHarminstructions: List<String>,
    @SerializedName("self-harm/intent")
    val selfHarmintent: List<String>,
    @SerializedName("sexual")
    val sexual: List<String>,
    @SerializedName("sexual/minors")
    val sexualminors: List<Any>,
    @SerializedName("violence")
    val violence: List<String>,
    @SerializedName("violence/graphic")
    val violencegraphic: List<String>
)