package com.thinkgas.heatapp.data.repository

import androidx.paging.PagingData
import com.thinkgas.heatapp.data.remote.model.Agent
import com.thinkgas.heatapp.data.remote.model.GcUploadRequestModel
import com.thinkgas.heatapp.data.remote.model.UploadRequestModel
import kotlinx.coroutines.flow.Flow
import java.io.File

interface TpiRepository {

    suspend fun getOtp(params: Map<String, String>)

    suspend fun validateOtp(params: Map<String, String>)

    suspend fun getProfileInfo(param: String)

    suspend fun getFeasibilityInfo(params: Map<String, String>)

    fun getFeasibilityList(
        status: String?,
        sessionId:String
    ): Flow<PagingData<Agent>>

    fun searchFeasibilityList(
        query: String?,
        status: String?,
        sessionId: String?
    ): Flow<PagingData<Agent>>

    suspend fun updateFeasibilityClaimStatus(params: Map<String, String>)

    suspend fun tpiTfsClaimUpdate(params: Map<String, String>)

    suspend fun tpiLmcClaimUpdate(params: Map<String, String>)

    suspend fun tpiRfcClaimUpdate(params: Map<String, String>)

    suspend fun cancelFeasibility(params: Map<String, String>)

    suspend fun getFeasibilityCategories(params: Map<String, String>)

    suspend fun submitFeasibility(params: Map<String,String>)

    suspend fun uploadAttachments(params: UploadRequestModel, files: File,type:String)

    suspend fun uploadGcAttachments(params: GcUploadRequestModel, files: File,type:String)


    suspend fun deleteAttachment(params: Map<String, String>)

    suspend fun getLmcInfo(params: Map<String, String>)

    fun getLmcList(sessionId: String?,
                   status: String?): Flow<PagingData<Agent>>

    fun searchLmcList(
        sessionId: String?,
        query: String?,
        status: String?
    ): Flow<PagingData<Agent>>

    suspend fun updateLmcClaimStatus(params: Map<String, String>)

    suspend fun cancelLmc(params: Map<String, String>)

    suspend fun updateCustomerDetails(params: Map<String, String>)

    suspend fun updateFollowUpStatus(params: Map<String, String>)

    suspend fun submitLmc(params: Map<String,String>)

    suspend fun viewAttachments(params: Map<String, String>)

    suspend fun getRfcInfo(params: Map<String, String>)

    fun getRfcList(sessionId: String?,
                   status: String?): Flow<PagingData<Agent>>

    fun searchRfcList(
        sessionId: String?,
        query: String?,
        status: String?
    ): Flow<PagingData<Agent>>

    suspend fun updateRfcClaimStatus(params: Map<String, String>)

    suspend fun cancelRfc(params: Map<String, String>)

    suspend fun getRfcStatus(params: Map<String, String>)

    suspend fun submitRfcApproval(params: Map<String, String>)

    suspend fun getNgApprovalList(params: Map<String, String>)

    suspend fun updateRfcNg(params: Map<String, String?>)

    suspend fun getGcInfo(params: Map<String, String>)

    fun getGcList(
        status: String?,
        sessionId:String
    ): Flow<PagingData<Agent>>

    fun searchGcList(
        query: String?,
        status: String?,
        sessionId: String?
    ): Flow<PagingData<Agent>>

    suspend fun cancelGc(params: Map<String, String>)

    suspend fun updateGcClaimStatus(params: Map<String, String>)

    suspend fun tpiGfcClaimUpdate(params: Map<String, String>)

    suspend fun submitGc(params: Map<String, String>)

    suspend fun unregisterCustomer(params: Map<String, String>)

    fun getGcUnregList(
        sessionId:String
    ): Flow<PagingData<Agent>>

    fun searchGcUnregList(
        query: String?,
        sessionId: String?
    ): Flow<PagingData<Agent>>

}