package com.thinkgas.heatapp.data.cache

import com.thinkgas.heatapp.data.remote.model.SubState

object AppCache {

     val tpiMap: MutableMap<String, List<SubState>> = mutableMapOf()
     val tpiStatusMap: MutableMap<String, Int> = mutableMapOf()
    val fsStatus: MutableMap<Int, String> = mutableMapOf()
    val tpiSubStatusMap: MutableMap<String, Int> = mutableMapOf()
    val lmcTypeList = mutableMapOf<String,Int>()
    val lmcMeterStatusList = mutableMapOf<String,Int>()
    val lmcMeterDetails = mutableMapOf<String,Int>()
    val lmcPropertyList = mutableMapOf<String,Int>()
    val lmcMeterList = mutableMapOf<String,Int>()
    val lmcGasType = mutableMapOf<String,Int>()
    val ngStatusList = mutableMapOf<String,Int>()
    val ngBurnerList = mutableMapOf<String,Int>()
    val ngHoseList = mutableMapOf<String,Int>()
    val ngNozzle65List = mutableMapOf<String,Int>()
    val ngNozzle90List = mutableMapOf<String,Int>()
    val ngNozzle110List = mutableMapOf<String,Int>()
    val ngNozzle125List = mutableMapOf<String,Int>()
    val meterCompanyList = mutableMapOf<String,Int>()

    val lmcExtensionList = mutableMapOf<String,Int>()


    var latitude:Double? = 0.0
    var longitude:Double? = 0.0
    var isTpi:Boolean = false

}