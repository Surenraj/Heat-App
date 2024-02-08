package com.thinkgas.heatapp.ui.gc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thinkgas.heatapp.data.remote.model.GcUploadRequestModel
import com.thinkgas.heatapp.data.remote.model.UploadRequestModel
import com.thinkgas.heatapp.data.repository.TpiRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class GcStatusViewModel @Inject constructor(private var repository: TpiRepositoryImpl) :ViewModel(){

    val gcSubmitResponse = repository.gcSubmitResponse
    val tpiListResponse = repository.tpiListValue
    val uploadResponse = repository.uploadResponse
    val viewAttachmentResponse = repository.viewAttachmentResponse
    val deleteAttachmentResponse = repository.deleteAttachmentResponse

    val unregisterResponse = repository.gcUnregisterReponse


    fun unregisterCustomer(params: Map<String, String>){
        viewModelScope.launch {
            repository.unregisterCustomer(params)
        }
    }


    fun getTpiListTypes(params: Map<String, String>) {
        viewModelScope.launch {
            repository.getFeasibilityCategories(params)
        }
    }

    fun submitGc(params:Map<String,String>){
        viewModelScope.launch{
            repository.submitGc(params)
        }
    }

    fun uploadAttachment(params: UploadRequestModel, files: File, type:String){
        viewModelScope.launch {
            repository.uploadAttachments(params, files,type)
        }
    }

    fun uploadGcAttachment(params: GcUploadRequestModel, files: File, type:String){
        viewModelScope.launch {
            repository.uploadGcAttachments(params, files,type)
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
}