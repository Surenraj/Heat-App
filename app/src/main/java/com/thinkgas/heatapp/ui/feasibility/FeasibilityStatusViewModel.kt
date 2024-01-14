package com.thinkgas.heatapp.ui.feasibility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thinkgas.heatapp.data.remote.model.UploadRequestModel
import com.thinkgas.heatapp.data.repository.TpiRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FeasibilityStatusViewModel @Inject constructor(private val repository: TpiRepositoryImpl) :
    ViewModel() {
    val tpiListResponse = repository.tpiListValue
    val feasibilityResponse = repository.fsSubmitResponse
    val uploadResponse = repository.uploadResponse
    val viewAttachmentResponse = repository.viewAttachmentResponse
    val deleteAttachmentResponse = repository.deleteAttachmentResponse

    fun getTpiListTypes(params: Map<String, String>) {
        viewModelScope.launch {
            repository.getFeasibilityCategories(params)
        }
    }

    fun submitFeasibility(params: Map<String,String>) {
        viewModelScope.launch {
            repository.submitFeasibility(params)
        }
    }

    fun uploadAttachment(params: UploadRequestModel,files:File,type:String){
        viewModelScope.launch {
            repository.uploadAttachments(params, files,type)
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