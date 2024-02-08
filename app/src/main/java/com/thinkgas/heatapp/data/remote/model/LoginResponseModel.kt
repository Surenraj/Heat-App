package com.thinkgas.heatapp.data.remote.model


import com.google.gson.annotations.SerializedName

data class LoginResponseModel(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("httpcode")
    val httpcode: Any?,
    @SerializedName("message")
    val message: String,
    @SerializedName("msg_id")
    val msgId: Any?,
    @SerializedName("otp")
    val otp: Int,
    @SerializedName("smsresponse")
    val smsresponse: Any?,
    @SerializedName("smsstatus")
    val smsstatus: Any?
)

data class OtpResponseModel(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("error_code")
    val errorCode: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("session_id")
    val sessionId: String
)