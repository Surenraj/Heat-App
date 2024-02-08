package com.thinkgas.heatapp.data.remote.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LmcConnectionModel(
    var lmcGiClamp:String?,
    var lmcMlcClamp:String?,
    var lmcGiMfElbow:String?,
    var lmcGiFfElbow:String?,
    var lmcGi2:String?,
    var lmcGi3:String?,
    var lmcGi4:String?,
    var lmcGi6:String?,
    var lmcGi8:String?,
    var lmcGiTee:String?,
    var lmcMlcTee:String?,
    var lmcGiSocket:String?,
    var lmcMaleUnion:String?,
    var lmcFemaleUnion:String?,
    var lmcMeterBracket:String?,
    var lmcMeterSticker:String?,
    var lmcPlateMarker:String?,
    var lmcAdaptorGI:String?,
    var lmcAdaptorReg:String?,
    var lmcAdaptorMeter:String?,
    var lmcFemaleMeter:String?,
):Parcelable
