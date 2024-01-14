package com.thinkgas.heatapp.data.remote.model


import com.google.gson.annotations.SerializedName

data class TpiListModel(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("logout_time")
    val logoutTime: Int,
    @SerializedName("pipeline_list")
    val pipelineList: List<Pipeline>,
    @SerializedName("potential_list")
    val potentialList: List<Potential>,
    @SerializedName("riser_list")
    val riserList: List<Riser>,
    @SerializedName("session_out_message")
    val sessionOutMessage: String,
    @SerializedName("tpi_list")
    val tpiList: List<Tpi>,
    @SerializedName("lmc_list")
    val lmcList: List<Tpi>,
    @SerializedName("update_status")
    val updateStatus: Int,
    @SerializedName("lmc_type_list")
    val lmcTypeList: List<LmcType>,
    @SerializedName("lmc_meter_status_list")
    val lmcMeterStatusList: List<LmcType>,
    @SerializedName("lmc_execution_list")
    val lmcExtensionList: List<LmcType>,
    @SerializedName("lmc_meter_details")
    val lmcMeterDetails: List<LmcType>,
    @SerializedName("lmc_meter_list")
    val lmcMeterList: List<LmcType>,
    @SerializedName("lmc_property_list")
    val lmcPropertyList:List<LmcType>,
    @SerializedName("lmc_gas_type")
    val lmcGasType:List<LmcType>,
    @SerializedName("ng_status_list")
    val ngStatusList:List<LmcType>,
    @SerializedName("gc_status_list")
    val gcStatusList:List<GcType>,
    @SerializedName("ng_approval_burner_type_list")
    val burnerList:List<LmcType>,
    @SerializedName("ng_approval_hose_pipe_list")
    val hoseList:List<LmcType>,
    @SerializedName("ng_approval_nozzle_brass_65_list")
    val nozzle65List:List<LmcType>,
    @SerializedName("ng_approval_nozzle_brass_90_list")
    val nozzle90List:List<LmcType>,
    @SerializedName("ng_approval_nozzle_brass_110_list")
    val nozzle110List:List<LmcType>,
    @SerializedName("ng_approval_nozzle_brass_125_list")
    val nozzle125List:List<LmcType>,
    @SerializedName("floor_facing_list")
    val floorList:List<FloorFacingType>,
    @SerializedName("gassified_list")
    val gassifiedList:List<GassifiedType>,
    @SerializedName("legal_entity_list")
    val entityList:List<LegalEntity>,
    @SerializedName("floor_list")
    val fList: List<Floor>
)

data class Floor(
    @SerializedName("id")
    val id: Int,
    @SerializedName("riser_height")
    val floorNo:String
)

data class GcType(
    @SerializedName("gcid")
    val id:Int,
    @SerializedName("gcstatus")
    val gcStatus:String
)

data class LmcType(
    @SerializedName("id")
    val id:Int,
    @SerializedName("name")
    val name:String
)

data class GassifiedType(
    @SerializedName("gsid")
    val id:Int,
    @SerializedName("gass_status")
    val status:String
)

data class FloorFacingType(
    @SerializedName("ffcid")
    val id:Int,
    @SerializedName("floor_facing")
    val floor:String
)

data class Tpi(
    @SerializedName("id")
    val id: Int,
    @SerializedName("status_type")
    val statusType: String,
    @SerializedName("status")
    val status:String,
    @SerializedName("sub_state_list")
    val subStateList: List<SubState>
)

data class SubState(
    @SerializedName("sub_id")
    val subId: Int,
    @SerializedName("sub_status_type")
    val subStatusType: String
)

data class Riser(
    @SerializedName("id")
    val id: Int,
    @SerializedName("riser_category")
    val riserCategory: String
)

data class Pipeline(
    @SerializedName("id")
    val id: Int,
    @SerializedName("pipeline_category")
    val pipelineCategory: String
)

data class Potential(
    @SerializedName("id")
    val id: Int,
    @SerializedName("potential_type")
    val potentialType: String
)