package com.gowittgroup.smartassistlib.models

import com.google.gson.annotations.SerializedName

data class Permission(
    @SerializedName("id") val id: String,
    @SerializedName("object") val objectType: String,
    @SerializedName("created") val created: Int,
    @SerializedName("allow_create_engine") val allowCreateEngine: Boolean,
    @SerializedName("allow_sampling") val allowSampling: Boolean,
    @SerializedName("allow_logprobs") val allowLogprobs: Boolean,
    @SerializedName("allow_search_indices") var allowSearchIndices: Boolean,
    @SerializedName("allow_view") val allowView: Boolean,
    @SerializedName("allow_fine_tuning") val allowFineTuning: Boolean,
    @SerializedName("organization") val organization: String,
    @SerializedName("group") val group: String,
    @SerializedName("is_blocking") val isBlocking: Boolean
)
