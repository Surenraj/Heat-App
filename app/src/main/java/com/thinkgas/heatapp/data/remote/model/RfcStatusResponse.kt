package com.thinkgas.heatapp.data.remote.model


import com.google.gson.annotations.SerializedName

data class RfcStatusResponse(
    @SerializedName("customer_info")
    val customerInfo: CustomerInfo,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message:String?,
    @SerializedName("installation_details")
    val installationDetails: InstallationDetails
)
data class InstallationDetails(
        @SerializedName("cementingofholes")
        val cementingOfHoles: String?,
        @SerializedName("clamping")
        val clamping: String?,
        @SerializedName("connectivity")
        val connectivity: String?,
        @SerializedName("contractorname")
        val contractorName: String?,
        @SerializedName("cuinstallation")
        val cuInstallation: String?,
        @SerializedName("endcap")
        val endCap: String?,
        @SerializedName("extrapipelength")
        val extraPipelength: String?,
        @SerializedName("gastype")
        val gasType: String?,
        @SerializedName("guinstallation")
        val guInstallation: String?,
        @SerializedName("initialreading")
        val initialReading: String?,
        @SerializedName("lmc_type")
        val lmcType: String?,
        @SerializedName("meterinstallation")
        val meterInstallation: String?,
        @SerializedName("metermake")
        val meterMake: String?,
        @SerializedName("meterno")
        val meterNo: String?,
        @SerializedName("metertype")
        val meterType: String?,
        @SerializedName("noofav")
        val noOfAv: String?,
        @SerializedName("paintingofgipipe")
        val paintingOfGiPipe: String?,
        @SerializedName("propertytype")
        val propertyType: String?,
        @SerializedName("pvcsleeve")
        val pvcSleeve: String?,
        @SerializedName("regulatorno")
        val regulatorNo: String?,
        @SerializedName("supervisiorname")
        val supervisorName: String?,
        @SerializedName("tfavail")
        val tfAvail: String?,
        @SerializedName("customer_Name")
        val customerName: String?,
        @SerializedName("noofiv")
        val noOfIv: String?,
        @SerializedName("hole_drilled")
        val holeDrilled: String?,
        @SerializedName("mcv_testing")
        val mcvTesting: String?,
        @SerializedName("sr_no")
        val srNo:String?,
        @SerializedName("drs_no")
        val drsNo:String?,
        @SerializedName("lmc_meter_installation_status")
        val lmcStatus:String?
    )

    data class CustomerInfo(
        @SerializedName("address")
        val address: String?,
        @SerializedName("application_number")
        val applicationNumber: String?,
        @SerializedName("bpnumber")
        val bpNumber: String?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("mobile")
        val mobile: String?,
        @SerializedName("customer_Name")
        val customerName: String?
    )