package com.thinkgas.heatapp.data.remote.model


import com.google.gson.annotations.SerializedName

data class HeatResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
)