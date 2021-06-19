package com.example.petrogps2.model


import com.google.gson.annotations.SerializedName

data class Availability(
    @SerializedName("Everyday")
    val everyday: Boolean,
    @SerializedName("TwentyFourHours")
    val twentyFourHours: Boolean
)