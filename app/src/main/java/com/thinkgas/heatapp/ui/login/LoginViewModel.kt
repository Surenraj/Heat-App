package com.thinkgas.heatapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thinkgas.heatapp.data.repository.TpiRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: TpiRepositoryImpl) : ViewModel() {

    val otpValue = repository.otpResponseValue
    val validateValue = repository.otpValidateValue

    fun getOtpValue(params: Map<String, String>) {
        viewModelScope.launch {
            repository.getOtp(params)
        }
    }

    fun validateOtpValue(params: Map<String, String>) {
        viewModelScope.launch {
            repository.validateOtp(params)
        }
    }
}