package com.thinkgas.heatapp.data.remote.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class GcUnregisterModel(
    var customerName:String,
    var mobileNumber:String,
    var towerNo:String,
    var houseNo:String,
    var floorNo:String,
    var floorFacing:String,
    var gassification:String,
    var gaId:String,
    var zonalId:String,
    var caId:String,
    var colonyId:String,
    var districtId: String,
    var talukaId:String,
    var cityId:String,
    var areaId:String,
    var pincodeId:String,
    var state:String,
    var landmark:String
):Parcelable

@Parcelize
data class GcUnregisterListModel(
    var customerName:String,
    var mobileNumber:String,
    var towerNo:String,
    var houseNo:String,
    var floorNo:String,
    var floorFacing:String,
    var gassification:String,
    var gaId:String,
    var zonalId:String,
    var caId:String,
    var colonyId:String,
    var districtId: String,
    var talukaId:String,
    var cityId:String,
    var areaId:String,
    var pincodeId:String,
    var gaName:String,
    var zonalName:String,
    var caName:String,
    var colonyName:String,
    var districtName: String,
    var talukaName:String,
    var cityName:String,
    var areaName:String,
    var pincodeNo:String,
    var state:String,
    var stateId:String,
    var landmark: String,
    var statusType: String,
    var subStatus: String,
    var gcDate: String,
    var potential: String,
    var statusTypeId:Int,
    var subStatusCode: Int,
    var lmcGcAlignment: String,
    var lmcStatus: String,
    var gcStatus:String,
    var gcContractor:String,
    var gcSupervisor:String,
    var consentTaken: String,
    var warningAvailable: String
):Parcelable

