package com.thinkgas.heatapp

import androidx.lifecycle.ViewModel

class RFCExtensionViewModel: ViewModel() {

    private var _qrValue: String? = null
    val qrValue: String?
        get() = _qrValue

    fun setQrValue(value: String) {
        _qrValue = value
    }
}