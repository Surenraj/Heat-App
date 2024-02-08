package com.thinkgas.heatapp.data.remote.model


import com.google.gson.annotations.SerializedName

data class ViewAttachmentResponse(
    @SerializedName("attachment_list")
    val attachmentList: List<Attachment>,
    @SerializedName("type")
    val type:String,
    @SerializedName("error")
    val error: Boolean
)

data class Attachment(
    @SerializedName("attachment_id")
    val attachmentId: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("file_name")
    val fileName: String,
    @SerializedName("type")
    val type:String
)