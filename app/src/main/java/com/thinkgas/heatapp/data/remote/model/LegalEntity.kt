package com.thinkgas.heatapp.data.remote.model


import com.google.gson.annotations.SerializedName

data class LegalEntity(
    @SerializedName("entity_id")
    val entityId: Int?,
    @SerializedName("entity_name")
    val entityName: String?,
    @SerializedName("ga_id_list")
    val gaIdList: List<GaId?>?
)


data class GaId(
    @SerializedName("district_list")
    val districtList: List<District?>?,
    @SerializedName("ga_id")
    val gaId: Int?,
    @SerializedName("ga_name")
    val gaName: String?,
    @SerializedName("Zonal")
    val zonal: List<Zonal?>?
)

data class District(
    @SerializedName("distric_id")
    val districId: Int?,
    @SerializedName("distric_name")
    val districName: String?,
    @SerializedName("language")
    val language: Any?,
    @SerializedName("state_id")
    val stateId: String?,
    @SerializedName("state_name")
    val stateName: String?,
    @SerializedName("taluka_list")
    val talukaList: List<Taluka?>?
)

data class Zonal(
    @SerializedName("charge_area_list")
    val chargeAreaList: List<ChargeArea?>?,
    @SerializedName("zone_id")
    val zoneId: Int?,
    @SerializedName("zone_name")
    val zoneName: String?
)

data class ChargeArea(
    @SerializedName("charge_area")
    val chargeArea: String?,
    @SerializedName("charge_id")
    val chargeId: Int?,
    @SerializedName("colony_data_list")
    val colonyDataList: String?
)

data class Taluka(
    @SerializedName("charge_area")
    val chargeArea: String?,
    @SerializedName("city_list")
    val cityList: List<City?>?,
    @SerializedName("taluka_id")
    val talukaId: Int?,
    @SerializedName("taluka_name")
    val talukaName: String?
)

data class City(
    @SerializedName("area_list")
    val areaList: List<Area?>?,
    @SerializedName("city_id")
    val cityId: Int?,
    @SerializedName("city_name")
    val cityName: String?
)

data class Area(
    @SerializedName("area_id")
    val areaId: Int?,
    @SerializedName("area_name")
    val areaName: String?,
    @SerializedName("pin_code")
    val pinCode: List<PinCode?>?
)

data class PinCode(
    @SerializedName("pincode_id")
    val pincodeId: Int?,
    @SerializedName("pincode_no")
    val pincodeNo: String?
)