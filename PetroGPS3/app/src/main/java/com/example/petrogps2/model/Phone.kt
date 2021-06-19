package com.example.petrogps2.model


import com.google.gson.annotations.SerializedName

data class Phone(
    @SerializedName("formatted")
    val formatted: String,
    @SerializedName("type")
    val type: String
)