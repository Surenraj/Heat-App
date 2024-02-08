package com.thinkgas.heatapp.ui.rfc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.thinkgas.heatapp.data.remote.model.Agent
import com.thinkgas.heatapp.data.repository.TpiRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RfcViewModel @Inject constructor(private val repository:TpiRepositoryImpl):ViewModel(){

    val rfcInfoResponse = repository.rfcInfoResponse

    val cancelResponse = repository.rfcCancelResponse

    val tpiRfcClaimResponse =  repository.tpiRfcClaimResponse

    var rfcResponse: Flow<PagingData<Agent>>? = null

    var searchResponse: Flow<PagingData<Agent>>? = null

    val rfcClaimResponse = repository.rfcClaimResponse

    fun getRfcInfoResponse(params: HashMap<String,String>){
        viewModelScope.launch {
            repository.getRfcInfo(params)
        }
    }

    fun getRfcList(sessionId:String?,status:String):Flow<PagingData<Agent>> {
        val lastResult = rfcResponse
        if (lastResult != null) {
            return lastResult
        }
        val result = repository.getRfcList(sessionId,status).cachedIn(viewModelScope)
        rfcResponse = result
        return result
    }

    fun searchRfcList(sessionId: String?, query: String,status: String):Flow<PagingData<Agent>> {
        val lastResult = searchResponse
        if (lastResult != null) {
            return lastResult
        }
        val result = repository.searchRfcList(sessionId,query,status).cachedIn(viewModelScope)
        searchResponse = result
        return result
    }

    fun updateRfcClaimStatus(params: HashMap<String, String>){
        viewModelScope.launch {
            repository.updateRfcClaimStatus(params)
        }
    }

    fun tpiRfcClaimUpdate(params: HashMap<String,String>){
        viewModelScope.launch {
            repository.tpiRfcClaimUpdate(params)
        }
    }

    fun cancelRfc(params: HashMap<String, String>){
        viewModelScope.launch {
            repository.cancelRfc(params)
        }
    }


}