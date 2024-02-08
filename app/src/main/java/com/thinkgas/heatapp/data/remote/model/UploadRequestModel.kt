package com.thinkgas.heatapp.data.remote.model

import com.google.gson.annotations.SerializedName

data class UploadRequestModel(
    @SerializedName("bp_number")
    val bpNumber:String,
    @SerializedName("application_number")
    val appNo:String,
    @SerializedName("session_id")
    val sessionId:String
)

data class GcUploadRequestModel(
    @SerializedName("unregstatus")
    val status:String,
    @SerializedName("mobile_number")
    val mobile:String,
    @SerializedName("session_id")
    val sessionId:String
)


