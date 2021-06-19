package com.example.petrogps2.model


import com.google.gson.annotations.SerializedName

data class ResponseMetaData(
    @SerializedName("SearchRequest")
    val searchRequest: SearchRequest,
    @SerializedName("SearchResponse")
    val searchResponse: SearchResponse
)