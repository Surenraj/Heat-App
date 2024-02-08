package com.thinkgas.heatapp.ui.lmc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.thinkgas.heatapp.data.remote.model.Agent
import com.thinkgas.heatapp.data.repository.TpiRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LmcViewModel @Inject constructor(private val repository: TpiRepositoryImpl) : ViewModel() {

    val lmcInfoResponse = repository.lmcInfoResponse

    val claimStatus = repository.lmcClaimResponse

    val cancelResponse = repository.lmcCancelResponse

    val tpiLmcClaimResponse = repository.tpiLmcClaimResponse

    var lmcResponse:Flow<PagingData<Agent>>? = null

    var searchResponse:Flow<PagingData<Agent>>? = null

    private var getLmcListJob: Job? = null


    fun getLmcInfo(params: Map<String, String>) {
        viewModelScope.launch {
            repository.getLmcInfo(params)
        }
    }

    fun getLmcList(sessionId:String?,status:String):Flow<PagingData<Agent>> {
        val lastResult = lmcResponse
        if (lastResult != null) {
            return lastResult
        }
        val result = repository.getLmcList(sessionId,status).cachedIn(viewModelScope)
        lmcResponse = result
        return result
    }

    fun searchLmcList(sessionId: String?, query: String,status: String):Flow<PagingData<Agent>> {
        val lastResult = searchResponse
        if (lastResult != null) {
            return lastResult
        }
        val result = repository.searchLmcList(sessionId,query,status).cachedIn(viewModelScope)
        searchResponse = result
        return result
    }

    fun updateLmcStatus(params: Map<String, String>){
        viewModelScope.launch {
            repository.updateLmcClaimStatus(params)
        }
    }

    fun cancelLmc(params: Map<String, String>){
        viewModelScope.launch {
            repository.cancelLmc(params)
        }
    }

    fun tpiLmcClaimUpdate(params: Map<String, String>){
        viewModelScope.launch {
            repository.tpiLmcClaimUpdate(params)
        }
    }

    fun stopAllJobs(){
        getLmcListJob?.cancel("cancelled")
    }


}