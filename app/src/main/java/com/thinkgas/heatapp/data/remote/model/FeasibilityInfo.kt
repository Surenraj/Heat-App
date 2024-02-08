package com.thinkgas.heatapp.data.remote.model


import com.google.gson.annotations.SerializedName

data class FeasibilityInfoResponse(
    @SerializedName("agent_list")
    val agentList: List<CountList>
)

data class CountList(
    @SerializedName("done")
    val done: Int,
    @SerializedName("hold")
    val hold: Int,
    @SerializedName("pending")
    val pending: Int,
    @SerializedName("un_claimed")
    val unClaimed: Int,
    @SerializedName("failed")
    val failed: Int,
    @SerializedName("approved")
    val approved: Int,
    @SerializedName("declined")
    val declined: Int
)