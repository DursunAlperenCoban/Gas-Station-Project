package com.example.petrogps2.model


import com.google.gson.annotations.SerializedName

data class Properties(
    @SerializedName("boundedBy")
    val boundedBy: List<List<Double>>,
    @SerializedName("CompanyMetaData")
    val companyMetaData: CompanyMetaData,
    @SerializedName("description")
    val description: String,
    @SerializedName("name")
    val name: String
)