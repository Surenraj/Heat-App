package com.thinkgas.heatapp.data.remote.model


import com.google.gson.annotations.SerializedName

data class ProfileResponseModel(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("heat_app_user_details")
    val heatAppUserDetails: HeatAppUserDetails
)

data class HeatAppUserDetails(
    @SerializedName("agent_count")
    val agentCount: Int,
    @SerializedName("agent_id")
    val agentId: String,
    @SerializedName("agent_name")
    val agentName: String,
    @SerializedName("heat_app_executive_name")
    val heatAppExecutiveName: String,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("is_tpi")
    val isTpi:Boolean,
    @SerializedName("lead_count")
    val leadCount: Int,
    @SerializedName("local_office_address")
    val localOfficeAddress: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("roles_permission")
    val rolesPermission: List<RolesPermission>,
    @SerializedName("session_id")
    val sessionId: String,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("member_since")
    val dateOfJoining:String
)

data class RolesPermission(
    @SerializedName("log_roles")
    val logRoles: String
)

data class TpiCategory(
    val category: String,
    val icon: Int,
    val id: String
)