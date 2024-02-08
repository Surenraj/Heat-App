package com.thinkgas.heatapp.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.thinkgas.heatapp.data.remote.model.Agent
import com.thinkgas.heatapp.data.remote.model.GcUploadRequestModel
import com.thinkgas.heatapp.data.repository.TpiRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(private val repository: TpiRepositoryImpl):ViewModel(){

    val tpiListResponse = repository.tpiListValue
    val unregisterResponse = repository.gcUnregisterReponse
    val uploadResponse = repository.uploadResponse
    val viewAttachmentResponse = repository.viewAttachmentResponse
    val deleteAttachmentResponse = repository.deleteAttachmentResponse


    var gcResponse: Flow<PagingData<Agent>>? = null
    var searchResponse: Flow<PagingData<Agent>>? = null


    fun getTpiListTypes(params: Map<String, String>) {
        viewModelScope.launch {
            repository.getFeasibilityCategories(params)
        }
    }

    fun uploadGcAttachment(params: GcUploadRequestModel, files: File, type:String){
        viewModelScope.launch {
            repository.uploadGcAttachments(params, files,type)
        }
    }


    fun unregisterCustomer(params: Map<String, String>){
        viewModelScope.launch {
            repository.unregisterCustomer(params)
        }
    }

    fun viewAttachments(params: Map<String, String>){
        viewModelScope.launch {
            repository.viewAttachments(params)
        }
    }

    fun deleteAttachment(params: Map<String, String>){
        viewModelScope.launch {
            repository.deleteAttachment(params)
        }
    }

    fun getGcUnregList(sessionId:String): Flow<PagingData<Agent>> {
        val lastResult = gcResponse
        if (lastResult != null) {
            return lastResult
        }
        val result = repository.getGcUnregList(sessionId).cachedIn(viewModelScope)
        gcResponse = result
        return result
    }

    fun searchGcUnregList(query: String,sessionId: String):Flow<PagingData<Agent>>{
        val lastResult = searchResponse
        if(lastResult!=null){
            return lastResult
        }
        val result = repository.searchGcUnregList(query,sessionId).cachedIn(viewModelScope)
        searchResponse = result
        return result
    }


}