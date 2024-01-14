package com.thinkgas.heatapp.data.remote.model


import com.google.gson.annotations.SerializedName

data class NgApprovalResponse(
    @SerializedName("customer_info")
    val customerInfo: CustomerInformation?,
    @SerializedName("error")
    val error: Boolean?
)

data class CustomerInformation(
    @SerializedName("bpnumber")
    val bpnumber: String?,
    @SerializedName("customer_name")
    val customerName: String?,
    @SerializedName("installation_details")
    val installationDetails: NgInstallationDetails?,
    @SerializedName("extension_modification_lmc")
    val lmcExtensionWithMeter: LmcExtensionWithMeter?,
    @SerializedName("extension_modification_without_meter_lmc")
    val lmcExtensionWithoutMeter: LmcExtensionWithoutMeter?,
    @SerializedName("jmr_no")
    val jmrNo: String?,
    @SerializedName("ng_verification_details")
    val ngVerificationDetails: NgVerificationDetails?
)

data class LmcExtensionWithoutMeter(
    @SerializedName("wo_meter_company")
    val woMeterCompany: String?,
    @SerializedName("wo_meter_no")
    val woMeterNo: String?,
    @SerializedName("wo_initial_meter_reading")
    val woInitialReading: String?,
    @SerializedName("wo_regulator_number")
    val woRegulatorNo: String?,
    @SerializedName("wo_meter_bracket")
    val woMeterBracket: String?,
    @SerializedName("wo_meter_sticker")
    val woMeterSticker: String?,
    @SerializedName("wo_adaptor_GI_to_reg")
    val woAdaptorGi: String?,
    @SerializedName("wo_adaptor_reg_to_meter")
    val woAdaptorReg: String?,
    @SerializedName("wo_adaptor_meter_to_GI_pipe")
    val woAdaptorMeter: String?,
    @SerializedName("wo_female_union_meter_MLC_pipe")
    val woFemaleMeter: String?,
    @SerializedName("wo_gi_length")
    val woGiLength: String?,
    @SerializedName("wo_mlc_length")
    val woMlcLength: String?,
    @SerializedName("wo_extra_gi")
    val woExtraGi: String?,
    @SerializedName("wo_extra_mlc")
    val woExtraMlc: String?,
    @SerializedName("wo_iv_no")
    val woIvNo: String?,
    @SerializedName("wo_av_no")
    val woAvNo: String?

)

data class LmcExtensionWithMeter(
    @SerializedName("extension_modication_of_lmc")
    val lmcModification: String?,
    @SerializedName("lmc_meter_installation_status")
    val lmcMeterStatus: String?,
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
    @SerializedName("meter_no")
    val meterNo:String?,
    @SerializedName("regulator_no")
    val regulatorNo:String?

)

data class NgInstallationDetails(
    @SerializedName("black_tower")
    val blackTower: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("claim_date")
    val claimDate: String?,
    @SerializedName("customer_type")
    val customerType: String?,
    @SerializedName("floor")
    val floor: String?,
    @SerializedName("house_no")
    val houseNo: String?,
    @SerializedName("metermake")
    val metermake: String?,
    @SerializedName("meterno")
    val meterno: String?,
    @SerializedName("metertype")
    val metertype: String?,
    @SerializedName("mobile")
    val mobile: String?,
    @SerializedName("ng_meter_correct_status")
    val ngMeterCorrectStatus: Any?,
    @SerializedName("ng_select_status_id")
    val ngSelectStatusId: Any?,
    @SerializedName("rfc_date")
    val rfcDate: String?,
    @SerializedName("rfc_initial_reading")
    val rfcInitialReading: String?,
    @SerializedName("society")
    val society: String?,
    @SerializedName("rfc_status")
    val rfcStatus: String?,
    @SerializedName("mmt_testing")
    val mmtTesting: String?,
    @SerializedName("leakage_testing")
    val leakageTesting:String?,
    @SerializedName("gas_pressure")
    val gasPressure: String?,
    @SerializedName("meter_reading")
    val meterReading: String?,
    @SerializedName("burner_type")
    val burnerType:String?,
    @SerializedName("hose_pipe")
    val hosePipe: String?,
    @SerializedName("nozzle_65")
    val nozzle65:String?,
    @SerializedName("nozzle_90")
    val nozzle90:String?,
    @SerializedName("nozzle_110")
    val nozzle110:String?,
    @SerializedName("nozzle_125")
    val nozzle125:String?,
    @SerializedName("area")
    val area:String?,
    @SerializedName("ng_convertion_date")
    val ngConversionDate:String?,
    @SerializedName("ng_testing_leakage_acceptance")
    val acknowledgeId:String?,
    @SerializedName("drs_number")
    val drsNumber:String?,
    @SerializedName("sr_number")
    val srNumber:String?,
    @SerializedName("gi_union")
    val giUnion:String?,
    @SerializedName("follow_up_date")
    val followupDate:String?,
    @SerializedName("comment")
    val comment:String?
)

data class NgVerificationDetails(
    @SerializedName("assigned_date")
    val assignedDate: String?,
    @SerializedName("bpnumber_ng_vf")
    val bpnumberNgVf: String?,
    @SerializedName("jmr_no")
    val jmrNo: String?,
    @SerializedName("ng_burner_details")
    val ngBurnerDetails: Any?,
    @SerializedName("ng_initial_reading")
    val ngInitialReading: Any?
)