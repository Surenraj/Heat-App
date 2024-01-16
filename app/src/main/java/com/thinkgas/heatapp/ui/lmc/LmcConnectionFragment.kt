package com.thinkgas.heatapp.ui.lmc

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.thinkgas.heatapp.R
import com.thinkgas.heatapp.data.cache.AppCache
import com.thinkgas.heatapp.data.remote.model.LmcConnectionModel
import com.thinkgas.heatapp.databinding.FragmentLmcConnectionBinding
import com.thinkgas.heatapp.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@AndroidEntryPoint
class LmcConnectionFragment : Fragment() {
    private var _binding: FragmentLmcConnectionBinding? = null
    private val binding get() = _binding!!
    var lmcTypeSpinner: SpinnerDialog? = null
    var lmcMeterDetailSpinner: SpinnerDialog? = null
    var lmcMeterSpinner: SpinnerDialog? = null
    var lmcPropertySpinner: SpinnerDialog? = null
    var lmcGasSpinner: SpinnerDialog? = null
    private val viewModel by viewModels<LmcStatusViewModel>()
    private val args by navArgs<LmcConnectionFragmentArgs>()
    private var dialog: Dialog? = null


    companion object{
        var qrValue:String? = null
        var qrError:String? = null
        var lmcType:String? = null
        var lmcMeterDetail:String? = null
        var lmcMeterNumber:String? = null
        var lmcMeterId:String? = null
        var lmcMeterType:String? = null
        var lmcInitialReading:String? = null
        var lmcRegulator:String? = null
        var lmcGI:String? = null
        var lmcCU:String? = null
        var lmcNoOfAV:String? = null
        var lmcNoOfIV:String? = null
        var lmcPipeLength:String? = null
        var lmcProperty:String? = null
        var lmcGas:String? = null
        var lmcExtraGiLength:String? = null
        var lmcExtraMlclength:String? = null
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

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLmcConnectionBinding.inflate(inflater, container, false)


        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.progress)
        dialog = builder.create()

        val lmcList = mutableListOf<String>()
        AppCache.lmcTypeList.keys.forEach {
            lmcList.add(it)
        }

        lmcTypeSpinner = SpinnerDialog(
            activity,
            lmcList as ArrayList<String>,
            "Select LMC Type"
        )

        val meterDetailsList = mutableListOf<String>()
        AppCache.lmcMeterDetails.keys.forEach {
            meterDetailsList.add(it)
        }
        lmcMeterDetailSpinner = SpinnerDialog(
            activity,
            meterDetailsList as ArrayList<String>,
            "Select Meter Details"
        )

        val meterList = mutableListOf<String>()
        AppCache.lmcMeterList.keys.forEach {
            meterList.add(it)
        }
        lmcMeterSpinner = SpinnerDialog(
            activity,
            meterList as ArrayList<String>,
            "Select Meter No."
        )

        val propertyList = mutableListOf<String>()
        AppCache.lmcPropertyList.keys.forEach {
            propertyList.add(it)
        }
        lmcPropertySpinner = SpinnerDialog(
            activity,
            propertyList as ArrayList<String>,
            "Select Property Type"
        )

        val gasList = mutableListOf<String>()
        AppCache.lmcGasType.keys.forEach {
            gasList.add(it)
        }
        lmcGasSpinner = SpinnerDialog(
            activity,
            gasList as ArrayList<String>,
            "Select GC Status"
        )

        binding.apply {

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
                    val directions = LmcConnectionFragmentDirections.actionLmcConnectionFragmentToLoginFragment()
                    findNavController().navigate(directions)

                }
                logoutBuilder.setNegativeButton("No") { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                val logoutAlert = logoutBuilder.create()
                logoutAlert.show()


            }

            if(args.lmcExecution != null && args.lmcExecution!!.contains("without",true))
            {
                tvMeterDetails.visibility = View.GONE
                spinnerMeter.visibility = View.GONE
                tvMeterIdText.visibility = View.GONE
                tvMeterId.visibility = View.GONE
                tvMeterTypeText.visibility = View.GONE
                tvMeterType.visibility = View.GONE
                tvInitialMeter.visibility = View.GONE
                ivQr.visibility = View.GONE
                etInitialMeter.visibility = View.GONE
                tvRegulatorNumber.visibility = View.GONE
                etRegulatorNo.visibility = View.GONE
                tvMeterBracket.visibility = View.GONE
                etMeterBracket.visibility = View.GONE
                tvMeterSticker.visibility = View.GONE
                etMeterSticker.visibility = View.GONE
                tvAdaptorGi.visibility = View.GONE
                etAdaptorGi.visibility = View.GONE
                etAdaptorMeter.visibility = View.GONE
                etAdaptorReg.visibility =View.GONE
                tvAdaptorMeter.visibility = View.GONE
                tvAdaptorReg.visibility =View.GONE
                tvFemaleUnion.visibility = View.GONE
                etFemaleUnion.visibility = View.GONE
            }


            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }

            if(AppCache.isTpi){
                spinnerRfc.isEnabled = false
                spinnerRfc.setTextColor(Color.parseColor("#545454"))
                spinnerMeter.isEnabled = false
                spinnerMeter.setTextColor(Color.parseColor("#545454"))
//                spinnerMeterNo.isEnabled = false
//                spinnerMeterNo.setTextColor(Color.parseColor("#545454"))
                spinnerGas.isEnabled = false
                spinnerGas.setTextColor(Color.parseColor("#545454"))
                spinnerProperty.isEnabled = false
                spinnerProperty.setTextColor(Color.parseColor("#545454"))
                etFirstName.isEnabled = false
                etMobile.isEnabled = false
                etMiddleName.isEnabled = false
                etLastName.isEnabled = false
                etEmail.isEnabled = false
                btnUpdate.isEnabled = false
                btnUpdate.visibility = View.GONE
                etInitialMeter.isEnabled = false
                etRegulatorNo.isEnabled = false
                etCuInstallation.isEnabled = false
                etGiInstallation.isEnabled = false
                etAvNo.isEnabled = false
                etIvNo.isEnabled = false
//                etPipeLength.isEnabled = false
                tvMeterType.isEnabled = false
                etGiClamp.isEnabled = false
                etMlcClamp.isEnabled = false
                etGiMf.isEnabled = false
                etGiFf.isEnabled = false
                etGi2.isEnabled = false
                etGi3.isEnabled = false
                etGi4.isEnabled = false
                etGi6.isEnabled = false
                etGi8.isEnabled = false
                etGiTee.isEnabled = false
                etMlcTee.isEnabled = false
                etGiSocket.isEnabled = false
                etMlcMale.isEnabled = false
                etMlcFemale.isEnabled = false
                etPlateMarker.isEnabled = false
                ivQr.isEnabled = false
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

            if(lmcType!=null){
                spinnerRfc.text = lmcType
            }

            if(lmcMeterDetail != null){
                spinnerMeter.text = lmcMeterDetail
            }

//            if(lmcMeterNumber != null){
//                spinnerMeterNo.text = lmcMeterNumber
//            }

            if(lmcMeterId != null){
                tvMeterId.setText(lmcMeterId)
            }

            if(lmcMeterType != null){
                tvMeterType.setText(lmcMeterType)
            }

            if(lmcInitialReading != null){
                etInitialMeter.setText(lmcInitialReading)
            }

            if(lmcRegulator != null){
                etRegulatorNo.setText(lmcRegulator)
            }

            if(lmcGI != null){
                etGiInstallation.setText(lmcGI)
            }

            if(lmcCU != null){
                etCuInstallation.setText(lmcCU)
            }

            if(lmcNoOfAV != null){
                etAvNo.setText(lmcNoOfAV)
            }

            if(lmcNoOfIV != null){
                etAvNo.setText(lmcNoOfIV)
            }

            if(lmcGiClamp != null){
                etGiClamp.setText(lmcNoOfIV)
            }

            if(lmcMlcClamp != null){
                etMlcClamp.setText(lmcNoOfIV)
            }

            if(lmcGiMfElbow != null){
                etGiMf.setText(lmcNoOfIV)
            }

            if(lmcGiFfElbow != null){
                etGiFf.setText(lmcNoOfIV)
            }

            if(lmcGi2 != null){
                etGi2.setText(lmcNoOfIV)
            }

            if(lmcGi3 != null){
                etGi3.setText(lmcNoOfIV)
            }

            if(lmcGi4 != null){
                etGi4.setText(lmcNoOfIV)
            }

            if(lmcGi6 != null){
                etGi6.setText(lmcNoOfIV)
            }

            if(lmcGi8 != null){
                etGi8.setText(lmcNoOfIV)
            }

            if(lmcGiTee != null){
                etGiTee.setText(lmcNoOfIV)
            }

            if(lmcMlcTee != null){
                etMlcTee.setText(lmcNoOfIV)
            }

            if(lmcGiSocket != null){
                etGiSocket.setText(lmcNoOfIV)
            }

            if(lmcMaleUnion != null){
                etMlcMale.setText(lmcNoOfIV)
            }

            if(lmcFemaleUnion != null){
                etMlcFemale.setText(lmcNoOfIV)
            }

            if(lmcMeterBracket != null){
                etMeterBracket.setText(lmcNoOfIV)
            }

            if(lmcMeterSticker != null){
                etMeterSticker.setText(lmcNoOfIV)
            }

            if(lmcPlateMarker != null){
                etPlateMarker.setText(lmcNoOfIV)
            }

            if(lmcAdaptorGI != null){
                etAdaptorGi.setText(lmcNoOfIV)
            }

            if(lmcAdaptorReg != null){
                etAdaptorReg.setText(lmcNoOfIV)
            }

            if(lmcAdaptorMeter != null){
                etAdaptorMeter.setText(lmcNoOfIV)
            }

            if(lmcFemaleMeter != null){
                etFemaleUnion.setText(lmcNoOfIV)
            }


//            if(lmcPipeLength != null){
//                etPipeLength.setText(lmcPipeLength)
//            }

            if(lmcProperty != null){
                spinnerProperty.setText(lmcProperty)
            }

            if(lmcGas != null){
                spinnerGas.setText(lmcGas)
            }

            lmcTypeSpinner?.bindOnSpinerListener { item, position ->
                spinnerRfc.text = item
                spinnerRfc.error = null
                lmcType = item
            }
            lmcMeterDetailSpinner?.bindOnSpinerListener { item, position ->
                spinnerMeter.text = item
                spinnerMeter.error = null
                lmcMeterDetail = item
            }
//            lmcMeterSpinner?.bindOnSpinerListener { item, position ->
//                spinnerMeterNo.text = item
//                spinnerMeterNo.error = null
//                lmcMeterNumber = item
//            }
            lmcPropertySpinner?.bindOnSpinerListener { item, position ->
                spinnerProperty.text = item
                spinnerProperty.error = null
                lmcProperty = item
            }
            lmcGasSpinner?.bindOnSpinerListener { item, position ->
                spinnerGas.text = item
                spinnerGas.error = null
                lmcGas = item
            }

            Log.d("lmcConnection: ",args.toString())
            etFirstName.setText(args.firstName)
            etMobile.setText(args.mobile)
            etLastName.setText(args.lastName)
            etEmail.setText(args.email)
            etMiddleName.setText(args.middleName)

            etGiInstallation.addTextChangedListener(object : TextWatcher{
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
                                lmcExtraMlclength = "${length-12}"
                                etExtraMlcLength.setText(lmcExtraMlclength)
                            }else {
                                lmcExtraMlclength = "0"
                                etExtraMlcLength.setText(lmcExtraMlclength)
                            }
                        }
                    }
                }

            })

            btnUpdate.setOnClickListener {

                if(etFirstName.text.isNullOrBlank()){
                    etFirstName.error = "Enter first name"
                    etFirstName.requestFocus()
                    return@setOnClickListener
                }

                if(etLastName.text.isNullOrBlank()){
                    etLastName.error = "Enter last name"
                    etLastName.requestFocus()
                    return@setOnClickListener
                }

                if(etEmail.text.isNullOrBlank()){
                    etEmail.error = "Enter email address"
                    etEmail.requestFocus()
                    return@setOnClickListener
                }

                val params = HashMap<String,String>()
                params["session_id"] = args.sessionId
                params["application_number"] = args.appNo
                params["bpnumber"] = args.bpNo
                params["firstname"] = etFirstName.text.toString()
                params["middlename"] = etMiddleName.text.toString()
                params["lastname"] = etLastName.text.toString()
                params["mobile_number"] = etMobile.text.toString()
                params["email"] = etEmail.text.toString()

                viewModel.updateCustomerDetails(params)
                viewModel.customerResponse.observe(viewLifecycleOwner){
                    if(it.data!=null){
                        when(it.status){
                            Status.ERROR->{
                                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            }
                            Status.LOADING->{

                            }
                            Status.SUCCESS->{
                                Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

            }

            if(args.status == "hold" || args.status == "done"){
                spinnerRfc.text = args.lmcType
                spinnerMeter.text = args.meterDetails
                spinnerGas.text = args.gasType
                spinnerProperty.text = args.propertyType
//                spinnerMeterNo.text = args.meterNo
                tvMeterId.setText(args.meterSerialNo)
                tvMeterType.setText(args.meterType)
                etInitialMeter.setText(args.initialReading)
                etGiInstallation.setText(args.giNo)
                etCuInstallation.setText(args.cuNo)
                etIvNo.setText(args.ivNo)
                etAvNo.setText(args.avNo)
//                etPipeLength.setText(args.pipeLength)
                etRegulatorNo.setText(args.regNo)
                lmcType = args.lmcType
                lmcMeterDetail = args.meterDetails
                lmcMeterNumber = args.meterNo
                lmcMeterId = args.meterSerialNo
                lmcProperty = args.propertyType
                lmcGas = args.gasType

                etGiClamp.setText(args.lmcConnectionModel!!.lmcGiClamp)
                etMlcClamp.setText(args.lmcConnectionModel!!.lmcMlcClamp)
                etGiMf.setText(args.lmcConnectionModel!!.lmcGiMfElbow)
                etGiFf.setText(args.lmcConnectionModel!!.lmcGiFfElbow)
                etGi2.setText(args.lmcConnectionModel!!.lmcGi2)
                etGi3.setText(args.lmcConnectionModel!!.lmcGi3)
                etGi4.setText(args.lmcConnectionModel!!.lmcGi4)
                etGi6.setText(args.lmcConnectionModel!!.lmcGi6)
                etGi8.setText(args.lmcConnectionModel!!.lmcGi8)
                etGiTee.setText(args.lmcConnectionModel!!.lmcGiTee)
                etMlcTee.setText(args.lmcConnectionModel!!.lmcMlcTee)
                etGiSocket.setText(args.lmcConnectionModel!!.lmcGiSocket)
                etMlcMale.setText(args.lmcConnectionModel!!.lmcMaleUnion)
                etMlcFemale.setText(args.lmcConnectionModel!!.lmcFemaleUnion)
                etMeterBracket.setText(args.lmcConnectionModel!!.lmcMeterBracket)
                etMeterSticker.setText(args.lmcConnectionModel!!.lmcMeterSticker)
                etPlateMarker.setText(args.lmcConnectionModel!!.lmcPlateMarker)
                etAdaptorGi.setText(args.lmcConnectionModel!!.lmcAdaptorGI)
                etAdaptorReg.setText(args.lmcConnectionModel!!.lmcAdaptorReg)
                etAdaptorMeter.setText(args.lmcConnectionModel!!.lmcAdaptorMeter)
                etFemaleUnion.setText(args.lmcConnectionModel!!.lmcFemaleMeter)
                lmcGiClamp = args.lmcConnectionModel!!.lmcGiClamp
                lmcMlcClamp = args.lmcConnectionModel!!.lmcMlcClamp
                lmcGiMfElbow = args.lmcConnectionModel!!.lmcGiMfElbow
                lmcGiFfElbow = args.lmcConnectionModel!!.lmcGiFfElbow
                lmcGi2 = args.lmcConnectionModel!!.lmcGi2
                lmcGi3 = args.lmcConnectionModel!!.lmcGi3
                lmcGi4 = args.lmcConnectionModel!!.lmcGi4
                lmcGi6 = args.lmcConnectionModel!!.lmcGi6
                lmcGi8 = args.lmcConnectionModel!!.lmcGi8
                lmcGiTee = args.lmcConnectionModel!!.lmcGiTee
                lmcMlcTee = args.lmcConnectionModel!!.lmcMlcTee
                lmcGiSocket = args.lmcConnectionModel!!.lmcGiSocket
                lmcMaleUnion = args.lmcConnectionModel!!.lmcMaleUnion
                lmcFemaleUnion = args.lmcConnectionModel!!.lmcFemaleUnion
                lmcMeterBracket = args.lmcConnectionModel!!.lmcMeterBracket
                lmcMeterSticker = args.lmcConnectionModel!!.lmcMeterSticker
                lmcPlateMarker = args.lmcConnectionModel!!.lmcPlateMarker
                lmcAdaptorGI = args.lmcConnectionModel!!.lmcAdaptorGI
                lmcAdaptorReg = args.lmcConnectionModel!!.lmcAdaptorReg
                lmcAdaptorMeter = args.lmcConnectionModel!!.lmcAdaptorMeter
                lmcFemaleMeter = args.lmcConnectionModel!!.lmcFemaleMeter

            }

            btnNext.setOnClickListener {
                if(!AppCache.isTpi)
                {

                    lmcInitialReading = etInitialMeter.text.toString()
                    lmcRegulator = etRegulatorNo.text.toString()
                    lmcGI = etGiInstallation.text.toString()
                    lmcCU = etCuInstallation.text.toString()
                    lmcNoOfAV = etAvNo.text.toString()
                    lmcNoOfIV = etIvNo.text.toString()
//                    lmcPipeLength = etPipeLength.text.toString()
                    lmcMeterType = tvMeterType.text.toString()
                    lmcExtraGiLength = etExtraGlLength.text.toString()
                    lmcExtraMlclength = etExtraMlcLength.text.toString()
                    lmcMeterId = tvMeterId.text.toString()
                    lmcGiClamp = etGiClamp.text.toString()
                    lmcMlcClamp = etMlcClamp.text.toString()
                    lmcGiMfElbow = etGiMf.text.toString()
                    lmcGiFfElbow = etGiFf.text.toString()
                    lmcGi2 = etGi2.text.toString()
                    lmcGi3 = etGi3.text.toString()
                    lmcGi4 = etGi4.text.toString()
                    lmcGi6 = etGi6.text.toString()
                    lmcGi8 = etGi8.text.toString()
                    lmcGiTee = etGiTee.text.toString()
                    lmcMlcTee = etMlcTee.text.toString()
                    lmcGiSocket = etGiSocket.text.toString()
                    lmcMaleUnion = etMlcMale.text.toString()
                    lmcFemaleUnion = etMlcFemale.text.toString()
                    lmcMeterBracket = etMeterBracket.text.toString()
                    lmcMeterSticker = etMeterSticker.text.toString()
                    lmcPlateMarker = etPlateMarker.text.toString()
                    lmcAdaptorGI = etAdaptorGi.text.toString()
                    lmcAdaptorReg = etAdaptorReg.text.toString()
                    lmcAdaptorMeter = etAdaptorMeter.text.toString()
                    lmcFemaleMeter = etFemaleUnion.text.toString()

                    if (args.lmcExecution!!.contains("without",true))
                    {
                        if (lmcType.isNullOrBlank()) {
                            spinnerRfc.error = "Select LMC Type"
                            spinnerRfc.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcGI.isNullOrBlank()) {
                            etGiInstallation.error = "Enter value"
                            etGiInstallation.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcCU.isNullOrBlank()) {
                            etCuInstallation.error = "Enter value"
                            etCuInstallation.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcNoOfAV.isNullOrBlank()) {
                            etAvNo.error = "Enter value"
                            etAvNo.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcNoOfIV.isNullOrBlank()) {
                            etIvNo.error = "Enter value"
                            etIvNo.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcGiClamp.isNullOrBlank()) {
                            etGiClamp.error = "Enter value"
                            etGiClamp.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcMlcClamp.isNullOrBlank()) {
                            etMlcClamp.error = "Enter value"
                            etMlcClamp.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcGiMfElbow.isNullOrBlank()) {
                            etGiMf.error = "Enter value"
                            etGiMf.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcGiFfElbow.isNullOrBlank()) {
                            etGiFf.error = "Enter value"
                            etGiFf.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcGi2.isNullOrBlank()) {
                            etGi2.error = "Enter value"
                            etGi2.requestFocus()
                            return@setOnClickListener
                        }
                        if (lmcGi4.isNullOrBlank()) {
                            etGi4.error = "Enter value"
                            etGi4.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcGi6.isNullOrBlank()) {
                            etGi6.error = "Enter value"
                            etGi6.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcGi8.isNullOrBlank()) {
                            etGi8.error = "Enter value"
                            etGi8.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcGiTee.isNullOrBlank()) {
                            etGiTee.error = "Enter value"
                            etGiTee.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcMlcTee.isNullOrBlank()) {
                            etMlcTee.error = "Enter value"
                            etMlcTee.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcGiSocket.isNullOrBlank()) {
                            etGiSocket.error = "Enter value"
                            etGiSocket.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcMaleUnion.isNullOrBlank()) {
                            etMlcMale.error = "Enter value"
                            etMlcMale.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcFemaleUnion.isNullOrBlank()) {
                            etMlcFemale.error = "Enter value"
                            etMlcFemale.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcPlateMarker.isNullOrBlank()) {
                            etPlateMarker.error = "Enter value"
                            etPlateMarker.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcProperty.isNullOrBlank()) {
                            spinnerProperty.error = "Select property type"
                            spinnerProperty.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcGas.isNullOrBlank()) {
                            spinnerGas.error = "Enter value"
                            spinnerGas.requestFocus()
                            return@setOnClickListener
                        }


                    }
                    else
                    {

                        if (lmcType.isNullOrBlank())
                        {
                            spinnerRfc.error = "Select LMC Type"
                            spinnerRfc.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcMeterDetail.isNullOrBlank())
                        {
                            spinnerMeter.error = "Select Meter Details"
                            spinnerMeter.requestFocus()
                            return@setOnClickListener
                        }

//                    if (lmcMeterNumber.isNullOrBlank()) {
//                        spinnerMeterNo.error = "Select Meter Number"
//                        spinnerMeterNo.requestFocus()
//                        return@setOnClickListener
//                    }

                    if (lmcMeterId.isNullOrBlank()) {
                        tvMeterId.error = "Type Meter ID"
                        tvMeterId.requestFocus()
                        return@setOnClickListener
                    }

                        if (lmcMeterType.isNullOrBlank()) {
                            tvMeterType.error = "Enter Meter Type"
                            tvMeterType.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcInitialReading.isNullOrBlank()) {
                            etInitialMeter.error = "Enter value"
                            etInitialMeter.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcRegulator.isNullOrBlank()) {
                            etRegulatorNo.error = "Enter value"
                            etRegulatorNo.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcGI.isNullOrBlank()) {
                            etGiInstallation.error = "Enter value"
                            etGiInstallation.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcCU.isNullOrBlank()) {
                            etCuInstallation.error = "Enter value"
                            etCuInstallation.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcNoOfAV.isNullOrBlank()) {
                            etAvNo.error = "Enter value"
                            etAvNo.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcNoOfIV.isNullOrBlank()) {
                            etIvNo.error = "Enter value"
                            etIvNo.requestFocus()
                            return@setOnClickListener
                        }

//                    if (lmcPipeLength.isNullOrBlank()) {
//                        etPipeLength.error = "Enter value"
//                        etPipeLength.requestFocus()
//                        return@setOnClickListener
//                    }

                        if (lmcGiClamp.isNullOrBlank()) {
                            etGiClamp.error = "Enter value"
                            etGiClamp.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcMlcClamp.isNullOrBlank()) {
                            etMlcClamp.error = "Enter value"
                            etMlcClamp.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcGiMfElbow.isNullOrBlank()) {
                            etGiMf.error = "Enter value"
                            etGiMf.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcGiFfElbow.isNullOrBlank()) {
                            etGiFf.error = "Enter value"
                            etGiFf.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcGi2.isNullOrBlank()) {
                            etGi2.error = "Enter value"
                            etGi2.requestFocus()
                            return@setOnClickListener
                        }
                        if (lmcGi4.isNullOrBlank()) {
                            etGi4.error = "Enter value"
                            etGi4.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcGi6.isNullOrBlank()) {
                            etGi6.error = "Enter value"
                            etGi6.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcGi8.isNullOrBlank()) {
                            etGi8.error = "Enter value"
                            etGi8.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcGiTee.isNullOrBlank()) {
                            etGiTee.error = "Enter value"
                            etGiTee.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcMlcTee.isNullOrBlank()) {
                            etMlcTee.error = "Enter value"
                            etMlcTee.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcGiSocket.isNullOrBlank()) {
                            etGiSocket.error = "Enter value"
                            etGiSocket.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcMaleUnion.isNullOrBlank()) {
                            etMlcMale.error = "Enter value"
                            etMlcMale.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcFemaleUnion.isNullOrBlank()) {
                            etMlcFemale.error = "Enter value"
                            etMlcFemale.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcMeterBracket.isNullOrBlank()) {
                            etMeterBracket.error = "Enter value"
                            etMeterBracket.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcMeterSticker.isNullOrBlank()) {
                            etMeterSticker.error = "Enter value"
                            etMeterSticker.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcPlateMarker.isNullOrBlank()) {
                            etPlateMarker.error = "Enter value"
                            etPlateMarker.requestFocus()
                            return@setOnClickListener
                        }
                        if (lmcAdaptorGI.isNullOrBlank()) {
                            etAdaptorGi.error = "Enter value"
                            etAdaptorGi.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcAdaptorReg.isNullOrBlank()) {
                            etAdaptorReg.error = "Enter value"
                            etAdaptorReg.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcAdaptorMeter.isNullOrBlank()) {
                            etAdaptorMeter.error = "Enter value"
                            etAdaptorMeter.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcFemaleMeter.isNullOrBlank()) {
                            etFemaleUnion.error = "Enter value"
                            etFemaleUnion.requestFocus()
                            return@setOnClickListener
                        }


                        if (lmcProperty.isNullOrBlank()) {
                            spinnerProperty.error = "Select property type"
                            spinnerProperty.requestFocus()
                            return@setOnClickListener
                        }

                        if (lmcGas.isNullOrBlank()) {
                            spinnerGas.error = "Enter value"
                            spinnerGas.requestFocus()
                            return@setOnClickListener
                        }
                    }

                    val timeStamp: String = SimpleDateFormat("ddMMyyyy_HHmmss").format(Date())

                    val statusTypeId = AppCache.tpiStatusMap[args.lmcStatusType].toString()
                    val subStatusId = AppCache.tpiSubStatusMap[args.lmcSubStatus].toString()

                    val params = HashMap<String, String>()
                    params["application_number"] = args.appNo
                    params["bp_number"] = args.bpNo
                    params["customer_info"] = args.customerInfo
                    params["status_type_id"] = statusTypeId
                    params["status_type"] = args.lmcStatusType.toString()
                    params["sub_status_id"] = subStatusId
                    params["sub_status"] = args.lmcSubStatus.toString()
                    params["tpi_id"] = args.tpiId
                    params["follow_up_date"] = args.followUpDate.toString()
                    params["description"] = args.description.toString()
                    params["lmc_type"] = lmcType.toString()
                    params["meter_details"] = lmcMeterDetail.toString()
                    params["meter_no"] = lmcMeterNumber.toString()
                    params["meter_sno"] = lmcMeterId.toString() ?: ""
                    params["meter_type"] = lmcMeterType.toString()
                    params["initial_meter_reading"] = lmcInitialReading.toString()
                    params["regulator_number"] = lmcRegulator.toString()
                    params["GI_install_meter"] = lmcGI.toString()
                    params["CU_install_meter"] = lmcCU.toString()
                    params["no_of_av"] = lmcNoOfAV.toString()
                    params["no_of_iv"] = lmcNoOfIV.toString()
                    params["extra_pipe_length"] = lmcPipeLength.toString()
                    params["property_type"] = lmcProperty.toString()
                    params["gas_type"] = lmcGas.toString()
                    params["extra_gi_length"] = lmcExtraGiLength.toString()
                    params["extra_mlc_length"] = lmcExtraMlclength.toString()
                    params["lmc_execution"] = args.lmcExecution.toString()
                    params["gi_clamp"] = lmcGiClamp.toString()
                    params["mlc_clamp"] = lmcMlcClamp.toString()
                    params["gi_MF_elbow"] = lmcGiMfElbow.toString()
                    params["gi_FF_elbow"] = lmcGiFfElbow.toString()
                    params["gi_2_nipple"] = lmcGi2.toString()
                    params["gi_3_nipple"] = lmcGi3.toString()
                    params["gi_4_nipple"] = lmcGi4.toString()
                    params["gi_6_nipple"] = lmcGi6.toString()
                    params["gi_8_nipple"] = lmcGi8.toString()
                    params["gi_tee"] = lmcGiTee.toString()
                    params["mlc_tee"] = lmcMlcTee.toString()
                    params["gi_socket"] = lmcGiSocket.toString()
                    params["mlc_male_union"] = lmcMaleUnion.toString()
                    params["mlc_female_union"] = lmcFemaleUnion.toString()
                    params["meter_bracket"] = lmcMeterBracket.toString()
                    params["meter_sticker"] = lmcMeterSticker.toString()
                    params["plate_marker"] = lmcPlateMarker.toString()
                    params["adaptor_GI_to_reg"] = lmcAdaptorGI.toString()
                    params["adaptor_reg_to_meter"] = lmcAdaptorReg.toString()
                    params["adaptor_meter_to_GI_pipe"] = lmcAdaptorMeter.toString()
                    params["female_union_meter_MLC_pipe"] = lmcFemaleMeter.toString()
                    params["approval_status"] = "Nil"
                    params["comments"] = ""
                    params["lmc_session_id"]=args.sessionId
                    params["pvc_sleeve"]=args.pvcSleeve ?: "-1"
                    params["meter_installation"]=args.meterInstallation ?: "-1"
                    params["clamping"]=args.clamping ?: "-1"
                    params["gas_meter_testing"]=args.gmTesting ?: "-1"
                    params["cementing_of_holes"]=args.coh ?: "-1"
                    params["painting_of_GI_pipe"]=args.painting ?: "-1"
                    params["TF_avail"]=args.tfAvail ?: "-1"
                    params["connectivity"]=args.connectivity ?: "-1"
                    params["enc_cap"]=args.endCap ?: "-1"
                    params["area_gassified"]=args.areagassified ?: "-1"
                    params["cust_sat_ready_to_get_status"]=args.custStatus ?: "-1"
                    params["ng_conv_date_status"]=args.convStatus ?: "-1"
                    params["lmc_acknowledge_status"]=args.confirmStatus ?: "-1"
                    params["lmc_session_id"]=args.sessionId
                    params["lmc_created_date_time"] = timeStamp
                    params["hole_drilled"] = args.holeDrilled ?: "-1"
                    params["mcv_testing"] =  args.mcvTesting ?: "-1"
                    params["corrosion_tape"] = args.acTape ?: "-1"
                    viewModel.submitLmc(params)
                    setupObserver()

                }
                else
                {
                    val directions =
                        LmcConnectionFragmentDirections.actionLmcConnectionFragmentToLmcFinalFragment(
                            appNo = args.appNo,
                            bpNo = args.bpNo,
                            tpiId = args.tpiId,
                            customerInfo = args.customerInfo,
                            statusType = args.lmcStatusType,
                            subStatus = args.lmcSubStatus,
                            followUpDate = args.followUpDate,
                            description = args.description,
                            lmcType = lmcType.toString(),
                            meterDetails = lmcMeterDetail.toString(),
                            meterNo = lmcMeterNumber.toString(),
                            meterType = lmcMeterType.toString(),
                            initialReading = lmcInitialReading.toString(),
                            regulatorNo = lmcRegulator.toString(),
                            giMeter = lmcGI.toString(),
                            cuMeter = lmcCU.toString(),
                            avNo = lmcNoOfAV.toString(),
                            pipeLength = lmcPipeLength.toString(),
                            propertyType = lmcProperty.toString(),
                            gasType = lmcGas.toString(),
                            sessionId = args.sessionId,
                            ivNo = lmcNoOfIV.toString(),
                            meterSerialNo = lmcMeterId.toString() ?: "",
                            status = args.status,
                            pvcSleeve = args.pvcSleeve,
                            meterInstallation = args.meterInstallation,
                            gmTesting = args.gmTesting,
                            coh = args.coh,
                            clamping = args.clamping,
                            painting = args.painting,
                            tfAvail = args.tfAvail,
                            connectivity = args.connectivity,
                            endCap = args.endCap,
                            areagassified = args.areagassified,
                            holeDrilled = args.holeDrilled,
                            mcvTesting = args.mcvTesting,
                            custStatus = args.custStatus,
                            convStatus = args.convStatus,
                            confirmStatus = args.confirmStatus,
                            extraGiLength = lmcExtraGiLength,
                            extraMlLength = lmcExtraMlclength, acTape = args.acTape,
                            lmcConnectionModel = args.lmcConnectionModel,
                            lmcExecution = args.lmcExecution,

                            )
                    findNavController().navigate(directions)
                }
            }

            spinnerRfc.setOnClickListener {
                lmcTypeSpinner?.showSpinerDialog()
                lmcTypeSpinner?.setShowKeyboard(false)
                lmcTypeSpinner?.setShowKeyboard(false)
            }

            spinnerMeter.setOnClickListener {
                lmcMeterDetailSpinner?.showSpinerDialog()
                lmcMeterDetailSpinner?.setShowKeyboard(false)
                lmcMeterDetailSpinner?.setShowKeyboard(false)
            }

//            spinnerMeterNo.setOnClickListener {
//                lmcMeterSpinner?.showSpinerDialog()
//                lmcMeterSpinner?.setShowKeyboard(false)
//                lmcMeterSpinner?.setShowKeyboard(false)
//            }

            spinnerProperty.setOnClickListener {
                lmcPropertySpinner?.showSpinerDialog()
                lmcPropertySpinner?.setShowKeyboard(false)
                lmcPropertySpinner?.setShowKeyboard(false)
            }

            spinnerGas.setOnClickListener {
                lmcGasSpinner?.showSpinerDialog()
                lmcGasSpinner?.setShowKeyboard(false)
                lmcGasSpinner?.setShowKeyboard(false)
            }

            tvMeterId.isEnabled = false
            ivQr.setOnClickListener {
                requestCameraPermission.launch(
                    Manifest.permission.CAMERA
                )
            }

            if(qrValue!=null){
                lmcMeterId = qrValue
                tvMeterId.setText(qrValue)
            }
            if(qrError!=null){
                Toast.makeText(requireContext(), qrError, Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if(qrValue!=null){
            lmcMeterId = qrValue
            binding.tvMeterId.setText(qrValue)
        }
    }

    private fun setupObserver() {
        viewModel.submitResponse.observe(viewLifecycleOwner) {
            if (it.data != null) {
                when (it.status) {
                    Status.LOADING -> {
                        setDialog(true)
                    }
                    Status.SUCCESS -> {
                        setDialog(false)
                        if(!it.data.error){
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                            if(args.status == "hold" || args.status == "done"){

                                val directions =
                                    LmcConnectionFragmentDirections.actionLmcConnectionFragmentToLmcFinalFragment(
                                        appNo = args.appNo,
                                        bpNo = args.bpNo,
                                        tpiId = args.tpiId,
                                        customerInfo = args.customerInfo,
                                        statusType = args.lmcStatusType,
                                        subStatus = args.lmcSubStatus,
                                        followUpDate = args.followUpDate,
                                        description = args.description,
                                        lmcType = lmcType.toString(),
                                        meterDetails = lmcMeterDetail.toString(),
                                        meterNo = lmcMeterNumber.toString(),
                                        meterType = lmcMeterType.toString(),
                                        initialReading = lmcInitialReading.toString(),
                                        regulatorNo = lmcRegulator.toString(),
                                        giMeter = lmcGI.toString(),
                                        cuMeter = lmcCU.toString(),
                                        avNo = lmcNoOfAV.toString(),
                                        pipeLength = lmcPipeLength.toString(),
                                        propertyType = lmcProperty.toString(),
                                        gasType = lmcGas.toString(),
                                        sessionId = args.sessionId,
                                        ivNo = lmcNoOfIV.toString(),
                                        meterSerialNo = lmcMeterId.toString(),
                                        status = args.status,
                                        pvcSleeve = args.pvcSleeve,
                                        meterInstallation = args.meterInstallation,
                                        gmTesting = args.gmTesting,
                                        coh = args.coh,
                                        clamping = args.clamping,
                                        painting = args.painting,
                                        tfAvail = args.tfAvail,
                                        connectivity = args.connectivity,
                                        endCap = args.endCap,
                                        areagassified = args.areagassified,
                                        holeDrilled = args.holeDrilled,
                                        mcvTesting = args.mcvTesting,
                                        custStatus = args.custStatus,
                                        convStatus = args.convStatus,
                                        confirmStatus = args.confirmStatus,
                                        extraGiLength = lmcExtraGiLength,
                                        extraMlLength = lmcExtraMlclength, acTape = args.acTape,
                                        lmcExecution = args.lmcExecution,
                                        lmcConnectionModel = LmcConnectionModel(
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
                                            lmcFemaleMeter = lmcFemaleMeter

                                        )
                                    )
                                findNavController().navigate(directions)
                                return@observe
                            }else{
                                val directions =
                                    LmcConnectionFragmentDirections.actionLmcConnectionFragmentToLmcFinalFragment(
                                        appNo = args.appNo,
                                        bpNo = args.bpNo,
                                        tpiId = args.tpiId,
                                        customerInfo = args.customerInfo,
                                        statusType = args.lmcStatusType,
                                        subStatus = args.lmcSubStatus,
                                        followUpDate = args.followUpDate,
                                        description = args.description,
                                        lmcType = lmcType.toString(),
                                        meterDetails = lmcMeterDetail.toString(),
                                        meterNo = lmcMeterNumber.toString(),
                                        meterType = lmcMeterType.toString(),
                                        initialReading = lmcInitialReading.toString(),
                                        regulatorNo = lmcRegulator.toString(),
                                        giMeter = lmcGI.toString(),
                                        cuMeter = lmcCU.toString(),
                                        avNo = lmcNoOfAV.toString(),
                                        pipeLength = lmcPipeLength.toString(),
                                        propertyType = lmcProperty.toString(),
                                        gasType = lmcGas.toString(),
                                        sessionId = args.sessionId,
                                        ivNo = lmcNoOfIV.toString(),
                                        meterSerialNo = lmcMeterId.toString(),
                                        extraGiLength = lmcExtraGiLength.toString(),
                                        extraMlLength = lmcExtraMlclength.toString(),
                                        lmcExecution = args.lmcExecution,
                                        lmcConnectionModel = LmcConnectionModel(
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
                                            lmcFemaleMeter = lmcFemaleMeter

                                        )
                                    )
                                findNavController().navigate(directions)
                                return@observe
                            }
                        }else{
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                        }

                    }
                    Status.ERROR -> {
                        setDialog(false)
                    }
                }
            }
        }
    }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                Toast.makeText(
                    requireContext(),
                    "Camera permission is needed to scan code",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val directions = LmcConnectionFragmentDirections.actionLmcConnectionFragmentToScannerFragment()
                findNavController().navigate(directions)
            }

        }

    private fun setDialog(show: Boolean) {
        if (show) dialog!!.show() else dialog!!.dismiss()
    }


}