package com.thinkgas.heatapp.ui.ng

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
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
import com.thinkgas.heatapp.data.remote.model.UploadRequestModel
import com.thinkgas.heatapp.databinding.CommentDialogBinding
import com.thinkgas.heatapp.databinding.FragmentNgApprovalBinding
import com.thinkgas.heatapp.ui.lmc.LmcStatusFragment
import com.thinkgas.heatapp.utils.AppUtils
import com.thinkgas.heatapp.utils.Constants
import com.thinkgas.heatapp.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@AndroidEntryPoint
class NgApprovalFragment : Fragment() {
    private var _binding:FragmentNgApprovalBinding? = null
    private val binding get()= _binding!!
    private val viewModel by viewModels<NgApprovalViewModel>()
    private val args by navArgs<NgApprovalFragmentArgs>()
    var ngStatusSpinner: SpinnerDialog? = null
    var rfcStatusSpinner: SpinnerDialog? = null
    var burnerSpinner: SpinnerDialog? = null
    var hoseSpinner: SpinnerDialog? = null
    var nozzleSpinner65: SpinnerDialog? = null
    var nozzleSpinner90: SpinnerDialog? = null
    var nozzleSpinner110: SpinnerDialog? = null
    var nozzleSpinner125: SpinnerDialog? = null
    var lmcExtensionSpinner: SpinnerDialog? = null
    private var isFailed = false
    private var dialog: Dialog? = null
    var hasMeter = false
    var isLmcModified = false
    var hour: Int = 0
    var minute: Int = 0
    var myDay = 0
    var myMonth: Int = 0
    var myYear: Int = 0
    var myHour: Int = 0
    var myMinute: Int = 0

    companion object{
        var meterFlag = 1
        var ngStatus:String? = null
        var ngStatusId:String? = null
        var rfcStatus:String? = null
        var lmcExtension:String? = null
        var initialReading:String? = null
        var burnerDetails:String? = null
        var conversionDate:String? = null
        var acknowledgeId:String? = null
        var mmtStatus:String? = null
        var leakageStatus:String? = null
        var gasPressure:String? = null
        var finalReading:String? = null
        var burnerType:String? = null
        var hoseLength:String? = null
        var nozzle65Length:String? = null
        var nozzle90Length:String? = null
        var nozzle110Length:String? = null
        var nozzle125Length:String? = null
        var drsNumber:String? = null
        var srNumber:String? = null
        var giUnion:String? = null
        var followUpDate: String? = null
    }

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
    var lmcWoMeterBracket:String? = null
    var lmcWoMeterSticker:String? = null
    var lmcWoAdaptorGI:String? = null
    var lmcWoAdaptorReg:String? = null
    var lmcWoAdaptorMeter:String? = null
    var lmcWoFemaleMeter:String? = null
    var lmcWoMeterNumber:String? = null
    var lmcWoRegulatorNumber:String? = null


    var day = 0
    var month: Int = 0
    var year: Int = 0
    var bpNo:String? = null
    var jmrNo:String? = null
    var assignedDate:String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNgApprovalBinding.inflate(inflater,container,false)

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.progress)
        dialog = builder.create()

        val params = HashMap<String,String>()
        params["application_number"] = args.appNo
        params["session_id"] = args.sessionId
        params["is_tpi"] = if(AppCache.isTpi) "1" else "0"


        viewModel.getNgApprovalList(params)
        setUpObserver()


        val paramsTpi = HashMap<String, String>()
        paramsTpi["version_code"] = "1"
        paramsTpi["os_type"] = "android"


        viewModel.getTpiListTypes(paramsTpi)
        setUpTpiObserver()


        binding.apply {

            if(AppCache.isTpi){
                spinnerLmcExtension.isEnabled = false
                spinnerRfcStatus.isEnabled = false
                rbMmtDone.isEnabled = false
                rbMmtNotDone.isEnabled = false
                rbLeakageDone.isEnabled = false
                rbLeakageNotDone.isEnabled = false
                etLiveGas.isEnabled = false
                etMeterReading.isEnabled = false
               etGiUnion.isEnabled = false
                spinnerBurnerType.isEnabled = false
                spinnerHose.isEnabled = false
                spinnerNb65.isEnabled = false
                spinnerNb90.isEnabled = false
                spinnerNb110.isEnabled = false
                spinnerNb125.isEnabled = false
                tvConversationDate.isEnabled =false
                etDrsNo.isEnabled = false
                etSrNo.isEnabled = false
            }

            tvDateTime.setOnClickListener {
                val calendar: Calendar = Calendar.getInstance()
                day = calendar.get(Calendar.DAY_OF_MONTH)
                month = calendar.get(Calendar.MONTH)
                year = calendar.get(Calendar.YEAR)
                val datePickerDialog =
                    DatePickerDialog(requireContext(), dateListener, year, month,day)
                datePickerDialog.datePicker.minDate = Date().time
                datePickerDialog.show()
            }

            ivProfile.setOnClickListener {
                val logoutBuilder = AlertDialog.Builder(requireContext())
                logoutBuilder.setTitle("Log Out")
                logoutBuilder.setMessage("Are you sure want to log out?")
                logoutBuilder.setCancelable(false)
                logoutBuilder.setPositiveButton("Yes") { dialogInterface, i ->
                    val preferences = activity?.getSharedPreferences("TPI_PREFS",
                        Context.MODE_PRIVATE
                    )
                    val editor: SharedPreferences.Editor = preferences!!.edit()
                    editor.clear()
                    editor.apply()
                    val directions = NgApprovalFragmentDirections.actionNgApprovalFragmentToLoginFragment()
                    findNavController().navigate(directions)

                }
                logoutBuilder.setNegativeButton("No") { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                val logoutAlert = logoutBuilder.create()
                logoutAlert.show()


            }

            followUpDate?.let {
                tvDateTime.text = it
            }

            rfcStatus?.let {
               spinnerRfcStatus.text = it
                if(it == "Passed"){
                    tvMmtTesting.visibility = View.VISIBLE
                    rgMmt.visibility = View.VISIBLE
                    tvDateTitle.visibility = View.VISIBLE
                    tvConversationDate.visibility = View.VISIBLE
                }
            }

            lmcExtension?.let {
                spinnerLmcExtension.text = it
            }

            burnerType?.let {
                spinnerBurnerType.text = it
            }

            hoseLength?.let {
                spinnerHose.text = it
            }

            nozzle65Length?.let {
                spinnerNb65.text = it
            }

            nozzle90Length?.let {
                spinnerNb90.text = it
            }

            nozzle110Length?.let {
                spinnerNb110.text = it
            }

            nozzle125Length?.let {
                spinnerNb125.text = it
            }

            drsNumber?.let {
                etDrsNo.setText(it)
            }

            srNumber?.let {
                etSrNo.setText(it)
            }

            initialReading?.let {
                etMeterReading.setText(it)
            }

            giUnion?.let {
                etGiUnion.setText(it)
            }

            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }

            btnApprove.setOnClickListener {
                submitNgApproval("Approved")
            }

            btnDecline.setOnClickListener {
                submitNgApproval("Declined")
            }

            rgMmt.setOnCheckedChangeListener { radioGroup, i ->
                when(i){
                    rbMmtDone.id->{

                            tvLeakageTesting.visibility = View.VISIBLE
                            rgLeakage.visibility = View.VISIBLE
                            tvLiveGas.visibility = View.VISIBLE
                            etLiveGas.visibility = View.VISIBLE
                            cvMeter.visibility = View.VISIBLE
                            cvSr.visibility = View.VISIBLE
                            tvBurnerType.visibility = View.VISIBLE
                            spinnerBurnerType.visibility = View.VISIBLE
                            tvHoseOptions.visibility = View.VISIBLE
                            spinnerHose.visibility = View.VISIBLE
                            clNozzle.visibility = View.VISIBLE

                        mmtStatus = "1"
                    }
                    rbMmtNotDone.id->{

                            tvLeakageTesting.visibility = View.GONE
                            rgLeakage.visibility = View.GONE
                            tvLiveGas.visibility = View.GONE
                            etLiveGas.visibility = View.GONE
                            cvMeter.visibility = View.GONE
                            cvSr.visibility = View.GONE
                            tvBurnerType.visibility = View.GONE
                            spinnerBurnerType.visibility = View.GONE
                            tvHoseOptions.visibility = View.GONE
                            spinnerHose.visibility = View.GONE
                            clNozzle.visibility = View.GONE

                        mmtStatus = "0"

                    }
                }
            }

            rgLeakage.setOnCheckedChangeListener { radioGroup, i ->
                leakageStatus = if(rbLeakageDone.id == i) "1" else "0"
            }

            spinnerLmcExtension.setOnClickListener {
                lmcExtensionSpinner!!.showSpinerDialog()
                lmcExtensionSpinner!!.setCancellable(true)
            }


            spinnerBurnerType.setOnClickListener {
                burnerSpinner!!.showSpinerDialog()
                burnerSpinner!!.setCancellable(true)
            }

            spinnerHose.setOnClickListener {
                hoseSpinner!!.showSpinerDialog()
                hoseSpinner!!.setCancellable(true)
            }

            spinnerNb65.setOnClickListener {
                nozzleSpinner65!!.showSpinerDialog()
                nozzleSpinner65!!.setCancellable(true)
            }
            spinnerNb90.setOnClickListener {
                nozzleSpinner90!!.showSpinerDialog()
                nozzleSpinner90!!.setCancellable(true)
            }
            spinnerNb110.setOnClickListener {
                nozzleSpinner110!!.showSpinerDialog()
                nozzleSpinner110!!.setCancellable(true)
            }
            spinnerNb125.setOnClickListener {
                nozzleSpinner125!!.showSpinerDialog()
                nozzleSpinner125!!.setCancellable(true)
            }

            val rfcList = arrayListOf<String>("Passed","Hold","Failed")
            rfcStatusSpinner = SpinnerDialog(
                activity,
                rfcList,
                "Select RFC Status",
                "Close"
            )


            spinnerRfcStatus.setOnClickListener {
                rfcStatusSpinner!!.showSpinerDialog()
                rfcStatusSpinner!!.setCancellable(true)

            }


            tvConversationDate.setOnClickListener {
                val calendar: Calendar = Calendar.getInstance()
                day = calendar.get(Calendar.DAY_OF_MONTH)
                month = calendar.get(Calendar.MONTH)
                year = calendar.get(Calendar.YEAR)
//                val datePickerDialog =
//                    DatePickerDialog(requireContext(), { datePicker, year, month, day ->
//                        conversionDate = AppUtils.getDate("$day-${month+1}-$year")
//
//                        tvConversationDate.text = conversionDate
//                        tvConversationDate.error = null
//                    }, year, month,day)
                val datePickerDialog = DatePickerDialog(requireContext(), dateListener, year, month, day )
                datePickerDialog.datePicker.maxDate = Date().time
                datePickerDialog.datePicker.minDate = Date().time

                datePickerDialog.show()
            }


            btnSubmit.setOnClickListener {

                if(!isFailed){

                    if(rfcStatus.isNullOrBlank()){
                        spinnerRfcStatus.error = "Select RFC Status"
                        spinnerRfcStatus.requestFocus()
                        return@setOnClickListener
                    }

                    if (mmtStatus.isNullOrBlank()){
                        Toast.makeText(requireContext(), "Select MMT testing", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    if(lmcExtension.isNullOrBlank()){
                        spinnerLmcExtension.error = "Select LMC Extension/Modification"
                        spinnerLmcExtension.requestFocus()
                        return@setOnClickListener
                    }

                    if(mmtStatus == "1"){

                        gasPressure = etLiveGas.text.toString()
                        finalReading = etMeterReading.text.toString()
                        srNumber = etSrNo.text.toString()
                        drsNumber = etDrsNo.text.toString()
                        giUnion = etGiUnion.text.toString()

                        if (leakageStatus.isNullOrBlank()){
                            Toast.makeText(requireContext(), "Select leakage testing", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                        if(gasPressure.isNullOrBlank()){
                            etLiveGas.error = "Enter gas pressure"
                            etLiveGas.requestFocus()
                            return@setOnClickListener
                        }

                        if(drsNumber.isNullOrBlank()){
                            etDrsNo.error = "Enter DRS No."
                            etDrsNo.requestFocus()
                            return@setOnClickListener
                        }

                        if(srNumber.isNullOrBlank()){
                            etSrNo.error = "Enter SR No."
                            etSrNo.requestFocus()
                            return@setOnClickListener
                        }

                        if(finalReading.isNullOrBlank()){
                            etMeterReading.error = "Enter final meter reading"
                            etMeterReading.requestFocus()
                            return@setOnClickListener
                        }

                        if(giUnion.isNullOrBlank()){
                            etGiUnion.error = "Enter Gi Union reading"
                            etGiUnion.requestFocus()
                            return@setOnClickListener
                        }

                        if(burnerType.isNullOrBlank()){
                            spinnerBurnerType.error = "Select burner type"
                            spinnerBurnerType.requestFocus()
                            return@setOnClickListener
                        }
                        if(hoseLength.isNullOrBlank()){
                            spinnerHose.error = "Select hose length"
                            spinnerHose.requestFocus()
                            return@setOnClickListener
                        }

                        if(nozzle65Length.isNullOrBlank()){
                            spinnerNb65.error = "Select size(65)"
                            spinnerNb65.requestFocus()
                            return@setOnClickListener
                        }

                        if(nozzle90Length.isNullOrBlank()){
                            spinnerNb90.error = "Select size(90)"
                            spinnerNb90.requestFocus()
                            return@setOnClickListener
                        }

                        if(nozzle110Length.isNullOrBlank()){
                            spinnerNb110.error = "Select size(110)"
                            spinnerNb110.requestFocus()
                            return@setOnClickListener
                        }

                        if(nozzle125Length.isNullOrBlank()){
                            spinnerNb125.error = "Select size(125)"
                            spinnerNb125.requestFocus()
                            return@setOnClickListener
                        }
                    }

                    if(conversionDate.isNullOrBlank()){
                        tvConversationDate.error = "Select date"
                        tvConversationDate.requestFocus()
                        return@setOnClickListener
                    }

                    val params = HashMap<String,String?>()
                    params["application_number"] = args.appNo
                    params["bp_number"] = bpNo ?: ""
                    params["tpi_id"] = args.tpiId
                    params["customer_info"] = args.customerInfo
                    params["ng_session_id"] = args.sessionId
                    params["ng_convertion_date"] = conversionDate.toString() ?: ""
                    params["ng_testing_leakage_acceptance"] = acknowledgeId ?: "0"
                    params["rfc_status"] = rfcStatus
                    params["mmt_testing"] = mmtStatus
                    params["leakage_testing"] = leakageStatus ?: ""
                    params["gas_pressure"] = gasPressure
                    params["meter_reading"] = finalReading
                    params["burner_type"] = burnerType
                    params["hose_pipe"] = hoseLength
                    params["nozzle_65"] = nozzle65Length ?: ""
                    params["nozzle_90"] = nozzle90Length ?: ""
                    params["nozzle_110"] = nozzle110Length ?: ""
                    params["nozzle_125"] = nozzle125Length ?: ""
                    params["drs_number"] = drsNumber
                    params["sr_number"] = srNumber
                    params["gi_union"] = giUnion ?: ""
                    params["rfc_approval_status"] = "Nil"
                    params["rfc_comments"] = ""
                    params["extension_modication_of_lmc"] = lmcExtension

                    if(args.status == "hold" || args.status == "done" || args.status == "pending"){
                        params["gi_clamp"] = lmcGiClamp ?: ""
                        params["mlc_clamp"] = lmcMlcClamp ?: ""
                        params["gi_MF_elbow"] = lmcGiMfElbow ?: ""
                        params["gi_FF_elbow"] = lmcGiFfElbow ?: ""
                        params["gi_2_nipple"] = lmcGi2 ?: ""
                        params["gi_3_nipple"] = lmcGi3 ?: ""
                        params["gi_4_nipple"] = lmcGi4 ?: ""
                        params["gi_6_nipple"] = lmcGi6 ?: ""
                        params["gi_8_nipple"] = lmcGi8 ?: ""
                        params["gi_tee"] = lmcGiTee ?: ""
                        params["mlc_tee"] = lmcMlcTee ?: ""
                        params["gi_socket"] = lmcGiSocket ?: ""
                        params["mlc_male_union"] = lmcMaleUnion ?: ""
                        params["mlc_female_union"] = lmcFemaleUnion ?: ""
                        params["meter_no"] = lmcMeterNo ?: ""
//                params["meter_type"] =
                        params["regulator_no"] = lmcRegulatorNo ?: ""
                        params["plate_marker"] = lmcPlateMarker ?: ""
                        params["adaptor_GI_to_reg"] = lmcAdaptorGI ?: ""
                        params["adaptor_reg_to_meter"] = lmcAdaptorReg ?: ""
                        params["adaptor_meter_to_GI_pipe"] = lmcAdaptorMeter ?: ""
                        params["female_union_meter_MLC_pipe"] = lmcFemaleMeter ?: ""

                        params["wo_gi_length"] = lmcGiLength  ?: ""
                        params["wo_mlc_length"] = lmcMlcLength ?: ""
                        params["wo_extra_gi"] = lmcExtraGiLength ?: ""
                        params["wo_extra_mlc"] = lmcExtraMlcLength ?: ""
                        params["wo_av_no"] = lmcAvQty ?: ""
                        params["wo_iv_no"] = lmcIvQty ?: ""

                        params["wo_meter_company"] = lmcMeterCompany ?: ""
                        params["wo_initial_meter_reading"] = lmcInitialReading ?: ""
                        params["wo_meter_bracket"] = lmcWoMeterBracket ?: ""
                        params["wo_meter_sticker"] = lmcWoMeterSticker ?: ""
                        params["wo_meter_no"] = lmcWoMeterNumber ?: ""
                        params["wo_regulator_number"] = lmcWoRegulatorNumber ?: ""
                        params["wo_adaptor_GI_to_reg"] = lmcWoAdaptorGI ?: ""
                        params["wo_adaptor_reg_to_meter"] = lmcWoAdaptorReg ?: ""
                        params["wo_adaptor_meter_to_GI_pipe"] = lmcWoAdaptorMeter ?: ""
                        params["wo_female_union_meter_MLC_pipe"] = lmcWoFemaleMeter ?: ""
                    }

                    viewModel.updateRfcNg(params)
                    setupUpdateObserver()

                }else{

                    if(tvDateTime.text.contains("Follow",true)){
                        tvDateTime.error = "Please select date"
                        tvDateTime.requestFocus()
                        return@setOnClickListener
                    }

                    if(etDescription.text.isBlank()){
                        etDescription.error = "Please enter comments"
                        etDescription.requestFocus()
                        return@setOnClickListener
                    }


                    val params = HashMap<String,String>()
                    params["application_number"] = args.appNo
                    params["bp_number"] = bpNo!!
                    params["tpi_id"] = args.tpiId
                    params["customer_info"] = args.customerInfo
                    params["ng_session_id"] = args.sessionId
                    params["rfc_status"] = rfcStatus!!
                    params["rfc_approval_status"] = "Nil"
                    params["rfc_comments"] = ""
//                    params["mmt_testing"] = "0"
                    params["follow_up_date"] = tvDateTime.text.toString()
                    params["comments"] = etDescription.text.toString()
//                    params["extension_modication_of_lmc"] = lmcExtension!!

                    viewModel.updateRfcNg(params)
                    setupNgObserver()
                }

            }

        }
        return binding.root
    }

    private val dateListener = DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
        myDay = i3
        myYear = i
        myMonth = i2 + 1
        val calendar: Calendar = Calendar.getInstance()
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(requireContext(), timeListener, hour, minute,
            true)
        timePickerDialog.show()

    }

    private val timeListener = TimePickerDialog.OnTimeSetListener { timePicker, hr, min ->
        myHour = hr
        myMinute = min

        val date = AppUtils.getFollowUpDateTime("$myDay/$myMonth/$myYear $myHour:$myMinute")
        binding.tvDateTime.text = AppUtils.getFollowUpDateTime("$myDay/$myMonth/$myYear $myHour:$myMinute")
        binding.tvConversationDate.text = AppUtils.getFollowUpDateTime("$myDay/$myMonth/$myYear $myHour:$myMinute")
        LmcStatusFragment.dateTime = "$myDay/$myMonth/$myYear $myHour:$myMinute"
        conversionDate = date.toString()
    }

    private fun submitNgApproval(status:String){
        val dialogBinding = CommentDialogBinding.inflate(LayoutInflater.from(requireContext()))
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        builder.setView(dialogBinding.root)
        val alert = builder.create()
        alert.show()
        dialogBinding.apply {
            if(status.equals("Approved")){
                etComment.visibility = View.GONE
                btnSubmit.text = "Approve"
            }else{
                btnSubmit.text = status
            }
            btnClear.setOnClickListener {
                signature.clear()
            }
            btnSignature.setOnClickListener {
                if(signature.isEmpty){
                    Toast.makeText(requireContext(), "Please add signature", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val bitmap=signature.signatureBitmap
                val fileName: String = SimpleDateFormat("DDMMyyyy_HHmmss").format(Date())
                val file = File(requireActivity().externalCacheDir, "$fileName.jpg")
                val os: OutputStream = BufferedOutputStream(FileOutputStream(file))
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, os)
                os.close()
                val request= UploadRequestModel(
                    bpNumber = bpNo!!, appNo = args.appNo, sessionId = args.sessionId
                )
                setUploadObserver(dialogBinding)
                viewModel.uploadAttachment(request,file, Constants.NG_CUSTOMER_SIGNATURE_FILE)
            }
            btnSubmit.setOnClickListener {
                if(signature.isEnabled){
                    Toast.makeText(requireContext(), "Signature is needed", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val params = HashMap<String,String?>()
                params["application_number"] = args.appNo
                params["bp_number"] = bpNo!!
                params["tpi_id"] = args.tpiId
                params["customer_info"] = args.customerInfo
                params["rfc_approval_status"] = status
                params["rfc_comments"] = etComment.text.toString()
                params["rfc_session_id"] = args.sessionId
                viewModel.updateRfcNg(params)
                alert.dismiss()
                setupNgObserver()

                //if ($rfc_approval_status == "Approved")
            }
        }

        dialogBinding.btnClose.setOnClickListener {
            alert.dismiss()
        }

    }

    private fun setUploadObserver(binding:CommentDialogBinding){
        viewModel.uploadResponse.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.LOADING->{
                        setDialog(true)
                    }
                    Status.SUCCESS->{
                        setDialog(false)
                        if(it.data?.error!!) {
                            Toast.makeText(requireContext(), it.data!!.message, Toast.LENGTH_SHORT)
                                .show()
                        }else {
                            binding.signature.isEnabled = false
                            binding.btnClear.visibility = View.GONE
                            binding.btnSignature.visibility = View.GONE
                        }
                    }
                    Status.ERROR->{
                        setDialog(false)
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }
    }

    private fun setupUpdateObserver() {
        viewModel.rfcNgUpdateResponse.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.LOADING->{
                        setDialog(true)
                    }
                    Status.SUCCESS->{
                        setDialog(false)
                        if(!it.data!!.error){
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                            if(!isLmcModified && hasMeter){
                                val directions = NgApprovalFragmentDirections.actionNgApprovalFragmentToNgVerificationFragment(
                                bpNo = bpNo!!,
                                jmrNo = jmrNo!!,
                                assignedDate = assignedDate!!,
                                workDate = conversionDate!!,
                                appNo = args.appNo,
                                sessionId = args.sessionId,
                                tpiId = args.tpiId,
                                customerInfo = args.customerInfo,
                                conversionDate = conversionDate!!,
                                rfcStatus = rfcStatus,
                                mmtStatus = mmtStatus,
                                leakageStatus = leakageStatus,
                                gasPressure = gasPressure,
                                finalReading = finalReading,
                                burnerType = burnerType,
                                hoseLength = hoseLength,
                                ng65Length = nozzle65Length,
                                ng90Length = nozzle90Length,
                                ng110Length = nozzle110Length,
                                ng125Length = nozzle125Length,
                                acknowledgeId = acknowledgeId,
                                drsNumber = drsNumber,
                                srNumber = srNumber,
                                lmcGiClamp = lmcGiClamp,
                                lmcMlcClamp = lmcMlcClamp,
                                lmcGiMfElbow = lmcGiMfElbow,
                                lmcGiFfElbow = lmcGiFfElbow,
                                lmcGi2 = lmcGi2,
                                lmcGi3 = lmcGi3,
                                lmcGi4 = lmcGi4,
                                lmcGi6 = lmcGi6,
                                lmcGi8 = lmcGi8,
                                lmcGiTee = lmcGiTee,
                                lmcMlcTee = lmcMlcTee,
                                lmcGiSocket = lmcGiSocket,
                                lmcMaleUnion = lmcMaleUnion,
                                lmcFemaleUnion = lmcFemaleUnion,
                                lmcMeterBracket = lmcMeterBracket,
                                lmcMeterSticker = lmcMeterSticker,
                                lmcPlateMarker = lmcPlateMarker,
                                lmcAdaptorGI = lmcAdaptorGI,
                                lmcAdaptorReg = lmcAdaptorReg,
                                lmcAdaptorMeter = lmcAdaptorMeter,
                                lmcFemaleMeter = lmcFemaleMeter,
                                lmcMeterNo = lmcMeterNo,
                                lmcRegulatorNo = lmcRegulatorNo,
                                lmcGiLength = lmcGiLength,
                                lmcMlcLength = lmcMlcLength,
                                lmcAvQty = lmcAvQty,
                                lmcIvQty = lmcIvQty,
                                lmcExtraGiLength = lmcExtraGiLength,
                                lmcExtraMlcLength = lmcExtraMlcLength,
                                lmcMeterCompany = lmcMeterCompany,
                                lmcInitialReading = lmcInitialReading,
                                lmcWoMeterBracket = lmcWoMeterBracket,
                                lmcWoMeterSticker = lmcWoMeterSticker,
                                lmcWoAdaptorGI = lmcWoAdaptorGI,
                                lmcWoAdaptorReg = lmcWoAdaptorReg,
                                lmcWoAdaptorMeter = lmcWoAdaptorMeter,
                                lmcWoFemaleMeter = lmcWoFemaleMeter,
                                lmcWoMeterNumber = lmcWoMeterNumber,
                                lmcWoRegulatorNumber = lmcWoRegulatorNumber,
                                    lmcExtension = lmcExtension,
                                    giUnion = giUnion
                                )

                                findNavController().navigate(directions)
                            }else {
                                val directions =
                                    NgApprovalFragmentDirections.actionNgApprovalFragmentToRfcExtensionFragment(
                                        appNo = args.appNo,
                                        sessionId = args.sessionId,
                                        tpiId = args.tpiId,
                                        customerInfo = args.customerInfo,
                                        conversionDate = conversionDate!!,
                                        rfcStatus = rfcStatus,
                                        mmtStatus = mmtStatus,
                                        leakageStatus = leakageStatus,
                                        gasPressure = gasPressure,
                                        finalReading = finalReading,
                                        burnerType = burnerType,
                                        hoseLength = hoseLength,
                                        ng65Length = nozzle65Length,
                                        ng90Length = nozzle90Length,
                                        ng110Length = nozzle110Length,
                                        ng125Length = nozzle125Length,
                                        acknowledgeId = acknowledgeId,
                                        drsNumber = drsNumber,
                                        srNumber = srNumber,
                                        bpNo = bpNo!!,
                                        jmrNo = jmrNo!!,
                                        assignedDate = assignedDate!!,
                                        workDate = conversionDate!!,
                                        lmcExtension = lmcExtension,
                                        giUnion = giUnion
                                    )
//
                                findNavController().navigate(directions)
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

    private fun setupNgObserver() {
        viewModel.rfcNgUpdateResponse.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.LOADING->{
//                        setDialog(true)
                    }
                    Status.SUCCESS->{
//                        setDialog(false)
                        if(!it.data!!.error){
                            findNavController().popBackStack(R.id.rfcHomeFragment,false)
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    Status.ERROR->{
//                        setDialog(false)

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
                            it.data.lmcExtensionList.forEach { lmcType ->
                                AppCache.lmcExtensionList[lmcType.name] = lmcType.id
                            }
                            it.data.ngStatusList.forEach { ngType->
                                AppCache.ngStatusList[ngType.name] = ngType.id
                            }
                            it.data.burnerList.forEach {
                                AppCache.ngBurnerList[it.name] = it.id
                            }
                            it.data.hoseList.forEach {
                                AppCache.ngHoseList[it.name] = it.id
                            }
                            it.data.nozzle65List.forEach {
                                AppCache.ngNozzle65List[it.name] = it.id
                            }
                            it.data.nozzle90List.forEach {
                                AppCache.ngNozzle90List[it.name] = it.id
                            }
                            it.data.nozzle110List.forEach {
                                AppCache.ngNozzle110List[it.name] = it.id
                            }
                            it.data.nozzle125List.forEach {
                                AppCache.ngNozzle125List[it.name] = it.id
                            }

                            val lmcList = mutableListOf<String>()
                            AppCache.lmcExtensionList.keys.forEach {
                                lmcList.add(it)
                            }

                            lmcExtensionSpinner = SpinnerDialog(
                                activity,
                                lmcList as ArrayList<String>,
                                "Select LMC Extension",
                                "Close"
                            )

                            val list = mutableListOf<String>()
                            AppCache.ngStatusList.keys.forEach {
                                list.add(it)
                            }

                            ngStatusSpinner = SpinnerDialog(
                                activity,
                                list as ArrayList<String>,
                                "Select NG Status",
                                "Close"
                            )

                            val burnerList = arrayListOf<String>()
                            val nozzle65List = arrayListOf<String>()
                            val nozzle90List = arrayListOf<String>()
                            val nozzle110List = arrayListOf<String>()
                            val nozzle125List = arrayListOf<String>()
                            val hoseList = arrayListOf<String>()

                            AppCache.ngBurnerList.keys.forEach {
                                burnerList.add(it)
                            }
                            burnerSpinner = SpinnerDialog(
                                activity,
                                burnerList,
                                "Select Burner Type",
                                "Close"
                            )

                            AppCache.ngNozzle65List.keys.forEach {
                                nozzle65List.add(it)
                            }
                            nozzleSpinner65 = SpinnerDialog(
                                activity,
                                nozzle65List,
                                "Select Nozzle Size",
                                "Close"
                            )
                            AppCache.ngNozzle90List.keys.forEach {
                                nozzle90List.add(it)
                            }
                            nozzleSpinner90 = SpinnerDialog(
                                activity,
                                nozzle90List,
                                "Select Nozzle Size",
                                "Close"
                            )

                            AppCache.ngNozzle110List.keys.forEach {
                                nozzle110List.add(it)
                            }
                            nozzleSpinner110 = SpinnerDialog(
                                activity,
                                nozzle110List,
                                "Select Nozzle Size",
                                "Close"
                            )

                            AppCache.ngNozzle125List.keys.forEach {
                                nozzle125List.add(it)
                            }
                            nozzleSpinner125 = SpinnerDialog(
                                activity,
                                nozzle125List,
                                "Select Nozzle Size",
                                "Close"
                            )

                            AppCache.ngHoseList.keys.forEach {
                                hoseList.add(it)
                            }
                            hoseSpinner = SpinnerDialog(
                                activity,
                                hoseList,
                                "Select Hose Length",
                                "Close"
                            )

                            binding.apply {
                                lmcExtensionSpinner?.bindOnSpinerListener { item, position ->
                                    spinnerLmcExtension.text = item
                                    spinnerLmcExtension.error = null
                                    lmcExtension = item

                                    if(item.contains("yes",true)){
                                        isLmcModified = true
                                    }else isLmcModified = false

                                }

                                burnerSpinner?.bindOnSpinerListener { item, position ->
                                    spinnerBurnerType.text = item
                                    spinnerBurnerType.error = null
                                    burnerType = item
                                }

                                hoseSpinner?.bindOnSpinerListener { item, position ->
                                    spinnerHose.text = item
                                    spinnerHose.error = null
                                    hoseLength = item
                                }

                                nozzleSpinner65?.bindOnSpinerListener { item, position ->
                                    spinnerNb65.text = item
                                    spinnerNb65.error = null
                                    nozzle65Length = item
                                }

                                nozzleSpinner90?.bindOnSpinerListener { item, position ->
                                    spinnerNb90.text = item
                                    spinnerNb90.error = null
                                    nozzle90Length = item

                                }

                                nozzleSpinner110?.bindOnSpinerListener { item, position ->
                                    spinnerNb110.text = item
                                    spinnerNb110.error = null
                                    nozzle110Length = item
                                }

                                nozzleSpinner125?.bindOnSpinerListener { item, position ->
                                    spinnerNb125.text = item
                                    spinnerNb125.error = null
                                    nozzle125Length = item
                                }

                            }


                            rfcStatusSpinner?.bindOnSpinerListener { item, position ->
                                binding.apply {
                                    spinnerRfcStatus.text = item
                                    spinnerRfcStatus.error = null
                                    rfcStatus = item

                                    if(item.contains("passed",true)){
                                        tvDateTitle.visibility = View.VISIBLE
                                        tvConversationDate.visibility = View.VISIBLE
                                        tvMmtTesting.visibility =View.VISIBLE
                                        rgMmt.visibility = View.VISIBLE
                                        rbMmtDone.isChecked = false
                                        llFollowUp.visibility = View.GONE
//                                        tvDrsNo.visibility = View.VISIBLE
//                                        etDrsNo.visibility = View.VISIBLE
//                                        tvSrNo.visibility = View.VISIBLE
//                                        etSrNo.visibility = View.VISIBLE
                                        tvExtension.visibility = View.VISIBLE
                                        spinnerLmcExtension.visibility = View.VISIBLE
                                        isFailed = false
                                        btnSubmit.text = "NEXT"
                                    }else{
                                        tvDateTitle.visibility = View.GONE
                                        llFollowUp.visibility = View.VISIBLE
                                        tvMmtTesting.visibility =View.GONE
                                        tvConversationDate.visibility = View.GONE
                                        rgMmt.visibility = View.GONE
//                                        tvDrsNo.visibility = View.GONE
//                                        etDrsNo.visibility = View.GONE
//                                        tvSrNo.visibility = View.GONE
//                                        etSrNo.visibility = View.GONE
                                        isFailed = true
                                        btnSubmit.text = "SUBMIT"
                                        cvSr.visibility = View.GONE
                                        tvExtension.visibility = View.GONE
                                        spinnerLmcExtension.visibility = View.GONE
                                        cvMeter.visibility = View.GONE
                                        tvLeakageTesting.visibility = View.GONE
                                        rgLeakage.visibility = View.GONE
                                        tvLiveGas.visibility = View.GONE
                                        etLiveGas.visibility = View.GONE
//                                        tvMeterReading.visibility = View.GONE
//                                        etMeterReading.visibility = View.GONE
                                        tvBurnerType.visibility = View.GONE
                                        spinnerBurnerType.visibility = View.GONE
                                        tvHoseOptions.visibility = View.GONE
                                        spinnerHose.visibility = View.GONE
                                        clNozzle.visibility = View.GONE
                                    }

                                    if ((item.contains("passed", true)) || (item.contains("Failed", true))) {
                                        tvFollowTitle.visibility = View.GONE
                                        tvDateTime.visibility = View.GONE
                                    }else {
                                        tvFollowTitle.visibility = View.VISIBLE
                                        tvDateTime.visibility = View.VISIBLE
                                    }
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
                            if(customerInfo.lmcExtensionWithMeter!!.lmcMeterStatus!!.contains("with meter",true)){
                                hasMeter = true
                            } else {
                                binding.apply {
                                    clMeterMake.visibility = View.GONE
                                    clMeterNo.visibility = View.GONE
                                    clMeterType.visibility = View.GONE
                                }
                            }

                            if(customerInfo.lmcExtensionWithMeter!!.lmcModification != null && customerInfo.lmcExtensionWithMeter!!.lmcModification!!.contains("yes",true)){
                                isLmcModified = true
                            }

                            val installationDetails = it.data.customerInfo.installationDetails
                            bpNo = customerInfo.bpnumber
                            jmrNo = customerInfo.jmrNo
                            lmcExtension = it.data.customerInfo.lmcExtensionWithMeter!!.lmcModification
                            val withMeter = it.data.customerInfo.lmcExtensionWithMeter
                            val withoutMeter = it.data.customerInfo.lmcExtensionWithoutMeter!!

                            assignedDate = customerInfo.ngVerificationDetails!!.assignedDate
                            binding.apply {
                                spinnerLmcExtension.text = lmcExtension ?: "Select LMC"
                                tvCustomerName.text = customerInfo.customerName
                                tvBpNo.text = customerInfo.bpnumber
                                tvHouseNo.text = installationDetails!!.houseNo
                                tvSociety.text = installationDetails.society
                                tvBlock.text = installationDetails.blackTower
                                tvFloor.text = installationDetails.floor
                                tvArea.text = installationDetails.area
                                tvCity.text = installationDetails.city
                                etMobile.setText(installationDetails.mobile)
                                etAlternateNo.setText(installationDetails.mobile)
//                                tvClaimDate.text = installationDetails.claimDate
//                                tvCustomerType.text = installationDetails.customerType
                                tvMeterNo.text = installationDetails.meterno
                                tvMeterMake.text = installationDetails.metermake
                                tvMeterType.text = installationDetails.metertype
                                tvLmcType.text = withMeter.lmcMeterStatus
                                tvConversationDate.text = installationDetails.ngConversionDate ?: "Select Date"
                                spinnerRfcStatus.text = installationDetails.rfcStatus ?: "Select Status"
//                                etDrsNo.setText(installationDetails.drsNumber)
//                                etSrNo.setText(installationDetails.srNumber)
                                drsNumber = installationDetails.drsNumber ?: ""
                                srNumber = installationDetails.srNumber ?: ""
                                rfcStatus = installationDetails.rfcStatus
                                burnerType = installationDetails.burnerType ?: ""
                                hoseLength = installationDetails.hosePipe ?: ""
                                conversionDate  = installationDetails.ngConversionDate
                                acknowledgeId = installationDetails.acknowledgeId
                                nozzle65Length =installationDetails.nozzle65 ?: ""
                                nozzle90Length = installationDetails.nozzle90 ?: ""
                                nozzle110Length = installationDetails.nozzle110 ?: ""
                                nozzle125Length = installationDetails.nozzle125 ?: ""
                                followUpDate = installationDetails.followupDate ?: ""
                                tvDateTime.text = installationDetails.followupDate.toString()
                                etDescription.setText(installationDetails.comment.toString())

                                if(installationDetails.rfcStatus != null) {
                                    if (installationDetails.rfcStatus == "Passed") {
                                        tvMmtTesting.visibility = View.VISIBLE
                                        rgMmt.visibility = View.VISIBLE
                                        tvDateTitle.visibility = View.VISIBLE
//                                    tvDrsNo.visibility = View.VISIBLE
//                                    etDrsNo.visibility = View.VISIBLE
//                                    tvSrNo.visibility = View.VISIBLE
//                                    etSrNo.visibility = View.VISIBLE
                                        tvConversationDate.visibility = View.VISIBLE
                                    } else {
                                        isFailed = true
                                        tvDateTitle.visibility = View.GONE
                                        llFollowUp.visibility = View.VISIBLE
                                        tvMmtTesting.visibility = View.GONE
                                        tvConversationDate.visibility = View.GONE
                                        rgMmt.visibility = View.GONE
                                        cvSr.visibility = View.GONE
                                        tvExtension.visibility = View.GONE
                                        spinnerLmcExtension.visibility = View.GONE
                                        cvMeter.visibility = View.GONE
                                        tvLeakageTesting.visibility = View.GONE
                                        rgLeakage.visibility = View.GONE
                                        tvLiveGas.visibility = View.GONE
                                        etLiveGas.visibility = View.GONE
//                                        tvMeterReading.visibility = View.GONE
//                                        etMeterReading.visibility = View.GONE
                                        tvBurnerType.visibility = View.GONE
                                        spinnerBurnerType.visibility = View.GONE
                                        tvHoseOptions.visibility = View.GONE
                                        spinnerHose.visibility = View.GONE
                                        clNozzle.visibility = View.GONE
                                        btnSubmit.text = "SUBMIT"
                                    }
                                }
                                if(AppCache.isTpi && installationDetails.rfcStatus == "Failed"){
                                    btnSubmit.visibility = View.GONE
                                    btnApprove.visibility = View.VISIBLE
                                    btnDecline.visibility = View.VISIBLE
                                    tvFollowTitle.visibility = View.GONE
                                    tvDateTime.visibility = View.GONE
                                }
                                if(installationDetails.mmtTesting != null && installationDetails.rfcStatus == "Passed"){
                                    if (installationDetails.mmtTesting == "1") rbMmtDone.isChecked = true else rbMmtNotDone.isChecked = true
                                }

                                if(installationDetails.leakageTesting != null){
                                    if (installationDetails.leakageTesting == "1") rbLeakageDone.isChecked = true else rbLeakageNotDone.isChecked = true
                                }
//                                etLiveGas.setText(installationDetails.gasPressure)
//                                etDrsNo.setText(installationDetails.drsNumber)
//                                etSrNo.setText(installationDetails.srNumber)
//                                etGiUnion.setText(installationDetails.giUnion)
//                                etMeterReading.setText(installationDetails.meterReading)
//                                spinnerBurnerType.text = installationDetails.burnerType ?: "Select Burner"
//                                spinnerHose.text = installationDetails.hosePipe ?: "Select Hose length"
//                                spinnerNb65.text = installationDetails.nozzle65 ?: ""
//                                spinnerNb90.text = installationDetails.nozzle90 ?: ""
//                                spinnerNb110.text = installationDetails.nozzle110 ?: ""
//                                spinnerNb125.text = installationDetails.nozzle125 ?: ""

                                if(args.status == "done" || args.status == "hold" || args.status == "pending"){
                                    lmcGiClamp = withMeter.giClamp
                                    lmcMlcClamp = withMeter.mlcClamp
                                    lmcGiMfElbow = withMeter.giMfElbow
                                    lmcGiFfElbow = withMeter.giFfElbow
                                    etDescription.setText(installationDetails.comment)
                                    tvDateTime.text = installationDetails.followupDate ?: "Select Follow Up date"
                                    lmcGi2 = withMeter.gi2Nipple
                                    lmcGi3 = withMeter.gi3Nipple
                                    lmcGi4 = withMeter.gi4Nipple
                                    lmcGi6 = withMeter.gi6Nipple
                                    lmcGi8 = withMeter.gi8Nipple
                                    lmcGiTee = withMeter.giTee
                                    lmcMlcTee = withMeter.mlcTee
                                    lmcGiSocket = withMeter.giSocket
                                    lmcMaleUnion = withMeter.mlcMaleUnion
                                    lmcFemaleUnion = withMeter.mlcFemaleUnion
                                    lmcMeterBracket = withMeter.meterBracket
                                    lmcMeterSticker = withMeter.meterSticker
                                    lmcPlateMarker = withMeter.plateMarker
                                    lmcAdaptorGI = withMeter.adaptorGi
                                    lmcAdaptorReg = withMeter.adaptorReg
                                    lmcAdaptorMeter = withMeter.adaptorMeter
                                    lmcFemaleMeter = withMeter.femaleUnion
                                    lmcMeterNo = withMeter.meterNo
                                    lmcRegulatorNo = withMeter.regulatorNo
                                    lmcGiLength = withoutMeter.woGiLength
                                    lmcMlcLength = withoutMeter.woMlcLength
                                    lmcAvQty = withoutMeter.woIvNo
                                    lmcIvQty = withoutMeter.woAvNo
                                    lmcExtraGiLength = withoutMeter.woExtraGi
                                    lmcExtraMlcLength = withoutMeter.woExtraMlc
                                    lmcMeterCompany = withoutMeter.woMeterCompany
                                    lmcInitialReading = withoutMeter.woInitialReading
                                    lmcWoMeterBracket = withoutMeter.woMeterBracket
                                    lmcWoMeterSticker = withoutMeter.woMeterSticker
                                    lmcWoAdaptorGI = withoutMeter.woAdaptorGi
                                    lmcWoAdaptorReg = withoutMeter.woAdaptorReg
                                    lmcWoAdaptorMeter = withoutMeter.woAdaptorMeter
                                    lmcWoFemaleMeter = withoutMeter.woFemaleMeter
                                    lmcWoMeterNumber = withoutMeter.woMeterNo
                                    lmcWoRegulatorNumber = withoutMeter.woRegulatorNo

                                    if (!installationDetails.gasPressure.isNullOrEmpty()) {
                                        etLiveGas.setText(installationDetails.gasPressure)
                                    }

                                    if (!installationDetails.drsNumber.isNullOrEmpty()) {
                                        etDrsNo.setText(installationDetails.drsNumber)
                                    }

                                    if (!installationDetails.srNumber.isNullOrEmpty()) {
                                        etSrNo.setText(installationDetails.srNumber)
                                    }

                                    if (!installationDetails.giUnion.isNullOrEmpty()) {
                                        etGiUnion.setText(installationDetails.giUnion)
                                    }

                                    if (!installationDetails.comment.isNullOrEmpty()) {
                                        etDescription.setText(installationDetails.comment)
                                    }

                                    if (!installationDetails.followupDate.isNullOrEmpty()) {
                                        tvDateTime.text = installationDetails.followupDate
                                    }

                                    if (!installationDetails.meterReading.isNullOrEmpty()) {
                                        etMeterReading.setText(installationDetails.meterReading)
                                    }

                                    if (!installationDetails.burnerType.isNullOrEmpty()) {
                                        spinnerBurnerType.text = installationDetails.burnerType
                                    }

                                    if (!installationDetails.hosePipe.isNullOrEmpty()) {
                                        spinnerHose.text = installationDetails.hosePipe
                                    }

                                    if (!installationDetails.nozzle65.isNullOrEmpty()) {
                                        spinnerNb65.text = installationDetails.nozzle65
                                    }

                                    if (!installationDetails.nozzle90.isNullOrEmpty()) {
                                        spinnerNb90.text = installationDetails.nozzle90
                                    }

                                    if (!installationDetails.nozzle110.isNullOrEmpty()) {
                                        spinnerNb110.text = installationDetails.nozzle110
                                    }

                                    if (!installationDetails.nozzle125.isNullOrEmpty()) {
                                        spinnerNb125.text = installationDetails.nozzle125
                                    }

                                }

                                if ((installationDetails.rfcStatus?.lowercase() == "failed") || (installationDetails.rfcStatus?.lowercase() == "passed")) {
                                    tvFollowTitle.visibility = View.GONE
                                    tvDateTime.visibility = View.GONE
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

    private fun setDialog(show: Boolean) {
        if (show) dialog!!.show() else dialog!!.dismiss()
    }

}