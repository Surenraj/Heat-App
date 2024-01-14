package com.thinkgas.heatapp.data.remote.model


import com.google.gson.annotations.SerializedName


data class FeasibilityListResponse(
    @SerializedName("agent_list")
    val agentList: List<Agent>,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("page_count")
    val pageCount: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("record_count")
    val recordCount: Int
)

data class Agent(
    @SerializedName("address")
    val address: String,
    @SerializedName("application_number")
    val applicationNumber: String,
    @SerializedName("bpnumber")
    val bpnumber: String,
    @SerializedName("dateofassign")
    val claimedDate: String,
    @SerializedName("CustomerName")
    val customerName: String,
    @SerializedName("claimed_date")
    val dateOfAssign: String,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("mobile_no")
    val mobileNo: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("tpi_id")
    val tpiId: Int,
    @SerializedName("tpi_name")
    val TpiName: String,
    @SerializedName("geo_id")
    val geoId: Int,
    @SerializedName("zonal_id")
    val zonalId: Int,
    @SerializedName("lmc_claimed_status")
    val claimStatus: String,
    @SerializedName("status_type_id")
    val statusTypeId: Int,
    @SerializedName("sub_status_id")
    val subStatusId: Int,
    @SerializedName("pipeline_category_id")
    val pipelineId:Int,
    @SerializedName("description")
    val description:String,
    @SerializedName("firstname")
    val firstName:String,
    @SerializedName("middlename")
    val middleName:String,
    @SerializedName("lastname")
    val lastName:String,
    @SerializedName("email")
    val email:String,
    @SerializedName("lmc_type")
    val lmcType:String?,
    @SerializedName("meter_details")
    val meterDetails:String?,
    @SerializedName("meter_no")
    val meterNo:String?,
    @SerializedName("meter_sno")
    val meterSerialNumber:String?,
    @SerializedName("meter_type")
    val meterType:String?,
    @SerializedName("initial_meter_reading")
    val initialReading:String?,
    @SerializedName("regulator_number")
    val regNo:String?,
    @SerializedName("GI_install_meter")
    val giMeter:String?,
    @SerializedName("CU_install_meter")
    val cuMeter:String?,
    @SerializedName("no_of_av")
    val avNo:String?,
    @SerializedName("no_of_iv")
    val ivNo:String?,
    @SerializedName("extra_pipe_length")
    val pipeLength:String?,
    @SerializedName("property_type")
    val propertyType:String?,
    @SerializedName("gas_type")
    val gasType:String?,
    @SerializedName("pvc_sleeve")
    val pvcSleeve:String?,
    @SerializedName("meter_installation")
    val meterInstallation:String?,
    @SerializedName("clamping")
    val clamping:String?,
    @SerializedName("gas_meter_testing")
    val gasTesting:String?,
    @SerializedName("cementing_of_holes")
    val coh:String?,
    @SerializedName("painting_of_GI_pipe")
    val paintingPipe:String?,
    @SerializedName("connectivity")
    val connectivity:String?,
    @SerializedName("enc_cap")
    val endCap:String?,
    @SerializedName("TF_avail")
    val tfAvail:String?,
    @SerializedName("area_gassified")
    val areaGassified:String?,
    @SerializedName("hole_drilled")
    val holeDrilled:String?,
    @SerializedName("mcv_testing")
    val mcvTesting:String?,
    @SerializedName("cust_sat_ready_to_get_status")
    val custReadyStatus:String?,
    @SerializedName("ng_conv_date_status")
    val ngConvDate:String?,
    @SerializedName("status_type")
    val statusType:String?,
    @SerializedName("sub_status")
    val subStatus:String,
    @SerializedName("riser_status")
    val riserStatus:String?,
    @SerializedName("riser_length")
    val riserLength:String?,
    @SerializedName("gi_pipelength")
    val giPipelength:String?,
    @SerializedName("mlc_pipelength")
    val mlcPipelength:String?,
    @SerializedName("gc_status")
    val gcStatus:String?,
    @SerializedName("tpi_mobile_no")
    val tpiMobileNo:String?,
    @SerializedName("lmc_acknowledge_status")
    val ackStatus:String?,
    @SerializedName("tpi_approval_status")
    val tpiApprovalStatus:String?,
    @SerializedName("supervisor_mobile_no")
    val supervisorMobile:String?,
    @SerializedName("supervisor_name")
    val supervisorName:String?,
    @SerializedName("extra_gi_length")
    val extraGiLength:String?,
    @SerializedName("extra_mlc_length")
    val extraMlcLength:String?,
    @SerializedName("corrosion_tape")
    val acTape:String?,
    @SerializedName("follow_up_date")
    val followUpDate:String?,
    @SerializedName("sr_no")
    val srNo:String?,
    @SerializedName("drs_no")
    val drsNo:String?,
    @SerializedName("gc_date")
    val gcDate:String?,
    @SerializedName("gc_number")
    val gcNumber:String?,
    @SerializedName("potential")
    val potential:String?,
    @SerializedName("registered_customer_application_list")
    val gcApplication:String?,
    @SerializedName("lmc_status")
    val lmcStatus:String?,
    @SerializedName("gc_type")
    val gcType:String?,
    @SerializedName("lmc_gc_alignment")
    val lmcGcAlignment:String?,
    @SerializedName("gc_contractor")
    val gcContractor:String?,
    @SerializedName("gc_supervisor")
    val gcSupervisor:String?,
    @SerializedName("un_reg_gc_number")
    val gcUnregNumber:String?,
    @SerializedName("tower_no") var towerNo: String?,
    @SerializedName("house_no") var houseNo: String?,
    @SerializedName("floor_no") var floorNo: String?,
    @SerializedName("floor_facing") var floorFacing: String?,
    @SerializedName("gassification") var gassification: String?,
    @SerializedName("landmark") var landmark: String?,
    @SerializedName("ganame") var ganame: String?,
    @SerializedName("zonal_name") var zonalName: String?,
    @SerializedName("charge_area_id") var chargeAreaId: String?,
    @SerializedName("charge_area_name") var chargeAreaName: String?,
    @SerializedName("colony_id") var colonyId: String?,
    @SerializedName("colony_name") var colonyName: String?,
    @SerializedName("district_id") var districtId: String?,
    @SerializedName("district_name") var districtName: String?,
    @SerializedName("taluka_id") var talukaId: String?,
    @SerializedName("taluka_name") var talukaName: String?,
    @SerializedName("city_id") var cityId: String?,
    @SerializedName("city_name") var cityName: String?,
    @SerializedName("area_id") var areaId: String?,
    @SerializedName("area_name") var areaName: String?,
    @SerializedName("pincode_id") var pincodeId: String?,
    @SerializedName("pincode") var pincode: String?,
    @SerializedName("state_id") var stateId: String?,
    @SerializedName("state_name") var stateName: String?,
    @SerializedName("gc_supervisor_name")
    val gcSupervisorName:String?,
    @SerializedName("lmc_execution")
    val lmcExecution:String?,
    @SerializedName("gi_clamp")
    val giClamp:String?,
    @SerializedName("mlc_clamp")
    val mlcClamp:String?,
    @SerializedName("gi_MF_elbow")
    val giMfElbow:String?,
    @SerializedName("gi_FF_elbow")
    val giFfElbow:String?,
    @SerializedName("gi_2_nipple")
    val gi2Nipple:String?,
    @SerializedName("gi_3_nipple")
    val gi3Nipple:String?,
    @SerializedName("gi_4_nipple")
    val gi4Nipple:String?,
    @SerializedName("gi_6_nipple")
    val gi6Nipple:String?,
    @SerializedName("gi_8_nipple")
    val gi8Nipple:String?,
    @SerializedName("gi_tee")
    val giTee:String?,
    @SerializedName("mlc_tee")
    val mlcTee:String?,
    @SerializedName("gi_socket")
    val giSocket:String?,
    @SerializedName("mlc_male_union")
    val mlcMaleUnion:String?,
    @SerializedName("mlc_female_union")
    val mlcFemaleUnion:String?,
    @SerializedName("meter_bracket")
    val meterBracket:String?,
    @SerializedName("meter_sticker")
    val meterSticker:String?,
    @SerializedName("plate_marker")
    val plateMarker:String?,
    @SerializedName("adaptor_GI_to_reg")
    val adaptorGi:String?,
    @SerializedName("adaptor_reg_to_meter")
    val adaptorReg:String?,
    @SerializedName("adaptor_meter_to_GI_pipe")
    val adaptorMeter:String?,
    @SerializedName("female_union_meter_MLC_pipe")
    val femaleUnion:String?,
    @SerializedName("consent_taken")
    val consentTaken: String?,
    @SerializedName("warning_plate_avb")
    val warningAvailable: String?
)

data class FsSubmitResponse(
    @SerializedName("application_no")
    val applicationNo: String,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("un_reg_gc_number")
    val gcNumber:String,
)

data class FsRequestModel(
    @SerializedName("application_number")
    val applicationNo: String,
    @SerializedName("tpi_id")
    val tpiId: String,
    @SerializedName("bp_number")
    val bpNumber: String,
    @SerializedName("customer_info")
    val customerName: String,
    @SerializedName("status_type_id")
    val statusId: String,
    @SerializedName("status_type")
    val status: String,
    @SerializedName("sub_status_id")
    val subStatusId: String,
    @SerializedName("sub_status")
    val subStatus: String,
    @SerializedName("pipeline_category_id")
    val pipelineId: String,
    @SerializedName("pipeline_category")
    val pipeline: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("fs_session_id")
    val sessionId: String,
    @SerializedName("fs_created_date_time")
    val fsTimeStamp: String,

    )

