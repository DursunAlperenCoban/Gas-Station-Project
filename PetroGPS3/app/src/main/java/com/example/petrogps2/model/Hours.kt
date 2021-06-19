package com.example.petrogps2.model


import com.google.gson.annotations.SerializedName

data class Hours(
    @SerializedName("Availabilities")
    val availabilities: List<Availability>,
    @SerializedName("text")
    val text: String
)