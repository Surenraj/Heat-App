package com.thinkgas.heatapp.ui.feasibility

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
class FeasibilityViewModel @Inject constructor(private val repository: TpiRepositoryImpl) :
    ViewModel() {

    val feasibilityInfoResponse = repository.feasibilityInfoValue
    val claimResponse = repository.claimResponse
    val cancelResponse = repository.fsCancelResponse
    val tpiTfsClaimResponse = repository.tpiTfsClaimResponse


    private var _feasibilityResponse = MutableLiveData<PagingData<Agent>>()
    var feasibilityResponse: LiveData<PagingData<Agent>> = _feasibilityResponse

    private var getFeasibilityListJob: Job? = null

    var fsResponse: Flow<PagingData<Agent>>? = null
    var searchResponse: Flow<PagingData<Agent>>? = null



    fun getFeasibilityInfo(params: Map<String, String>) {
       viewModelScope.launch {
            repository.getFeasibilityInfo(params)
        }
    }

    fun getFeasibilityList(status: String?,sessionId:String):Flow<PagingData<Agent>> {
        val lastResult = fsResponse
        if (lastResult != null) {
            return lastResult
        }
        val result = repository.getFeasibilityList(status,sessionId).cachedIn(viewModelScope)
        fsResponse = result
        return result
    }

    fun updateClaimStatus(params: Map<String, String>){
        viewModelScope.launch {
            repository.updateFeasibilityClaimStatus(params)
        }
    }

    fun cancelFeasibility(params: Map<String, String>){
        viewModelScope.launch {
            repository.cancelFeasibility(params)
        }
    }

    fun searchFeasibilityList(query: String, status: String?,sessionId: String):Flow<PagingData<Agent>>{
        val lastResult = searchResponse
        if(lastResult!=null){
            return lastResult
        }
        val result = repository.searchFeasibilityList(query, status,sessionId).cachedIn(viewModelScope)
        searchResponse = result
        return result
    }

    fun tpiTfsClaimResponse(params: Map<String, String>){
        viewModelScope.launch {
            repository.tpiTfsClaimUpdate(params)
        }
    }

    fun stopAllJobs(){
        getFeasibilityListJob?.cancel("cancelled")
    }
}