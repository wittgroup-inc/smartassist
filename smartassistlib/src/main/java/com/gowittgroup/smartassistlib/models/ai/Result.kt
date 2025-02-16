package com.gowittgroup.smartassistlib.models.ai


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("categories")
    val categories: Categories,
    @SerializedName("category_applied_input_types")
    val categoryAppliedInputTypes: CategoryAppliedInputTypes,
    @SerializedName("category_scores")
    val categoryScores: CategoryScores,
    @SerializedName("flagged")
    val flagged: Boolean
)