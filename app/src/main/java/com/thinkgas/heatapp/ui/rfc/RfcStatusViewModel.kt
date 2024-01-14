package com.thinkgas.heatapp.ui.rfc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thinkgas.heatapp.data.remote.model.UploadRequestModel
import com.thinkgas.heatapp.data.repository.TpiRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RfcStatusViewModel @Inject constructor(private var repository: TpiRepositoryImpl):ViewModel() {

    val rfcStatusResponse = repository.rfcStatusResponse
    val viewAttachmentResponse = repository.viewAttachmentResponse
    val uploadResponse = repository.uploadResponse
    val rfcApprovalResponse = repository.rfcApprovalResponse
    val deleteAttachmentResponse = repository.deleteAttachmentResponse



    fun getRfcStatus(params: HashMap<String,String>){
        viewModelScope.launch {
            repository.getRfcStatus(params)
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

    fun submitRfcApproval(params: Map<String, String>){
        viewModelScope.launch {
            repository.submitRfcApproval(params)
        }
    }

    fun deleteAttachment(params: Map<String, String>){
        viewModelScope.launch {
            repository.deleteAttachment(params)
        }
    }
}