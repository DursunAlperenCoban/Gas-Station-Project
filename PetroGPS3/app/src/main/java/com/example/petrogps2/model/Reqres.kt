package com.example.petrogps2.model


import com.google.gson.annotations.SerializedName

data class Reqres(
    @SerializedName("features")
    val features: List<Feature>,
    @SerializedName("properties")
    val properties: PropertiesX,
    @SerializedName("type")
    val type: String
)