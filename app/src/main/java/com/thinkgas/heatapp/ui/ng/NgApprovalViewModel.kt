package com.thinkgas.heatapp.ui.ng

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thinkgas.heatapp.data.remote.model.UploadRequestModel
import com.thinkgas.heatapp.data.repository.TpiRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class NgApprovalViewModel @Inject constructor(private val repository: TpiRepositoryImpl):ViewModel() {

    val ngApprovalResponse = repository.ngApprovalResponse
    val tpiListResponse = repository.tpiListValue
    val uploadResponse = repository.uploadResponse
    val viewAttachmentResponse = repository.viewAttachmentResponse
    val rfcNgUpdateResponse = repository.rfcNgupdateResponse
    val deleteAttachmentResponse = repository.deleteAttachmentResponse


    fun getNgApprovalList(params: HashMap<String,String>){
        viewModelScope.launch {
            repository.getNgApprovalList(params)
        }
    }

    fun getTpiListTypes(params: Map<String, String>) {
        viewModelScope.launch {
            repository.getFeasibilityCategories(params)
        }
    }

    fun uploadAttachment(params: UploadRequestModel, files: File, type:String){
        viewModelScope.launch {
            repository.uploadAttachments(params, files,type)
        }
    }

    fun viewAttachments(params: Map<String, String>){
        viewModelScope.launch {
            repository.viewAttachments(params)
        }
    }

    fun updateRfcNg(params: Map<String, String?>){
        viewModelScope.launch {
            repository.updateRfcNg(params)
        }
    }

    fun deleteAttachment(params: Map<String, String>){
        viewModelScope.launch {
            repository.deleteAttachment(params)
        }
    }
}