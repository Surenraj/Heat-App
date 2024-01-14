package com.thinkgas.heatapp.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thinkgas.heatapp.data.repository.TpiRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private val repositoryImpl: TpiRepositoryImpl) :
    ViewModel() {

    val profileResponse = repositoryImpl.profileResponseValue

    fun getProfileResponse(param: String) {
        viewModelScope.launch {
            repositoryImpl.getProfileInfo(param)
        }
    }
}