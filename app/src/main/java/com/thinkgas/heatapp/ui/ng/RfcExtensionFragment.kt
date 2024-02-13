package com.thinkgas.heatapp.ui.ng

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.thinkgas.heatapp.R
import com.thinkgas.heatapp.data.cache.AppCache
import com.thinkgas.heatapp.databinding.FragmentRfcExtensionBinding
import com.thinkgas.heatapp.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class RfcExtensionFragment : Fragment() {
    private var _binding: FragmentRfcExtensionBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<NgApprovalViewModel>()
    private var dialog: Dialog? = null
    private val args by navArgs<RfcExtensionFragmentArgs>()
    var companySpinnerDialog: SpinnerDialog? = null




    companion object {
        var lmcGiClamp:String? = null
        var lmcMlcClamp:String? = null
        var lmcGiMfElbow:String? = null
        var lmcGiFfElbow:String? = null
        var lmcGi2:String? = null
        var lmcGi3:String? = null
        var lmcGi4:String? = null
        var lmcGi6:String? = null
        var lmcGi8:String? = null
        var lmcGiTee:String? = null
        var lmcMlcTee:String? = null
        var lmcGiSocket:String? = null
        var lmcMaleUnion:String? = null
        var lmcFemaleUnion:String? = null
        var lmcMeterBracket:String? = null
        var lmcMeterSticker:String? = null
        var lmcPlateMarker:String? = null
        var lmcAdaptorGI:String? = null
        var lmcAdaptorReg:String? = null
        var lmcAdaptorMeter:String? = null
        var lmcFemaleMeter:String? = null
        var lmcMeterNo:String? = null
        var lmcRegulatorNo:String? = null
        var lmcGiLength:String? = null
        var lmcMlcLength:String? = null
        var lmcAvQty:String? = null
        var lmcIvQty:String? = null
        var lmcExtraGiLength:String? = null
        var lmcExtraMlcLength:String? = null
        var lmcMeterCompany:String? = null
        var lmcInitialReading:String? = null


    }

    var hasMeter = false
    var isLmcModified = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRfcExtensionBinding.inflate(inflater,container,false)

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.progress)
        dialog = builder.create()

        val paramsTpi = HashMap<String, String>()
        paramsTpi["version_code"] = "1"
        paramsTpi["os_type"] = "android"


        viewModel.getTpiListTypes(paramsTpi)
        setUpTpiObserver()

        val params = HashMap<String,String>()
        params["application_number"] = args.appNo
        params["session_id"] = args.sessionId.toString()
        params["is_tpi"] = if(AppCache.isTpi) "1" else "0"


        viewModel.getNgApprovalList(params)
        setUpObserver()

        binding.apply {

            etMeterCompany.setOnClickListener {
                companySpinnerDialog?.showSpinerDialog()
                companySpinnerDialog?.setCancellable(true)
            }

            if(AppCache.isTpi){
                etGiClamp.isEnabled = false
                etGiClamp.setTextColor(Color.parseColor("#545454"))
                etMlcClamp.isEnabled = false
                etMlcClamp.setTextColor(Color.parseColor("#545454"))
                etGiMf.isEnabled = false
                etGiMf.setTextColor(Color.parseColor("#545454"))
                etGiFf.isEnabled = false
                etGiFf.setTextColor(Color.parseColor("#545454"))
                etGi2.isEnabled = false
                etGi2.setTextColor(Color.parseColor("#545454"))
                etGi3.isEnabled = false
                etGi3.setTextColor(Color.parseColor("#545454"))
                etGi4.isEnabled = false
                etGi4.setTextColor(Color.parseColor("#545454"))
                etGi6.isEnabled = false
                etGi6.setTextColor(Color.parseColor("#545454"))
                etGi8.isEnabled = false
                etGi8.setTextColor(Color.parseColor("#545454"))
                etGiTee.isEnabled = false
                etGiTee.setTextColor(Color.parseColor("#545454"))
                etMlcTee.isEnabled = false
                etMlcTee.setTextColor(Color.parseColor("#545454"))
                etGiSocket.isEnabled = false
                etGiSocket.setTextColor(Color.parseColor("#545454"))
                etGiMf.isEnabled = false
                etGiMf.setTextColor(Color.parseColor("#545454"))
                etMlcMale.isEnabled = false
                etMlcMale.setTextColor(Color.parseColor("#545454"))
                etMlcFemale.setTextColor(Color.parseColor("#545454"))
                etMlcFemale.isEnabled = false
                etMeterNo.isEnabled = false
                etMeterNo.setTextColor(Color.parseColor("#545454"))
                etRegulatorNo.isEnabled = false
                etRegulatorNo.setTextColor(Color.parseColor("#545454"))
                etMeterCompany.isEnabled = false
                etMeterCompany.setTextColor(Color.parseColor("#545454"))
                etMeterBracket.isEnabled = false
                etMeterBracket.setTextColor(Color.parseColor("#545454"))
                etMeterSticker.isEnabled = false
                etMeterSticker.setTextColor(Color.parseColor("#545454"))
                etPlateMarker.isEnabled = false
                etPlateMarker.setTextColor(Color.parseColor("#545454"))
                etAdaptorGi.isEnabled = false
                etAdaptorGi.setTextColor(Color.parseColor("#545454"))
                etAdaptorReg.isEnabled = false
                etAdaptorReg.setTextColor(Color.parseColor("#545454"))
                etAdaptorMeter.isEnabled = false
                etAdaptorMeter.setTextColor(Color.parseColor("#545454"))
                etFemaleUnion.isEnabled = false
                etFemaleUnion.setTextColor(Color.parseColor("#545454"))
            }

            etGiInstallation.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    if (p0 != null) {
                        if(p0.isNotBlank()){
                            val length = p0.toString().toFloat()
                            if(length > 12f){
//                                etGiInstallation.setText("15")
                                lmcExtraGiLength = "${length-12}"
                                etExtraGlLength.setText(lmcExtraGiLength)
                            }else {
                                lmcExtraGiLength = "0"
                                etExtraGlLength.setText(lmcExtraGiLength)
                            }
                        }
                    }
                }

            })

            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }

            etCuInstallation.addTextChangedListener(object: TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    if (p0 != null) {
                        if(p0.isNotBlank()){
                            val length = p0.toString().toFloat()
                            if(length > 12f){
                                lmcExtraMlcLength = "${length-12}"
                                etExtraMlcLength.setText(lmcExtraMlcLength)
                            }else {
                                lmcExtraMlcLength = "0"
                                etExtraMlcLength.setText(lmcExtraMlcLength)
                            }
                        }
                    }
                }

            })


            btnNext.setOnClickListener {
                if (isLmcModified)
                {
                    if (!hasMeter)
                    {
                        if (etGiInstallation.text.isBlank()) {
                            etGiInstallation.error = "Enter GI Length"
                            etGiInstallation.requestFocus()
                            return@setOnClickListener
                        }

                        if (etCuInstallation.text.isBlank()) {
                            etCuInstallation.error = "Enter MLC Length"
                            etCuInstallation.requestFocus()
                            return@setOnClickListener
                        }

                        if (etAvNo.text.isBlank()) {
                            etAvNo.error = "Enter AV No."
                            etAvNo.requestFocus()
                            return@setOnClickListener
                        }

                        if (etIvNo.text.isBlank()) {
                            etIvNo.error = "Enter IV No."
                            etIvNo.requestFocus()
                            return@setOnClickListener
                        }

                        if (etMeterCompany.text.isBlank()) {
                            etMeterCompany.error = "Enter Meter Company"
                            etMeterCompany.requestFocus()
                            return@setOnClickListener
                        }

                        if (etInitialMeter.text.isBlank()) {
                            etInitialMeter.error = "Enter Initial Meter"
                            etInitialMeter.requestFocus()
                            return@setOnClickListener
                        }

                        if (etMeterBracket.text.isBlank()) {
                            etMeterBracket.error = "Enter Meter Bracket"
                            etMeterBracket.requestFocus()
                            return@setOnClickListener
                        }

                        if (etMeterSticker.text.isBlank()) {
                            etMeterSticker.error = "Enter Meter Sticker"
                            etMeterSticker.requestFocus()
                            return@setOnClickListener
                        }
                    }

                    if (etGiClamp.text.isBlank()) {
                        etGiClamp.error = "Enter GI Clamp"
                        etGiClamp.requestFocus()
                        return@setOnClickListener
                    }

                    if (etMlcClamp.text.isBlank()) {
                        etMlcClamp.error = "Enter MLC Clamp"
                        etMlcClamp.requestFocus()
                        return@setOnClickListener
                    }

                    if (etGiMf.text.isBlank()) {
                        etGiMf.error = "Enter Gi M/F Elbow"
                        etGiMf.requestFocus()
                        return@setOnClickListener
                    }

                    if (etGiFf.text.isBlank()) {
                        etGiFf.error = "Enter Gi F/F Elbow"
                        etGiFf.requestFocus()
                        return@setOnClickListener
                    }

                    if (etGi2.text.isBlank()) {
                        etGi2.error = "Enter Gi 2' nipple"
                        etGi2.requestFocus()
                        return@setOnClickListener
                    }

                    if (etGi3.text.isBlank()) {
                        etGi3.error = "Enter Gi 3' nipple"
                        etGi3.requestFocus()
                        return@setOnClickListener
                    }

                    if (etGi4.text.isBlank()) {
                        etGi4.error = "Enter Gi 4' nipple"
                        etGi4.requestFocus()
                        return@setOnClickListener
                    }

                    if (etGi6.text.isBlank()) {
                        etGi6.error = "Enter Gi 6' nipple"
                        etGi6.requestFocus()
                        return@setOnClickListener
                    }

                    if (etGi8.text.isBlank()) {
                        etGi8.error = "Enter Gi 8' nipple"
                        etGi8.requestFocus()
                        return@setOnClickListener
                    }


                    if (etGiTee.text.isBlank()) {
                        etGiTee.error = "Enter Gi Tee"
                        etGiTee.requestFocus()
                        return@setOnClickListener
                    }

                    if (etMlcTee.text.isBlank()) {
                        etMlcTee.error = "Enter MLC Tee"
                        etMlcTee.requestFocus()
                        return@setOnClickListener
                    }

                    if (etGiSocket.text.isBlank()) {
                        etGiSocket.error = "Enter GI Socket"
                        etGiSocket.requestFocus()
                        return@setOnClickListener
                    }

                    if (etMlcMale.text.isBlank()) {
                        etMlcMale.error = "Enter MLC Male Union"
                        etMlcMale.requestFocus()
                        return@setOnClickListener
                    }

                    if (etMlcFemale.text.isBlank()) {
                        etMlcFemale.error = "Enter MLC Male Union"
                        etMlcFemale.requestFocus()
                        return@setOnClickListener
                    }

                    //get_ng_approval_list.php
                    if (etMeterNo.text.isBlank()) {
                        etMeterNo.error = "Enter Meter No."
                        etMeterNo.requestFocus()
                        return@setOnClickListener
                    }

                    if (etRegulatorNo.text.isBlank()) {
                        etRegulatorNo.error = "Enter Regulator No."
                        etRegulatorNo.requestFocus()
                        return@setOnClickListener
                    }

                    if (etPlateMarker.text.isBlank()) {
                        etPlateMarker.error = "Enter Plate Marker"
                        etPlateMarker.requestFocus()
                        return@setOnClickListener
                    }

                    if (etAdaptorGi.text.isBlank()) {
                        etAdaptorGi.error = "Enter Adaptor GI to Reg"
                        etAdaptorGi.requestFocus()
                        return@setOnClickListener
                    }

                    if (etAdaptorReg.text.isBlank()) {
                        etAdaptorReg.error = "Enter Adaptor Reg to Meter"
                        etAdaptorReg.requestFocus()
                        return@setOnClickListener
                    }

                    if (etAdaptorMeter.text.isBlank()) {
                        etAdaptorMeter.error = "Enter Adaptor Meter to GI Pipe"
                        etAdaptorMeter.requestFocus()
                        return@setOnClickListener
                    }

                    if (etFemaleUnion.text.isBlank()) {
                        etFemaleUnion.error = "Enter Female Union Meter to MLC Pipe"
                        etFemaleUnion.requestFocus()
                        return@setOnClickListener
                    }

                    val params = HashMap<String, String>()
                    params["application_number"] = args.appNo
                    params["bp_number"] = args.bpNo.toString()
                    params["tpi_id"] = args.tpiId.toString()

                    params["ng_session_id"] = args.sessionId.toString()
                    params["ng_convertion_date"] = args.conversionDate
                    params["rfc_status"] = args.rfcStatus.toString()
                    params["mmt_testing"] = args.mmtStatus.toString()
                    params["leakage_testing"] = args.leakageStatus.toString()
                    params["gas_pressure"] = args.gasPressure ?: ""
                    params["meter_reading"] = args.finalReading ?: ""
                    params["burner_type"] = args.burnerType.toString()
                    params["hose_pipe"] = args.hoseLength.toString()
                    params["nozzle_65"] = args.ng65Length.toString()
                    params["nozzle_90"] = args.ng90Length.toString()
                    params["nozzle_110"] = args.ng110Length.toString()
                    params["nozzle_125"] = args.ng125Length.toString()
                    params["drs_number"] = args.drsNumber.toString()
                    params["sr_number"] = args.srNumber.toString()
                    params["gi_union"] = args.giUnion.toString()
                    params["gi_clamp"] = etGiClamp.text.toString()
                    params["mlc_clamp"] = etMlcClamp.text.toString()
                    params["gi_MF_elbow"] = etGiMf.text.toString()
                    params["gi_FF_elbow"] = etGiFf.text.toString()
                    params["gi_2_nipple"] = etGi2.text.toString()
                    params["gi_3_nipple"] = etGi3.text.toString()
                    params["gi_4_nipple"] = etGi4.text.toString()
                    params["gi_6_nipple"] = etGi6.text.toString()
                    params["gi_8_nipple"] = etGi8.text.toString()
                    params["gi_tee"] = etGiTee.text.toString()
                    params["mlc_tee"] = etMlcTee.text.toString()
                    params["gi_socket"] = etGiSocket.text.toString()
                    params["mlc_male_union"] = etMlcMale.text.toString()
                    params["mlc_female_union"] = etMlcFemale.text.toString()
                    params["meter_no"] = etMeterNo.text.toString()
//                params["meter_type"] =
                    params["regulator_no"] = etRegulatorNo.text.toString()
                    params["plate_marker"] = etPlateMarker.text.toString()
                    params["adaptor_GI_to_reg"] = etAdaptorGi.text.toString()
                    params["adaptor_reg_to_meter"] = etAdaptorReg.text.toString()
                    params["adaptor_meter_to_GI_pipe"] = etAdaptorMeter.text.toString()
                    params["female_union_meter_MLC_pipe"] = etFemaleUnion.text.toString()
                    params["extension_modication_of_lmc"] = args.lmcExtension.toString()

                    if (!hasMeter)
                    {
                        params["wo_gi_length"] = etGiInstallation.text.toString()
                        params["wo_mlc_length"] = etCuInstallation.text.toString()
                        params["wo_extra_gi"] = etExtraGlLength.text.toString()
                        params["wo_extra_mlc"] = etExtraMlcLength.text.toString()
                        params["wo_av_no"] = etAvNo.text.toString()
                        params["wo_iv_no"] = etIvNo.text.toString()
                        params["wo_meter_company"] = etMeterCompany.text.toString()
                        params["wo_initial_meter_reading"] = etInitialMeter.text.toString()
                        params["wo_meter_bracket"] = etMeterBracket.text.toString()
                        params["wo_meter_sticker"] = etMeterSticker.text.toString()

                    }

                    viewModel.updateRfcNg(params)
                    setupNgObserver()

                }
                else
                {
                    if (etMeterCompany.text.isBlank()) {
                        etMeterCompany.error = "Enter Meter Company"
                        etMeterCompany.requestFocus()
                        return@setOnClickListener
                    }

                    if (etInitialMeter.text.isBlank()) {
                        etInitialMeter.error = "Enter Initial Meter"
                        etInitialMeter.requestFocus()
                        return@setOnClickListener
                    }

                    if (etMeterBracket.text.isBlank()) {
                        etMeterBracket.error = "Enter Meter Bracket"
                        etMeterBracket.requestFocus()
                        return@setOnClickListener
                    }

                    if (etMeterSticker.text.isBlank()) {
                        etMeterSticker.error = "Enter Meter Sticker"
                        etMeterSticker.requestFocus()
                        return@setOnClickListener
                    }

                    if (etMeterNo.text.isBlank()) {
                        etMeterNo.error = "Enter Meter No."
                        etMeterNo.requestFocus()
                        return@setOnClickListener
                    }

                    if (etRegulatorNo.text.isBlank()) {
                        etRegulatorNo.error = "Enter Regulator No."
                        etRegulatorNo.requestFocus()
                        return@setOnClickListener
                    }

                    if (etAdaptorGi.text.isBlank()) {
                        etAdaptorGi.error = "Enter Adaptor GI to Reg"
                        etAdaptorGi.requestFocus()
                        return@setOnClickListener
                    }

                    if (etAdaptorReg.text.isBlank()) {
                        etAdaptorReg.error = "Enter Adaptor Reg to Meter"
                        etAdaptorReg.requestFocus()
                        return@setOnClickListener
                    }

                    if (etAdaptorMeter.text.isBlank()) {
                        etAdaptorMeter.error = "Enter Adaptor Meter to GI Pipe"
                        etAdaptorMeter.requestFocus()
                        return@setOnClickListener
                    }

                    if (etFemaleUnion.text.isBlank()) {
                        etFemaleUnion.error = "Enter Female Union Meter to MLC Pipe"
                        etFemaleUnion.requestFocus()
                        return@setOnClickListener
                    }

                    val params = HashMap<String, String>()
                    params["application_number"] = args.appNo
                    params["bp_number"] = args.bpNo.toString()
                    params["tpi_id"] = args.tpiId.toString()
                    params["extension_modication_of_lmc"] = args.lmcExtension.toString()

                    params["ng_session_id"] = args.sessionId.toString()
                    params["ng_convertion_date"] = args.conversionDate
                    params["rfc_status"] = args.rfcStatus.toString()
                    params["mmt_testing"] = args.mmtStatus.toString()
                    params["leakage_testing"] = args.leakageStatus.toString()
                    params["gas_pressure"] = args.gasPressure ?: ""
                    params["meter_reading"] = args.finalReading ?: ""
                    params["burner_type"] = args.burnerType.toString()
                    params["hose_pipe"] = args.hoseLength.toString()
                    params["nozzle_65"] = args.ng65Length.toString()
                    params["nozzle_90"] = args.ng90Length.toString()
                    params["nozzle_110"] = args.ng110Length.toString()
                    params["nozzle_125"] = args.ng125Length.toString()
                    params["drs_number"] = args.drsNumber.toString()
                    params["sr_number"] = args.srNumber.toString()
                    params["gi_union"] = args.giUnion.toString()

                    params["wo_meter_company"] = etMeterCompany.text.toString()
                    params["wo_initial_meter_reading"] = etInitialMeter.text.toString()
                    params["wo_meter_bracket"] = etMeterBracket.text.toString()
                    params["wo_meter_sticker"] = etMeterSticker.text.toString()
                    params["wo_meter_no"] = etMeterNo.text.toString()
                    params["wo_regulator_number"] = etRegulatorNo.text.toString()
                    params["wo_adaptor_GI_to_reg"] = etAdaptorGi.text.toString()
                    params["wo_adaptor_reg_to_meter"] = etAdaptorReg.text.toString()
                    params["wo_adaptor_meter_to_GI_pipe"] = etAdaptorMeter.text.toString()
                    params["wo_female_union_meter_MLC_pipe"] = etFemaleUnion.text.toString()

                    viewModel.updateRfcNg(params)
                    setupNgObserver()
                }
            }
        }

        return binding.root
    }

    private fun setupNgObserver() {
        viewModel.rfcNgUpdateResponse.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.LOADING->{
                        setDialog(true)
                    }
                    Status.SUCCESS->{
                        setDialog(false)
                        if(!it.data!!.error){
                            binding.apply {
                                val directions =
                                    RfcExtensionFragmentDirections.actionRfcExtensionFragmentToNgVerificationFragment(
                                        appNo = args.appNo,
                                        conversionDate = args.conversionDate,
                                        bpNo = args.bpNo,
                                        jmrNo = args.jmrNo,
                                        assignedDate = args.assignedDate,
                                        initialReading = args.initialReading,
                                        burnerDetails = args.burnerDetails,
                                        workDate = args.workDate,
                                        sessionId = args.sessionId,
                                        tpiId = args.tpiId,
                                        customerInfo = args.customerInfo,
                                        meterStatus = args.meterStatus,
                                        statusId = args.statusId,
                                        rfcStatus = args.rfcStatus,
                                        mmtStatus = args.mmtStatus,
                                        leakageStatus = args.leakageStatus,
                                        gasPressure = args.gasPressure,
                                        finalReading = args.finalReading,
                                        burnerType = args.burnerType,
                                        hoseLength = args.hoseLength,
                                        ng65Length = args.ng65Length,
                                        ng90Length = args.ng90Length,
                                        ng110Length = args.ng110Length,
                                        ng125Length = args.ng125Length,
                                        acknowledgeId = args.acknowledgeId,
                                        drsNumber = args.drsNumber,
                                        srNumber = args.srNumber,
                                        lmcGiClamp = etGiClamp.text.toString(),
                                        lmcMlcClamp = etMlcClamp.text.toString(),
                                        lmcGiMfElbow = etGiMf.text.toString(),
                                        lmcGiFfElbow = etGiFf.text.toString(),
                                        lmcGi2 = etGi2.text.toString(),
                                        lmcGi3 = etGi3.text.toString(),
                                        lmcGi4 = etGi4.text.toString(),
                                        lmcGi6 = etGi6.text.toString(),
                                        lmcGi8 = etGi8.text.toString(),
                                        lmcGiTee = etGiTee.text.toString(),
                                        lmcMlcTee = etMlcTee.text.toString(),
                                        lmcGiSocket = etGiSocket.text.toString(),
                                        lmcMaleUnion = etMlcMale.text.toString(),
                                        lmcFemaleUnion = etMlcFemale.text.toString(),
                                        lmcMeterBracket = etMeterBracket.text.toString(),
                                        lmcMeterSticker = etMeterSticker.text.toString(),
                                        lmcPlateMarker = etPlateMarker.text.toString(),
                                        lmcAdaptorGI = etAdaptorGi.text.toString(),
                                        lmcAdaptorReg = etAdaptorReg.text.toString(),
                                        lmcAdaptorMeter = etAdaptorMeter.text.toString(),
                                        lmcFemaleMeter = etFemaleUnion.text.toString(),
                                        lmcMeterNo = etMeterNo.text.toString(),
                                        lmcRegulatorNo = etRegulatorNo.text.toString(),
                                        lmcGiLength = etGiInstallation.text.toString(),
                                        lmcMlcLength = etCuInstallation.text.toString(),
                                        lmcAvQty = etAvNo.text.toString(),
                                        lmcIvQty = etIvNo.text.toString(),
                                        lmcExtraGiLength = etExtraGlLength.text.toString(),
                                        lmcExtraMlcLength = etExtraMlcLength.text.toString(),
                                        lmcMeterCompany = etMeterCompany.text.toString(),
                                        lmcInitialReading = etInitialMeter.text.toString(),
                                        lmcWoMeterBracket = etMeterBracket.text.toString(),
                                        lmcWoMeterSticker = etMeterSticker.text.toString(),
                                        lmcWoAdaptorGI = etAdaptorGi.text.toString(),
                                        lmcWoAdaptorReg = etAdaptorReg.text.toString(),
                                        lmcWoAdaptorMeter = etAdaptorMeter.text.toString(),
                                        lmcWoFemaleMeter = etFemaleUnion.text.toString(),
                                        lmcWoMeterNumber = etMeterNo.text.toString(),
                                        lmcWoRegulatorNumber = etRegulatorNo.text.toString(),
                                        lmcExtension = args.lmcExtension,
                                        giUnion = args.giUnion
                                        )
                                findNavController().navigate(directions)
                                Toast.makeText(
                                    requireContext(),
                                    it.data.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }else{
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    Status.ERROR->{
                        setDialog(false)
                    }
                }
            }
        }
    }

    private fun setUpObserver() {
        viewModel.ngApprovalResponse.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.LOADING->{
                        setDialog(true)
                    }
                    Status.SUCCESS->{
                        setDialog(false)
                        if(!it.data!!.error!!){
                            val customerInfo= it.data.customerInfo!!
                            val installationDetails = it.data.customerInfo.installationDetails
                            if(customerInfo.lmcExtensionWithMeter!!.lmcMeterStatus!!.contains("with meter",true)){
                                 hasMeter = true
                            }
                            if(customerInfo.lmcExtensionWithMeter.lmcModification!!.contains("yes",true)){
                                isLmcModified = true
                            }
                            val lmcWithMeter = customerInfo.lmcExtensionWithMeter
                            val lmcWithoutMeter = customerInfo.lmcExtensionWithoutMeter
                            if(isLmcModified){
                                if(hasMeter){
                                    binding.apply {
                                        cvWoMeter.visibility = View.GONE
                                        tvMeterCompany.visibility = View.GONE
                                        etMeterCompany.visibility = View.GONE
                                        tvInitialMeter.visibility = View.GONE
                                        etInitialMeter.visibility = View.GONE
                                        tvMeterBracket.visibility = View.GONE
                                        etMeterBracket.visibility = View.GONE
                                        tvMeterSticker.visibility = View.GONE
                                        etMeterSticker.visibility = View.GONE
                                    }
                                    binding.apply {
                                        lmcWithMeter.apply {
                                            lmcGiClamp = giClamp
                                            etGiClamp.setText(giClamp)
                                            lmcMlcClamp = mlcClamp
                                            etMlcClamp.setText(mlcClamp)
                                            lmcGiMfElbow = giMfElbow
                                            etGiMf.setText(giMfElbow)
                                            lmcGiFfElbow = giFfElbow
                                            etGiFf.setText(giFfElbow)
                                            lmcGi2 = gi2Nipple
                                            etGi2.setText(gi2Nipple)
                                            lmcGi3 = gi3Nipple
                                            etGi3.setText(gi3Nipple)
                                            lmcGi4 = gi4Nipple
                                            etGi4.setText(gi4Nipple)
                                            lmcGi6 = gi6Nipple
                                            etGi6.setText(gi6Nipple)
                                            lmcGi8 = gi8Nipple
                                            etGi8.setText(gi8Nipple)
                                            lmcGiTee = giTee
                                            etGiTee.setText(giTee)
                                            lmcMlcTee = mlcTee
                                            etMlcTee.setText(mlcTee)
                                            lmcGiSocket = giSocket
                                            etGiSocket.setText(giSocket)
                                            lmcMaleUnion = mlcMaleUnion
                                            etMlcMale.setText(mlcMaleUnion)
                                            lmcFemaleUnion = mlcFemaleUnion
                                            etMlcFemale.setText(mlcFemaleUnion)
//                                        lmcMeterBracket = meterBracket
//                                        lmcMeterSticker = meterSticker
                                            lmcMeterNo = meterNo
                                            etMeterNo.setText(meterNo)
                                            lmcRegulatorNo = regulatorNo
                                            etRegulatorNo.setText(regulatorNo)
                                            lmcPlateMarker = plateMarker
                                            etPlateMarker.setText(plateMarker)
                                            lmcAdaptorGI = adaptorGi
                                            etAdaptorGi.setText(adaptorGi)
                                            lmcAdaptorReg = adaptorReg
                                            etAdaptorReg.setText(adaptorReg)
                                            lmcAdaptorMeter = adaptorMeter
                                            etAdaptorMeter.setText(adaptorMeter)
                                            lmcFemaleMeter = femaleUnion
                                            etFemaleUnion.setText(femaleUnion)
                                        }
                                    }

                                }else{

                                    binding.apply {
                                        cvWoMeter.visibility = View.VISIBLE
                                        tvMeterCompany.visibility = View.VISIBLE
                                        etMeterCompany.visibility = View.VISIBLE
                                        tvInitialMeter.visibility = View.VISIBLE
                                        etInitialMeter.visibility = View.VISIBLE
                                        tvMeterBracket.visibility = View.VISIBLE
                                        etMeterBracket.visibility = View.VISIBLE
                                        tvMeterSticker.visibility = View.VISIBLE
                                        etMeterSticker.visibility = View.VISIBLE
                                    }

                                    binding.apply {
                                        lmcWithMeter.apply {
                                            lmcGiClamp = giClamp
                                            etGiClamp.setText(giClamp)
                                            lmcMlcClamp = mlcClamp
                                            etMlcClamp.setText(mlcClamp)
                                            lmcGiMfElbow = giMfElbow
                                            etGiMf.setText(giMfElbow)
                                            lmcGiFfElbow = giFfElbow
                                            etGiFf.setText(giFfElbow)
                                            lmcGi2 = gi2Nipple
                                            etGi2.setText(gi2Nipple)
                                            lmcGi3 = gi3Nipple
                                            etGi3.setText(gi3Nipple)
                                            lmcGi4 = gi4Nipple
                                            etGi4.setText(gi4Nipple)
                                            lmcGi6 = gi6Nipple
                                            etGi6.setText(gi6Nipple)
                                            lmcGi8 = gi8Nipple
                                            etGi8.setText(gi8Nipple)
                                            lmcGiTee = giTee
                                            etGiTee.setText(giTee)
                                            lmcMlcTee = mlcTee
                                            etMlcTee.setText(mlcTee)
                                            lmcGiSocket = giSocket
                                            etGiSocket.setText(giSocket)
                                            lmcMaleUnion = mlcMaleUnion
                                            etMlcMale.setText(mlcMaleUnion)
                                            lmcFemaleUnion = mlcFemaleUnion
                                            etMlcFemale.setText(mlcFemaleUnion)
                                            lmcMeterNo = meterNo
                                            etMeterNo.setText(meterNo)
                                            lmcRegulatorNo = regulatorNo
                                            etRegulatorNo.setText(regulatorNo)
                                            lmcPlateMarker = plateMarker
                                            etPlateMarker.setText(plateMarker)
                                            lmcAdaptorGI = adaptorGi
                                            etAdaptorGi.setText(adaptorGi)
                                            lmcAdaptorReg = adaptorReg
                                            etAdaptorReg.setText(adaptorReg)
                                            lmcAdaptorMeter = adaptorMeter
                                            etAdaptorMeter.setText(adaptorMeter)
                                            lmcFemaleMeter = femaleUnion
                                            etFemaleUnion.setText(femaleUnion)

                                        }

                                        lmcWithoutMeter?.apply {

                                            lmcGiLength = woGiLength
                                            etGiInstallation.setText(woGiLength)
                                            lmcMlcLength = woMlcLength
                                            etCuInstallation.setText(woMlcLength)
                                            lmcExtraGiLength = woExtraGi
                                            etExtraGlLength.setText(woExtraGi)
                                            lmcExtraMlcLength = woExtraMlc
                                            etExtraMlcLength.setText(woExtraMlc)
                                            lmcIvQty = woIvNo
                                            etIvNo.setText(woIvNo)
                                            lmcAvQty = woAvNo
                                            etAvNo.setText(woAvNo)
                                            lmcMeterCompany = woMeterCompany
                                            etMeterCompany.setText(woMeterCompany)
                                            lmcInitialReading = woInitialReading
                                            etInitialMeter.setText(woInitialReading)
                                            lmcMeterBracket = woMeterBracket
                                            etMeterBracket.setText(woMeterBracket)
                                            lmcMeterSticker = woMeterSticker
                                            etMeterSticker.setText(woMeterSticker)

                                        }

                                    }

                                }
                            }else{

                                binding.apply {
                                    cvWoMeter.visibility = View.GONE
                                    tvGiClamp.visibility = View.GONE
                                    etGiClamp.visibility = View.GONE
                                    tvMlcClamp.visibility = View.GONE
                                    etMlcClamp.visibility = View.GONE
                                    tvGiMf.visibility = View.GONE
                                    etGiMf.visibility = View.GONE
                                    tvGiFf.visibility = View.GONE
                                    etGiFf.visibility = View.GONE
                                    tvGi2.visibility = View.GONE
                                    etGi2.visibility = View.GONE
                                    tvGi3.visibility = View.GONE
                                    etGi3.visibility = View.GONE
                                    tvGi4.visibility = View.GONE
                                    etGi4.visibility = View.GONE
                                    tvGi6.visibility = View.GONE
                                    etGi6.visibility = View.GONE
                                    tvGi8.visibility = View.GONE
                                    etGi8.visibility = View.GONE
                                    tvGiTee.visibility = View.GONE
                                    etGiTee.visibility = View.GONE
                                    tvMlcTee.visibility = View.GONE
                                    etMlcTee.visibility = View.GONE
                                    tvGiSocket.visibility = View.GONE
                                    etGiSocket.visibility = View.GONE
                                    tvMlcMale.visibility = View.GONE
                                    etMlcMale.visibility = View.GONE
                                    tvMlcFemale.visibility = View.GONE
                                    etMlcFemale.visibility = View.GONE
                                    tvPlateMarker.visibility = View.GONE
                                    etPlateMarker.visibility = View.GONE
                                }

                                lmcWithoutMeter?.apply {
                                    lmcMeterCompany = woMeterCompany
                                    binding.etMeterCompany.setText(woMeterCompany)
                                    lmcInitialReading = woInitialReading
                                    binding.etInitialMeter.setText(woInitialReading)
                                }
                                binding.apply {
                                    lmcWithoutMeter?.apply {
                                        lmcMeterBracket = woMeterBracket
                                        etMeterBracket.setText(woMeterBracket)
                                        lmcMeterSticker = woMeterSticker
                                        etMeterSticker.setText(woMeterSticker)
                                        lmcMeterNo = woMeterNo
                                        etMeterNo.setText(woMeterNo)
                                        lmcRegulatorNo = woRegulatorNo
                                        etRegulatorNo.setText(woRegulatorNo)
                                        lmcAdaptorGI = woAdaptorGi
                                        etAdaptorGi.setText(woAdaptorGi)
                                        lmcAdaptorReg = woAdaptorReg
                                        etAdaptorReg.setText(woAdaptorReg)
                                        lmcAdaptorMeter = woAdaptorMeter
                                        etAdaptorMeter.setText(woAdaptorMeter)
                                        lmcFemaleMeter = woFemaleMeter
                                        etFemaleUnion.setText(woFemaleMeter)
                                    }
                                }

                            }

                        }
                    }
                    Status.ERROR->{
                        setDialog(false)
                    }
                }
            }
        }
    }

    private fun setUpTpiObserver() {
        viewModel.tpiListResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it.status) {
                    Status.LOADING -> {
                        setDialog(true)
                    }
                    Status.SUCCESS -> {
                        setDialog(false)
                        if (!it.data!!.error) {
                            AppCache.ngStatusList.clear()
                            it.data.lmcMeterDetails.forEach { lmcType ->
                                AppCache.meterCompanyList[lmcType.name] = lmcType.id
                            }

                            val companyList = mutableListOf<String>()
                            AppCache.meterCompanyList.keys.forEach {
                                companyList.add(it)
                            }

                            companySpinnerDialog = SpinnerDialog(
                                activity,
                                companyList as ArrayList<String>,
                                "Select Meter Company",
                                "Close"
                            )



                            binding.apply {
                                companySpinnerDialog?.bindOnSpinerListener { item, position ->
                                    etMeterCompany.text = item
                                    lmcMeterCompany = item
                                }
                            }


                        } else {
                            Toast.makeText(requireContext(), "Please try again", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    Status.ERROR -> {
                        setDialog(false)
                        Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

    }


    private fun setDialog(show: Boolean) {
        if (show) dialog!!.show() else dialog!!.dismiss()
    }

}