package com.thinkgas.heatapp.ui.gc

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
class GcViewModel @Inject constructor(private val repository: TpiRepositoryImpl) : ViewModel() {

    val gcInfoResponse =  repository.gcInfoResponse
    val gcCancelResponse = repository.gcCancelResponse
    val gcClaimStatus = repository.gcClaimResponse
    val tpiGcClaimStatus = repository.tpiGcClaimResponse

    var gcResponse: Flow<PagingData<Agent>>? = null
    var searchResponse: Flow<PagingData<Agent>>? = null

    fun getGcInfo(params: Map<String,String>){
        viewModelScope.launch {
            repository.getGcInfo(params)
        }
    }

    fun getGcList(status: String?,sessionId:String): Flow<PagingData<Agent>> {
        val lastResult = gcResponse
        if (lastResult != null) {
            return lastResult
        }
        val result = repository.getGcList(status,sessionId).cachedIn(viewModelScope)
        gcResponse = result
        return result
    }

    fun searchGcList(query: String, status: String?,sessionId: String):Flow<PagingData<Agent>>{
        val lastResult = searchResponse
        if(lastResult!=null){
            return lastResult
        }
        val result = repository.searchGcList(query, status,sessionId).cachedIn(viewModelScope)
        searchResponse = result
        return result
    }

    fun cancelGc(params: Map<String, String>){
        viewModelScope.launch {
            repository.cancelGc(params)
        }
    }

    fun updateGcClaimStatus(params: Map<String, String>){
        viewModelScope.launch {
            repository.updateGcClaimStatus(params)
        }
    }

    fun tpiGcClaimStatus(params: Map<String, String>){
        viewModelScope.launch {
            repository.tpiGfcClaimUpdate(params)
        }
    }
}