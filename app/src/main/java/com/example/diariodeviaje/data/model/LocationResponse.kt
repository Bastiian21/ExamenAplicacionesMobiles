package com.example.diariodeviaje.data.model

import com.google.gson.annotations.SerializedName

data class LocationResponse(
    @SerializedName("display_name") val display_name: String?,
    val address: AddressDetails?
)

data class AddressDetails(
    val road: String?,
    val suburb: String?,
    val city: String?,
    val state: String?,
    val country: String?
)