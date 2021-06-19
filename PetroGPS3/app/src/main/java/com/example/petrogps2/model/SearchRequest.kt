package com.example.petrogps2.model


import com.google.gson.annotations.SerializedName

data class SearchRequest(
    @SerializedName("boundedBy")
    val boundedBy: List<List<Double>>,
    @SerializedName("request")
    val request: String,
    @SerializedName("results")
    val results: Int,
    @SerializedName("skip")
    val skip: Int
)