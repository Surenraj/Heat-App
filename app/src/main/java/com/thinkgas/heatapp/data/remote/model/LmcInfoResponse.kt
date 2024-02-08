package com.thinkgas.heatapp.data.remote.model


import com.google.gson.annotations.SerializedName

data class LmcInfoResponse(
    @SerializedName("agent_list")
    val agentList: List<CountList>
)
