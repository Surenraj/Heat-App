package com.thinkgas.heatapp.data.remote.api

import com.thinkgas.heatapp.data.remote.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface TpiApiService {

    @POST("getOtp_heat_app_n.php")
    @FormUrlEncoded
    suspend fun getOtp(@FieldMap params: Map<String, String>): Response<LoginResponseModel>

    @POST("validateOtp_heat_app.php")
    @FormUrlEncoded
    suspend fun validateOtp(@FieldMap params: Map<String, String>): Response<OtpResponseModel>

    @POST("getProfile.php")
    @FormUrlEncoded
    suspend fun getProfileInfo(@Field("session_id") id: String): Response<ProfileResponseModel>

    @POST("get_feasibility_count_list.php")
    @FormUrlEncoded
    suspend fun getFeasibilityInfo(@FieldMap params: Map<String, String>): Response<FeasibilityInfoResponse>

    @POST("get_feasibility_claimed_list.php")
    @FormUrlEncoded
    suspend fun getFeasibilityList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("get_feasibility_hold_list.php")
    @FormUrlEncoded
    suspend fun getFeasibilityHoldList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("get_feasibility_done_list.php")
    @FormUrlEncoded
    suspend fun getFeasibilityDoneList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("get_feasibility_list.php")
    @FormUrlEncoded
    suspend fun getFeasibilityUnclaimedList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("get_feasibility_failed_list.php")
    @FormUrlEncoded
    suspend fun getFeasibilityFailedList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("get_tpi_feasibility_approved_list.php")
    @FormUrlEncoded
    suspend fun getFeasibilityApprovedList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("get_tpi_feasibility_declined_list.php")
    @FormUrlEncoded
    suspend fun getFeasibilityDeclinedList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("claim_update.php")
    @FormUrlEncoded
    suspend fun updateFeasibilityClaimStatus(@FieldMap params: Map<String, String>): Response<HeatResponse>

    @POST("tpi_tfs_claim_update.php")
    @FormUrlEncoded
    suspend fun tpiTfsClaimUpdate(@FieldMap params: Map<String, String>): Response<HeatResponse>

    @POST("fs_claim_cancel.php")
    @FormUrlEncoded
    suspend fun cancelFeasibility(@FieldMap params: Map<String, String>): Response<HeatResponse>


    @POST("heat_app_Config.php")
    @FormUrlEncoded
    suspend fun getFeasibilityCategories(@FieldMap params: Map<String, String>): Response<TpiListModel>

    @POST("fs_Registration.php")
    @FormUrlEncoded
    suspend fun submitFeasibility(
        @FieldMap
        params: Map<String, String>
    ): Response<FsSubmitResponse>


    @POST("uploadAttachments.php")
    @Multipart
    suspend fun uploadAttachments(@Part("application_number") appNo: RequestBody,
                                  @Part("bp_number") bpNo: RequestBody,
                                  @Part("session_id") sessionId: RequestBody,
                                  @Part file: MultipartBody.Part):Response<HeatResponse>

    @POST("uploadAttachments.php")
    @Multipart
    suspend fun uploadGcAttachments(@Part("unregstatus") status: RequestBody,
                                  @Part("mobile_number") mobile: RequestBody,
                                  @Part("session_id") sessionId: RequestBody,
                                  @Part file: MultipartBody.Part):Response<HeatResponse>

    @POST("get_lmc_count_list.php")
    @FormUrlEncoded
    suspend fun getLmcInfo(@FieldMap params: Map<String, String>): Response<LmcInfoResponse>

    @POST("get_lmc_claimed_list.php")
    @FormUrlEncoded
    suspend fun getLmcList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("get_lmc_hold_list.php")
    @FormUrlEncoded
    suspend fun getLmcHoldList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("get_lmc_done_list.php")
    @FormUrlEncoded
    suspend fun getLmcDoneList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("get_lmc_failed_list.php")
    @FormUrlEncoded
    suspend fun getLmcFailedList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("get_tpi_lmc_approved_list.php")
    @FormUrlEncoded
    suspend fun getLmcApprovedList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("get_tpi_lmc_declined_list.php")
    @FormUrlEncoded
    suspend fun getLmcDeclinedList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("get_lmc_list.php")
    @FormUrlEncoded
    suspend fun getLmcUnclaimedList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("lmc_claim_update.php")
    @FormUrlEncoded
    suspend fun updateLmcClaimStatus(@FieldMap params: Map<String, String>): Response<HeatResponse>

    @POST("tpi_lmc_claim_update.php")
    @FormUrlEncoded
    suspend fun tpiLmcClaimUpdate(@FieldMap params: Map<String, String>): Response<HeatResponse>

    @POST("tpi_rfc_claim_update.php")
    @FormUrlEncoded
    suspend fun tpiRfcClaimUpdate(@FieldMap params: Map<String, String>): Response<HeatResponse>

    @POST("lmc_cancel_claim_update.php")
    @FormUrlEncoded
    suspend fun cancelLmc(@FieldMap params: Map<String, String>): Response<HeatResponse>

    @POST("lmc_connection_update.php")
    @FormUrlEncoded
    suspend fun updateCustomerDetails(@FieldMap params: Map<String, String>): Response<HeatResponse>

    @POST("lmc_Registration.php")
    @FormUrlEncoded
    suspend fun submitLmc(@FieldMap params: Map<String, String>): Response<FsSubmitResponse>

    @POST("lmc_follow_up_status_update.php")
    @FormUrlEncoded
    suspend fun updateFollowUpStatus(@FieldMap params: Map<String, String>): Response<HeatResponse>

    @POST("viewAttachments.php")
    @FormUrlEncoded
    suspend fun viewAttachments(@FieldMap params: Map<String, String>): Response<ViewAttachmentResponse>

    @POST("deleteAttachment.php")
    @FormUrlEncoded
    suspend fun deleteAttachment(@FieldMap params: Map<String, String>): Response<HeatResponse>

    @POST("get_rfc_ng_count_list.php")
    @FormUrlEncoded
    suspend fun getRfcInfo(@FieldMap params: Map<String, String>): Response<LmcInfoResponse>

    @POST("get_rfc_claimed_list.php")
    @FormUrlEncoded
    suspend fun getRfcPendingList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("get_rfc_ng_hold_list.php")
    @FormUrlEncoded
    suspend fun getRfcHoldList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("get_rfc_ng_done_list.php")
    @FormUrlEncoded
    suspend fun getRfcDoneList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("get_tpi_rfc_ng_approved_list.php")
    @FormUrlEncoded
    suspend fun getRfcApprovedList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("get_tpi_rfc_ng_declined_list.php")
    @FormUrlEncoded
    suspend fun getRfcDeclinedList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("get_rfc_ng_failed_list.php")
    @FormUrlEncoded
    suspend fun getRfcFailedList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("get_rfc_ng_list.php")
    @FormUrlEncoded
    suspend fun getRfcUnclaimedList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("rfc_ng_claim_update.php")
    @FormUrlEncoded
    suspend fun updateRfcClaimStatus(@FieldMap params: Map<String, String>): Response<HeatResponse>

    @POST("rfc_ng_cancel_claim_update.php")
    @FormUrlEncoded
    suspend fun cancelRfc(@FieldMap params: Map<String, String>): Response<HeatResponse>

    @POST("get_rfc_status.php")
    @FormUrlEncoded
    suspend fun getRfcStatus(@FieldMap params: Map<String, String>):Response<RfcStatusResponse>

    @POST("rfc_ng_Registration.php")
    @FormUrlEncoded
    suspend fun submitRfcApproval(@FieldMap params: Map<String, String>):Response<HeatResponse>

    @POST("get_ng_approval_list.php")
    @FormUrlEncoded
    suspend fun getNgApprovalList(@FieldMap params: Map<String, String>): Response<NgApprovalResponse>

    @POST("rfc_ng_update.php")
    @FormUrlEncoded
    suspend fun updateRfcNg(@FieldMap params: Map<String, String?>): Response<HeatResponse>

    @POST("gc_get_feasibility_count_list.php")
    @FormUrlEncoded
    suspend fun getGcInfo(@FieldMap params: Map<String, String>): Response<FeasibilityInfoResponse>

    @POST("gc_get_feasibility_claimed_list.php")
    @FormUrlEncoded
    suspend fun getGcClaimedList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("gc_get_feasibility_done_list.php")
    @FormUrlEncoded
    suspend fun getGcDoneList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("gc_get_feasibility_failed_list.php")
    @FormUrlEncoded
    suspend fun getGcFailedList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("gc_get_feasibility_hold_list.php")
    @FormUrlEncoded
    suspend fun getGcHoldList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("gc_get_feasibility_list.php")
    @FormUrlEncoded
    suspend fun getGcUnclaimedList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("get_tpi_gc_approved_list.php")
    @FormUrlEncoded
    suspend fun getGcApprovedList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("get_tpi_gc_declined_list.php")
    @FormUrlEncoded
    suspend fun getGcDeclinedList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>

    @POST("gc_claim_update.php")
    @FormUrlEncoded
    suspend fun updateGcClaimStatus(@FieldMap params: Map<String, String>): Response<HeatResponse>

    @POST("gc_claim_cancel.php")
    @FormUrlEncoded
    suspend fun cancelGc(@FieldMap params: Map<String, String>): Response<HeatResponse>

    @POST("gc_Registration.php")
    @FormUrlEncoded
    suspend fun submitGc(@FieldMap params: Map<String, String>): Response<FsSubmitResponse>

    @POST("tpi_gfc_claim_update.php")
    @FormUrlEncoded
    suspend fun tpiGfcClaimUpdate(@FieldMap params: Map<String, String>): Response<HeatResponse>

    @POST("gc_unreg_customer.php")
    @FormUrlEncoded
    suspend fun unregisterCustomer(@FieldMap params: Map<String, String>): Response<FsSubmitResponse>

    @POST("gc_heat_app_get_unreg_customer_list.php")
    @FormUrlEncoded
    suspend fun getGcUnregList(@FieldMap params: Map<String, String>): Response<FeasibilityListResponse>


}