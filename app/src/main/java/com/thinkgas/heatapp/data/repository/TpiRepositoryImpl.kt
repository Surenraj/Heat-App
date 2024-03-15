package com.thinkgas.heatapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.thinkgas.heatapp.data.remote.api.TpiApiService
import com.thinkgas.heatapp.data.remote.model.*
import com.thinkgas.heatapp.data.remote.paging.*
import com.thinkgas.heatapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject


class TpiRepositoryImpl @Inject constructor(private val apiService: TpiApiService) : TpiRepository {
    private val _otpResponseValue = MutableLiveData<Resource<LoginResponseModel>>()
    val otpResponseValue: LiveData<Resource<LoginResponseModel>>
        get() = _otpResponseValue

    private val _otpValidateValue = MutableLiveData<Resource<OtpResponseModel>>()
    val otpValidateValue: LiveData<Resource<OtpResponseModel>>
        get() = _otpValidateValue

    private val _profileResponseValue = MutableLiveData<Resource<ProfileResponseModel>>()
    val profileResponseValue: LiveData<Resource<ProfileResponseModel>>
        get() = _profileResponseValue

    private val _feasibilityInfoValue = MutableLiveData<Resource<FeasibilityInfoResponse>>()
    val feasibilityInfoValue: LiveData<Resource<FeasibilityInfoResponse>>
        get() = _feasibilityInfoValue

    private val _tpiListValue = MutableLiveData<Resource<TpiListModel>>()
    val tpiListValue: LiveData<Resource<TpiListModel>>
        get() = _tpiListValue

    private val _claimResponse = MutableLiveData<Resource<HeatResponse>>()
    val claimResponse : LiveData<Resource<HeatResponse>>
        get() = _claimResponse

    private val _tpiTfsClaimResponse = MutableLiveData<Resource<HeatResponse>>()
    val tpiTfsClaimResponse : LiveData<Resource<HeatResponse>>
        get() = _tpiTfsClaimResponse

    private val _tpiLmcClaimResponse = MutableLiveData<Resource<HeatResponse>>()
    val tpiLmcClaimResponse : LiveData<Resource<HeatResponse>>
        get() = _tpiLmcClaimResponse

    private val _tpiRfcClaimResponse = MutableLiveData<Resource<HeatResponse>>()
    val tpiRfcClaimResponse : LiveData<Resource<HeatResponse>>
        get() = _tpiRfcClaimResponse

    private val _fsCancelResponse = MutableLiveData<Resource<HeatResponse>>()
    val fsCancelResponse : LiveData<Resource<HeatResponse>>
        get() = _fsCancelResponse

    private val _fsSubmitResponse = MutableLiveData<Resource<FsSubmitResponse>>()
    val fsSubmitResponse: LiveData<Resource<FsSubmitResponse>>
        get() = _fsSubmitResponse

    private val _lmcSubmitResponse = MutableLiveData<Resource<FsSubmitResponse>>()
    val lmcSubmitResponse: LiveData<Resource<FsSubmitResponse>>
        get() = _lmcSubmitResponse

    private val _uploadResponse = MutableLiveData<Resource<HeatResponse>>()
    val uploadResponse: LiveData<Resource<HeatResponse>>
        get() = _uploadResponse

    private val _lmcInfoResponse = MutableLiveData<Resource<LmcInfoResponse>>()
    val lmcInfoResponse: LiveData<Resource<LmcInfoResponse>>
        get() = _lmcInfoResponse

    private val _lmcClaimResponse = MutableLiveData<Resource<HeatResponse>>()
    val lmcClaimResponse : LiveData<Resource<HeatResponse>>
        get() = _lmcClaimResponse

    private val _lmcCancelResponse = MutableLiveData<Resource<HeatResponse>>()
    val lmcCancelResponse : LiveData<Resource<HeatResponse>>
        get() = _lmcCancelResponse

    private val _followUpResponse = MutableLiveData<Resource<HeatResponse>>()
    val followUpResponse : LiveData<Resource<HeatResponse>>
        get() = _followUpResponse

    private val _viewAttachmentResponse = MutableLiveData<Resource<ViewAttachmentResponse>>()
    val viewAttachmentResponse : LiveData<Resource<ViewAttachmentResponse>>
        get() = _viewAttachmentResponse

    private val _deleteAttachmentResponse = MutableLiveData<Resource<HeatResponse>>()
    val deleteAttachmentResponse : LiveData<Resource<HeatResponse>>
        get() = _deleteAttachmentResponse

    private val _rfcInfoResponse = MutableLiveData<Resource<LmcInfoResponse>>()
    val rfcInfoResponse: LiveData<Resource<LmcInfoResponse>>
        get() = _rfcInfoResponse

    private val _rfcClaimResponse = MutableLiveData<Resource<HeatResponse>>()
    val rfcClaimResponse : LiveData<Resource<HeatResponse>>
        get() = _rfcClaimResponse

    private val _rfcCancelResponse = MutableLiveData<Resource<HeatResponse>>()
    val rfcCancelResponse : LiveData<Resource<HeatResponse>>
        get() = _rfcCancelResponse

    private val _customerResponse = MutableLiveData<Resource<HeatResponse>>()
    val customerResponse : LiveData<Resource<HeatResponse>>
        get() = _customerResponse

    private val _rfcStatusResponse = MutableLiveData<Resource<RfcStatusResponse>>()
    val rfcStatusResponse : LiveData<Resource<RfcStatusResponse>>
        get() = _rfcStatusResponse

    private val _rfcApprovalResponse = MutableLiveData<Resource<HeatResponse>>()
    val rfcApprovalResponse : LiveData<Resource<HeatResponse>>
        get() = _rfcApprovalResponse

    private val _ngApprovalResponse = MutableLiveData<Resource<NgApprovalResponse>>()
    val ngApprovalResponse : LiveData<Resource<NgApprovalResponse>>
        get() = _ngApprovalResponse

    private val _rfcNgupdateResponse = MutableLiveData<Resource<HeatResponse>>()
    val rfcNgupdateResponse: LiveData<Resource<HeatResponse>>
        get() = _rfcNgupdateResponse

    private val _gcInfoResponse = MutableLiveData<Resource<FeasibilityInfoResponse>>()
    val gcInfoResponse: LiveData<Resource<FeasibilityInfoResponse>>
        get() = _gcInfoResponse

    private val _gcCancelResponse = MutableLiveData<Resource<HeatResponse>>()
    val gcCancelResponse : LiveData<Resource<HeatResponse>>
        get() = _gcCancelResponse

    private val _gcClaimResponse = MutableLiveData<Resource<HeatResponse>>()
    val gcClaimResponse : LiveData<Resource<HeatResponse>>
        get() = _gcClaimResponse

    private val _tpiGcClaimResponse = MutableLiveData<Resource<HeatResponse>>()
    val tpiGcClaimResponse : LiveData<Resource<HeatResponse>>
        get() = _tpiGcClaimResponse

    private val _gcSubmitResponse = MutableLiveData<Resource<FsSubmitResponse>>()
    val gcSubmitResponse: LiveData<Resource<FsSubmitResponse>>
        get() = _gcSubmitResponse

    private val _gcUnregisterReponse = MutableLiveData<Resource<FsSubmitResponse>>()
    val gcUnregisterReponse: LiveData<Resource<FsSubmitResponse>>
        get() = _gcUnregisterReponse

    override suspend fun getOtp(params: Map<String, String>) {

        _otpResponseValue.value = Resource.loading(null)
        val response = apiService.getOtp(params)
        if (response.isSuccessful) {

            _otpResponseValue.value = Resource.success(response.body())
        } else {
            _otpResponseValue.value = Resource.error("Something went wrong!", response.code(), null)
        }
    }


    override suspend fun validateOtp(params: Map<String, String>) {
        _otpValidateValue.value = Resource.loading(null)
        val response = apiService.validateOtp(params)
        if (response.isSuccessful) {

            _otpValidateValue.value = Resource.success(response.body())
        } else {
            _otpValidateValue.value = Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun getProfileInfo(param: String) {
        _profileResponseValue.value = Resource.loading(null)
        val response = apiService.getProfileInfo(param)
        if (response.isSuccessful) {
            _profileResponseValue.value = Resource.success(response.body())
        } else {
            _profileResponseValue.value =
                Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun getFeasibilityInfo(params: Map<String, String>) {
        _feasibilityInfoValue.value = Resource.loading(null)
        val response = apiService.getFeasibilityInfo(params)
        if (response.isSuccessful) {
            _feasibilityInfoValue.value = Resource.success(response.body())
        } else {
            _feasibilityInfoValue.value =
                Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override fun getFeasibilityList(
        status: String?,
        sessionId:String
    ): Flow<PagingData<Agent>> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FeasibilityPagingSource(apiService, null, status,sessionId)
            }
        ).flow
    }

    override fun searchFeasibilityList(
        query: String?,
        status: String?,
        sessionId: String?
    ): Flow<PagingData<Agent>> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FeasibilityPagingSource(apiService, query, status,sessionId!!)
            }
        ).flow
    }

    override suspend fun updateFeasibilityClaimStatus(params: Map<String, String>) {
        _claimResponse.value = Resource.loading(null)
        val response = apiService.updateFeasibilityClaimStatus(params)
        if (response.isSuccessful) {
            _claimResponse.value = Resource.success(response.body())
        } else {
            _claimResponse.value = Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun tpiTfsClaimUpdate(params: Map<String, String>) {
        _tpiTfsClaimResponse.value = Resource.loading(null)
        val response = apiService.tpiTfsClaimUpdate(params)
        if (response.isSuccessful) {
            _tpiTfsClaimResponse.value = Resource.success(response.body())
        } else {
            _tpiTfsClaimResponse.value = Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun tpiLmcClaimUpdate(params: Map<String, String>) {
        _tpiLmcClaimResponse.value = Resource.loading(null)
        val response = apiService.tpiLmcClaimUpdate(params)
        if (response.isSuccessful) {
            _tpiLmcClaimResponse.value = Resource.success(response.body())
        } else {
            _tpiLmcClaimResponse.value = Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun tpiRfcClaimUpdate(params: Map<String, String>) {
        _tpiRfcClaimResponse.value = Resource.loading(null)
        val response = apiService.tpiRfcClaimUpdate(params)
        if (response.isSuccessful) {
            _tpiRfcClaimResponse.value = Resource.success(response.body())
        } else {
            _tpiRfcClaimResponse.value = Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun cancelFeasibility(params: Map<String, String>) {
        _fsCancelResponse.value = Resource.loading(null)
        val response = apiService.cancelFeasibility(params)
        if (response.isSuccessful) {
            _fsCancelResponse.value = Resource.success(response.body())
        } else {
            _fsCancelResponse.value = Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun getFeasibilityCategories(params: Map<String, String>) {
        _tpiListValue.value = Resource.loading(null)
        Log.d("api params before", params.toString())
        val response = apiService.getFeasibilityCategories(params)
        Log.d("api params after", params.toString())
        if (response.isSuccessful) {
            _tpiListValue.value = Resource.success(response.body())
        } else {
            _tpiListValue.value = Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun submitFeasibility(params: Map<String,String>) {
        _fsSubmitResponse.value = Resource.loading(null)
        val response = apiService.submitFeasibility(params)
        if (response.isSuccessful) {
            _fsSubmitResponse.value = Resource.success(response.body())
        } else {
            _fsSubmitResponse.value =
                Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun uploadAttachments(params: UploadRequestModel, files: File,type: String) {

        _uploadResponse.value = Resource.loading(null)

        val file = files.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData(type, files.name, file)

        val appNo =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(),params.appNo)
        val bpNo =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(),params.bpNumber)
        val sessionId =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(),params.sessionId)
//        val bpNo =
//            Gson().toJson(params.bpNumber).toRequestBody("multipart/form-data".toMediaTypeOrNull())
//        val sessionId =
//            Gson().toJson(params.sessionId).toRequestBody("multipart/form-data".toMediaTypeOrNull())

        try {
            val response = apiService.uploadAttachments(
                appNo = appNo, bpNo = bpNo, sessionId =sessionId, file = filePart

            )
            if (response.isSuccessful) {
                _uploadResponse.value = Resource.success(response.body())
            } else {
                _uploadResponse.value =
                    Resource.error(response.body()!!.message, response.code(), null)
            }

        } catch (e: Exception) {
            Log.d("submitFeasibility: ", e.message.toString())
            _uploadResponse.value = Resource.error(e.message.toString(), 404, null)

        }

    }

    override suspend fun uploadGcAttachments(params: GcUploadRequestModel, files: File,type: String) {

        _uploadResponse.value = Resource.loading(null)

        val file = files.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData(type, files.name, file)

        val status =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(),params.status)
        val mobile =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(),params.mobile)
        val sessionId =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(),params.sessionId)
//        val bpNo =
//            Gson().toJson(params.bpNumber).toRequestBody("multipart/form-data".toMediaTypeOrNull())
//        val sessionId =
//            Gson().toJson(params.sessionId).toRequestBody("multipart/form-data".toMediaTypeOrNull())

        try {
            val response = apiService.uploadGcAttachments(
               status =  status,mobile = mobile, sessionId =sessionId, file = filePart

            )
            if (response.isSuccessful) {
                _uploadResponse.value = Resource.success(response.body())
            } else {
                _uploadResponse.value =
                    Resource.error(response.body()!!.message, response.code(), null)
            }

        } catch (e: Exception) {
            Log.d("submitFeasibility: ", e.message.toString())
            _uploadResponse.value = Resource.error(e.message.toString(), 404, null)

        }

    }

    override suspend fun deleteAttachment(params: Map<String, String>) {
        _deleteAttachmentResponse.value = Resource.loading(null)
        val response = apiService.deleteAttachment(params)
        if (response.isSuccessful) {
            _deleteAttachmentResponse.value = Resource.success(response.body())
        } else {
            _deleteAttachmentResponse.value = Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun getLmcInfo(params: Map<String, String>) {
        _lmcInfoResponse.value = Resource.loading(null)
        val response = apiService.getLmcInfo(params)
        if (response.isSuccessful) {
            _lmcInfoResponse.value = Resource.success(response.body())
        } else {
            _lmcInfoResponse.value = Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override fun getLmcList(sessionId: String?,
                            status: String?): Flow<PagingData<Agent>> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                LmcPagingSource(apiService, sessionId, null,status)
            }
        ).flow
    }

    override fun searchLmcList(
        sessionId: String?,
        query: String?,
        status: String?
    ): Flow<PagingData<Agent>> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                LmcPagingSource(apiService, sessionId, query,status)
            }
        ).flow
    }

    override suspend fun updateLmcClaimStatus(params: Map<String, String>) {
        _lmcClaimResponse.value = Resource.loading(null)
        val response = apiService.updateLmcClaimStatus(params)
        if (response.isSuccessful) {
            _lmcClaimResponse.value = Resource.success(response.body())
        } else {
            _lmcClaimResponse.value = Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun cancelLmc(params: Map<String, String>) {
        _lmcCancelResponse.value = Resource.loading(null)
        val response = apiService.cancelLmc(params)
        if (response.isSuccessful) {
            _lmcCancelResponse.value = Resource.success(response.body())
        } else {
            _lmcCancelResponse.value = Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun updateCustomerDetails(params: Map<String, String>) {
        _customerResponse.value = Resource.loading(null)
        val response = apiService.updateCustomerDetails(params)
        if (response.isSuccessful) {
            _customerResponse.value = Resource.success(response.body())
        } else {
            _customerResponse.value =
                Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun updateFollowUpStatus(params: Map<String, String>) {
        _followUpResponse.value = Resource.loading(null)
        val response = apiService.updateFollowUpStatus(params)
        if (response.isSuccessful) {
            _followUpResponse.value = Resource.success(response.body())
        } else {
            _followUpResponse.value = Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun submitLmc(params: Map<String, String>) {
        _lmcSubmitResponse.value = Resource.loading(null)
        val response = apiService.submitLmc(params)
        if (response.isSuccessful) {
            _lmcSubmitResponse.value = Resource.success(response.body())
        } else {
            _lmcSubmitResponse.value =
                Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun viewAttachments(params: Map<String, String>) {
        _viewAttachmentResponse.value = Resource.loading(null)
        val response = apiService.viewAttachments(params)
        if (response.isSuccessful) {
            _viewAttachmentResponse.value = Resource.success(response.body())
        } else {
            _viewAttachmentResponse.value =
                Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun getRfcInfo(params: Map<String, String>) {
        _rfcInfoResponse.value = Resource.loading(null)
        val response = apiService.getRfcInfo(params)
        if (response.isSuccessful) {
            _rfcInfoResponse.value = Resource.success(response.body())
        } else {
            _rfcInfoResponse.value =
                Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override fun getRfcList(sessionId: String?, status: String?): Flow<PagingData<Agent>> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                RfcPagingSource(apiService, sessionId, null,status)
            }
        ).flow
    }

    override fun searchRfcList(
        sessionId: String?,
        query: String?,
        status: String?
    ): Flow<PagingData<Agent>> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                RfcPagingSource(apiService, sessionId, query,status)
            }
        ).flow
    }

    override suspend fun updateRfcClaimStatus(params: Map<String, String>) {
        _rfcClaimResponse.value = Resource.loading(null)
        val response = apiService.updateRfcClaimStatus(params)
        if (response.isSuccessful) {
            _rfcClaimResponse.value = Resource.success(response.body())
        } else {
            _rfcClaimResponse.value =
                Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun cancelRfc(params: Map<String, String>) {
        _rfcCancelResponse.value = Resource.loading(null)
        val response = apiService.cancelRfc(params)
        if (response.isSuccessful) {
            _rfcCancelResponse.value = Resource.success(response.body())
        } else {
            _rfcCancelResponse.value =
                Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun getRfcStatus(params: Map<String, String>) {
        _rfcStatusResponse.value = Resource.loading(null)
        val response = apiService.getRfcStatus(params)
        if (response.isSuccessful) {
            _rfcStatusResponse.value = Resource.success(response.body())
        } else {
            _rfcStatusResponse.value =
                Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun submitRfcApproval(params: Map<String, String>) {
        _rfcApprovalResponse.value = Resource.loading(null)
        val response = apiService.submitRfcApproval(params)
        if (response.isSuccessful) {
            _rfcApprovalResponse.value = Resource.success(response.body())
        } else {
            _rfcApprovalResponse.value =
                Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun getNgApprovalList(params: Map<String, String>) {
        _ngApprovalResponse.value = Resource.loading(null)
        val response = apiService.getNgApprovalList(params)
        if (response.isSuccessful) {
            _ngApprovalResponse.value = Resource.success(response.body())
        } else {
            _ngApprovalResponse.value =
                Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun updateRfcNg(params: Map<String, String?>) {
        _rfcNgupdateResponse.value = Resource.loading(null)
        val response = apiService.updateRfcNg(params)
        if (response.isSuccessful) {
            _rfcNgupdateResponse.value = Resource.success(response.body())
        } else {
            _rfcNgupdateResponse.value =
                Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun getGcInfo(params: Map<String, String>) {
        _gcInfoResponse.value = Resource.loading(null)
        val response = apiService.getGcInfo(params)
        if (response.isSuccessful) {
            _gcInfoResponse.value = Resource.success(response.body())
        } else {
            _gcInfoResponse.value =
                Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override fun getGcList(status: String?, sessionId: String): Flow<PagingData<Agent>> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                GcPagingSource(apiService, null, status,sessionId)
            }
        ).flow
    }

    override fun searchGcList(
        query: String?,
        status: String?,
        sessionId: String?
    ): Flow<PagingData<Agent>> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                GcPagingSource(apiService, query, status,sessionId!!)
            }
        ).flow
    }

    override suspend fun cancelGc(params: Map<String, String>) {
        _gcCancelResponse.value = Resource.loading(null)
        val response = apiService.cancelGc(params)
        if (response.isSuccessful) {
            _gcCancelResponse.value = Resource.success(response.body())
        } else {
            _gcCancelResponse.value =
                Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun updateGcClaimStatus(params: Map<String, String>) {
        _gcClaimResponse.value = Resource.loading(null)
        val response = apiService.updateGcClaimStatus(params)
        if (response.isSuccessful) {
            _gcClaimResponse.value = Resource.success(response.body())
        } else {
            _gcClaimResponse.value =
                Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun tpiGfcClaimUpdate(params: Map<String, String>) {
        _tpiGcClaimResponse.value = Resource.loading(null)
        val response = apiService.tpiGfcClaimUpdate(params)
        if (response.isSuccessful) {
            _tpiGcClaimResponse.value = Resource.success(response.body())
        } else {
            _tpiGcClaimResponse.value =
                Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun submitGc(params: Map<String, String>) {
        _gcSubmitResponse.value = Resource.loading(null)
        val response = apiService.submitGc(params)
        if (response.isSuccessful) {
            _gcSubmitResponse.value = Resource.success(response.body())
        } else {
            _gcSubmitResponse.value =
                Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override suspend fun unregisterCustomer(params: Map<String, String>) {
        _gcUnregisterReponse.value = Resource.loading(null)
        val response = apiService.unregisterCustomer(params)
        if (response.isSuccessful) {
            _gcUnregisterReponse.value = Resource.success(response.body())
        } else {
            _gcUnregisterReponse.value =
                Resource.error("Something went wrong!", response.code(), null)
        }
    }

    override fun getGcUnregList(sessionId: String): Flow<PagingData<Agent>> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                GcUnregPagingSource(apiService, null,sessionId)
            }
        ).flow
    }

    override fun searchGcUnregList(query: String?, sessionId: String?): Flow<PagingData<Agent>> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                GcUnregPagingSource(apiService, query,sessionId!!)
            }
        ).flow
    }


}