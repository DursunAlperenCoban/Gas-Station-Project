package com.example.petrogps2.model


import com.google.gson.annotations.SerializedName

data class CompanyMetaData(
    @SerializedName("address")
    val address: String,
    @SerializedName("Categories")
    val categories: List<Category>,
    @SerializedName("Hours")
    val hours: Hours,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("Phones")
    val phones: List<Phone>,
    @SerializedName("url")
    val url: String
)