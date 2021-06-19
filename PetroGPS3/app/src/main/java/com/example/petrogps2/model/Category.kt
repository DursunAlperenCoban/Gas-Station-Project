package com.example.petrogps2.model


import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("class")
    val classX: String,
    @SerializedName("name")
    val name: String
)