package com.example.petrogps2.model


import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("boundedBy")
    val boundedBy: List<List<Double>>,
    @SerializedName("display")
    val display: String,
    @SerializedName("found")
    val found: Int
)