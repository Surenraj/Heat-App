package com.thinkgas.heatapp.ui.registration

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.thinkgas.heatapp.BuildConfig
import com.thinkgas.heatapp.R
import com.thinkgas.heatapp.data.cache.AppCache
import com.thinkgas.heatapp.data.remote.model.Attachment
import com.thinkgas.heatapp.data.remote.model.GcUploadRequestModel
import com.thinkgas.heatapp.data.remote.model.LegalEntity
import com.thinkgas.heatapp.data.remote.model.SubState
import com.thinkgas.heatapp.databinding.FragmentRegistrationBinding
import com.thinkgas.heatapp.databinding.LayoutViewImageBinding
import com.thinkgas.heatapp.ui.common.adapters.ViewAttachmentAdapter
import com.thinkgas.heatapp.utils.AppUtils
import com.thinkgas.heatapp.utils.Constants
import com.thinkgas.heatapp.utils.Status
import com.thinkgas.heatapp.utils.getFileName
import com.thinkgas.heatapp.utils.getFileSize
import dagger.hilt.android.AndroidEntryPoint
import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private var _binding:FragmentRegistrationBinding? = null
    val binding get() = _binding!!
    private val args by navArgs<RegistrationFragmentArgs>()
    private val viewModel by viewModels<RegistrationViewModel>()
    private var dialog: Dialog? = null
    private var tpiFloorMap: MutableMap<String, Int> = mutableMapOf()
    private var tpiGassificationMap: MutableMap<String, Int> = mutableMapOf()
    var floorSpinnerDialog: SpinnerDialog? = null
    var gassifiedSpinnerDialog: SpinnerDialog? = null
    var gaSpinnerDialog: SpinnerDialog? = null
    var zonalSpinnerDialog: SpinnerDialog? = null
    var chargeAreaSpinnerDialog: SpinnerDialog? = null
    var colonySpinnerDialog: SpinnerDialog? = null
    var districtSpinnerDialog: SpinnerDialog? = null
    var talukaSpinnerDialog: SpinnerDialog? = null
    var citySpinnerDialog: SpinnerDialog? = null
    var areaSpinnerDialog: SpinnerDialog? = null
    var pincodeSpinnerDialog: SpinnerDialog? = null
    var fSpinnerDialog: SpinnerDialog? = null
    private var tpiMap: MutableMap<String, List<SubState>> = mutableMapOf()
    private var tpiStatusMap: MutableMap<String, Int> = mutableMapOf()
    private var tpiSubStatus: MutableMap<String, Int> = mutableMapOf()
    private var tpiPotentialMap: MutableMap<String, Int> = mutableMapOf()
    private var tpiSubStatusMap: MutableMap<String, Int> = mutableMapOf()
    private var tfStatus: MutableMap<Int, String> = mutableMapOf()
    var statusSpinnerDialog: SpinnerDialog? = null
    var subStatusSpinner: SpinnerDialog? = null
    var potentialSpinnerDialog: SpinnerDialog? = null
//    private var gcPotential: String? = null
//    private
//    private var gcStatus: String? = null
//    private var gcStatusCode: String? = null
//    private var gcDate: String? = null


    private var hasConsent = false
    private var isConsentRadioTapped = false
    private var hasWarning = false
    private var isWarningPlateRadioTapped = false
    private var mobile : String? = null

    private  var consentAdapter: ViewAttachmentAdapter? = null
    private  var unRegAdapter: ViewAttachmentAdapter? = null
    private  var warningAdapter: ViewAttachmentAdapter? = null
    private var imageDialog: Dialog? = null
    private var imageLayout: LayoutViewImageBinding? = null
    private var imageType: GcUnregType? = null

    private lateinit var cameraActions: ActivityResultLauncher<Intent>
    private var photoURI: Uri? = null

    private var consentCount = 0
    private var unregCount = 0
    private var warningCount = 0

    var day = 0
    var month: Int = 0
    var year: Int = 0

    enum class GcUnregType{
        CONSENT,
        WARNING,
        ATTACHMENT
    }

    companion object{
        var gaName:String? =null
        var zonalName:String? =null
        var caName:String? =null
        var colonyName:String? = null
        var districtName:String? = null
        var talukaName:String? = null
        var cityName:String? = null
        var areaName:String? = null
        var pincode:String? = null
        var stateName:String? = null
        var gaId:String? =null
        var zonalId:String? =null
        var caId:String? =null
        var colonyId:String? = null
        var districtId:String? = null
        var talukaId:String? = null
        var cityId:String? = null
        var pincodeId:String? = null
        var stateId:String? = null
        var areaId:String? = null
        var floorfacing:String? = null
        var gassification:String? = null
        var floorNo:String? = null
        var legalEntity = mutableListOf<LegalEntity>()
         var gcStatus: String? = null
         var gcStatusCode: String? = null
         var gcDate: String? = null
        var gcPotential: String? = null
        var isFailed = false

        var unregFlag = false
        var warningFlag = false
        var consentFlag = false

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = activity?.getSharedPreferences("TPI_PREFS", Context.MODE_PRIVATE)
        mobile = sharedPreferences?.getString("mobile", null)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater,container,false)

        binding.apply {

            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setView(R.layout.progress)
            dialog = builder.create()



            if(stateName!=null)
            {
                spinnerFloor.text = floorfacing
                spinnerGassification.text = gassification
                spinnerGeoArea.text = gaName
                spinnerZonal.text = zonalName
                spinnerChargeArea.text = caName
                spinnerColony.text = colonyName
                spinnerDistrict.text = districtName
                spinnerTaluka.text = talukaName
                spinnerCity.text = cityName
                spinnerArea.text = areaName
                spinnerPincode.text = pincode
                etState.text = stateName
                etFloor.text = floorNo
                spinnerGcStatus.text = gcStatus
                spinnerPotential.text = gcPotential
                spinnerDate.text = gcDate

            }

            if(args.gcUnregModel  != null){
//                spinnerFloor.text = floorfacing
//                spinnerGassification.text = gassification
//                spinnerGeoArea.text = gaName
//                spinnerZonal.text = zonalName
//                spinnerChargeArea.text = caName
//                spinnerColony.text = colonyName
//                spinnerDistrict.text = districtName
//                spinnerTaluka.text = talukaName
//                spinnerCity.text = cityName
//                spinnerArea.text = areaName
//                spinnerPincode.text = pincode
//                etState.text = stateName
//                etFloor.text = floorNo
                spinnerGcStatus.text = args.gcUnregModel!!.statusType
                spinnerPotential.text = args.gcUnregModel!!.potential
                spinnerDate.text = args.gcUnregModel!!.gcDate

                if(args.gcUnregModel!!.consentTaken == "0") {
                    rgConsent.check(rgConsent.getChildAt(1).id)
                    tvConsentAttachment.visibility = View.GONE
                    ivConsent.visibility = View.GONE
                    rvConsent.visibility = View.GONE
                    hasConsent = false

                } else {
                    rgConsent.check(rgConsent.getChildAt(0).id)
                    tvConsentAttachment.visibility = View.VISIBLE
                    ivConsent.visibility = View.VISIBLE
                    rvConsent.visibility = View.VISIBLE
                    hasConsent = true
                }

                if(args.gcUnregModel!!.warningAvailable == "0") {
                    rgWarning.check(rgWarning.getChildAt(1).id)
                    tvWarningForm.visibility = View.GONE
                    ivWarning.visibility = View.GONE
                    rvWarning.visibility = View.GONE

                    hasWarning = false
                } else {
                    rgWarning.check(rgWarning.getChildAt(0).id)

                    tvWarningForm.visibility = View.VISIBLE
                    ivWarning.visibility = View.VISIBLE
                    rvWarning.visibility = View.VISIBLE

                    hasWarning = true

                }

            }


            spinnerGcStatus.setOnClickListener {
                statusSpinnerDialog?.showSpinerDialog()
                statusSpinnerDialog?.setCancellable(false)
                statusSpinnerDialog?.setShowKeyboard(false)
            }

            spinnerPotential.setOnClickListener {
                potentialSpinnerDialog?.showSpinerDialog()
                potentialSpinnerDialog?.setCancellable(false)
                potentialSpinnerDialog?.setShowKeyboard(false)
            }

            rgConsent.setOnCheckedChangeListener { radioGroup, i ->
//                hasConsent = if(rbConsentYes.id == i) true else false
                isConsentRadioTapped = true
                if(rbConsentYes.id == i) {
                    tvConsentAttachment.visibility = View.VISIBLE
                    ivConsent.visibility = View.VISIBLE
                    rvConsent.visibility = View.VISIBLE
                    hasConsent = true
                } else {
                    tvConsentAttachment.visibility = View.GONE
                    ivConsent.visibility = View.GONE
                    rvConsent.visibility = View.GONE
                    hasConsent = false

                }
            }

            rgWarning.setOnCheckedChangeListener { radioGroup, i ->
//                hasWarning = if(rbWarningYes.id == i) true else false
                isWarningPlateRadioTapped = true

                if(rbWarningYes.id == i) {
                    tvWarningForm.visibility = View.VISIBLE
                    ivWarning.visibility = View.VISIBLE
                    rvWarning.visibility = View.VISIBLE
                    hasWarning = true
                } else {
                    tvWarningForm.visibility = View.GONE
                    ivWarning.visibility = View.GONE
                    rvWarning.visibility = View.GONE
                    hasWarning = false
                }

            }





            tvLocation.setOnClickListener {
                val directions = RegistrationFragmentDirections.actionRegistrationFragmentToMapFragment2(
                    latitude = AppCache.latitude.toString(), longitude = AppCache.longitude.toString(),
                    type = "GC",

                )
                findNavController().navigate(directions)
            }

            spinnerDate.setOnClickListener {
                val calendar: Calendar = Calendar.getInstance()
                day = calendar.get(Calendar.DAY_OF_MONTH)
                month = calendar.get(Calendar.MONTH)
                year = calendar.get(Calendar.YEAR)
                val datePickerDialog =
                    DatePickerDialog(requireContext(), { datePicker, year, month, day ->
                        gcDate = AppUtils.getDate("$day-${month+1}-$year")

                        spinnerDate.text = gcDate
                        spinnerDate.error = null
                    }, year, month,day)
                datePickerDialog.datePicker.maxDate = Date().time
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
                    val directions = RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment()
                    findNavController().navigate(directions)

                }
                logoutBuilder.setNegativeButton("No") { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                val logoutAlert = logoutBuilder.create()
                logoutAlert.show()

            }

            ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
            val params = HashMap<String, String>()
            val versionCode = BuildConfig.VERSION_CODE


            params["version_code"] = versionCode.toString()
            params["os_type"] = "android"
            params["mobile_no"] = mobile.toString()

            viewModel.getTpiListTypes(params)

            setUpObserver()

            imageLayout = LayoutViewImageBinding.inflate(LayoutInflater.from(requireActivity()))
            imageDialog = Dialog(requireActivity(), R.style.list_dialog_style)
            imageDialog!!.setContentView(imageLayout!!.root)

            ivConsent.setOnClickListener {
                if(etMobile.text.isNullOrBlank()){
                    etMobile.error = "Enter Mobile Number first"
                    etMobile.requestFocus()
                    return@setOnClickListener
                }
                imageType = GcUnregType.CONSENT
                requestCameraPermission.launch(
                    Manifest.permission.CAMERA
                )
            }



            ivWarning.setOnClickListener {

                if(etMobile.text.isNullOrBlank()){
                    etMobile.error = "Enter Mobile Number first"
                    etMobile.requestFocus()
                    return@setOnClickListener
                }
                imageType = GcUnregType.WARNING
                requestCameraPermission.launch(
                    Manifest.permission.CAMERA
                )
            }


            ivUnregistered.setOnClickListener {
                if(etMobile.text.isNullOrBlank()){
                    etMobile.error = "Enter Mobile Number first"
                    etMobile.requestFocus()
                    return@setOnClickListener
                }
                imageType = GcUnregType.ATTACHMENT
                requestCameraPermission.launch(
                    Manifest.permission.CAMERA
                )

            }

            if (args.gcUnregModel != null) {
                spinnerDate.text = args.gcUnregModel!!.gcDate
                spinnerGcStatus.text = args.gcUnregModel!!.statusType
                spinnerPotential.text = args.gcUnregModel!!.potential
            }

            spinnerFloor.setOnClickListener {
                floorSpinnerDialog?.showSpinerDialog()
                floorSpinnerDialog?.setCancellable(false)
                floorSpinnerDialog?.setShowKeyboard(false)
            }

            etFloor.setOnClickListener {
                fSpinnerDialog?.showSpinerDialog()
                fSpinnerDialog?.setCancellable(false)
                fSpinnerDialog?.setShowKeyboard(false)
            }


            spinnerGassification.setOnClickListener {
                gassifiedSpinnerDialog?.showSpinerDialog()
                gassifiedSpinnerDialog?.setCancellable(false)
                gassifiedSpinnerDialog?.setShowKeyboard(false)
            }

            spinnerGeoArea.setOnClickListener {
                gaSpinnerDialog?.showSpinerDialog()
                gaSpinnerDialog?.setCancellable(false)
                gaSpinnerDialog?.setShowKeyboard(false)
            }

            spinnerZonal.setOnClickListener {

                if(gaName == null){
                    Toast.makeText(
                        requireContext(),
                        "Please select ga first",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                zonalSpinnerDialog?.showSpinerDialog()
                zonalSpinnerDialog?.setCancellable(false)
                zonalSpinnerDialog?.setShowKeyboard(false)
            }

            spinnerChargeArea.setOnClickListener {

                if(gaName == null){
                    Toast.makeText(
                        requireContext(),
                        "Please select ga first",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if(zonalName == null){
                    Toast.makeText(
                        requireContext(),
                        "Please select zonal first",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                chargeAreaSpinnerDialog?.showSpinerDialog()
                chargeAreaSpinnerDialog?.setCancellable(false)
                chargeAreaSpinnerDialog?.setShowKeyboard(false)
            }

            spinnerColony.setOnClickListener {

                if(gaName == null){
                    Toast.makeText(
                        requireContext(),
                        "Please select ga first",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if(zonalName == null){
                    Toast.makeText(
                        requireContext(),
                        "Please select zonal first",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if(caName == null){
                    Toast.makeText(
                        requireContext(),
                        "Please select zonal first",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                colonySpinnerDialog?.showSpinerDialog()
                colonySpinnerDialog?.setCancellable(false)
                colonySpinnerDialog?.setShowKeyboard(false)
            }

            spinnerDistrict.setOnClickListener {

                if(gaName == null){
                    Toast.makeText(
                        requireContext(),
                        "Please select ga first",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

//                if(zonalName == null){
//                    Toast.makeText(
//                        requireContext(),
//                        "Please select zonal first",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    return@setOnClickListener
//                }
//
//                if(caName == null){
//                    Toast.makeText(
//                        requireContext(),
//                        "Please select Chargearea first",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    return@setOnClickListener
//                }
//
//                if(colonyName == null){
//                    Toast.makeText(
//                        requireContext(),
//                        "Please select colony first",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    return@setOnClickListener
//                }
                districtSpinnerDialog?.showSpinerDialog()
                districtSpinnerDialog?.setCancellable(false)
                districtSpinnerDialog?.setShowKeyboard(false)
            }

            spinnerTaluka.setOnClickListener {

                if(gaName == null){
                    Toast.makeText(
                        requireContext(),
                        "Please select ga first",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

//                if(zonalName == null){
//                    Toast.makeText(
//                        requireContext(),
//                        "Please select zonal first",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    return@setOnClickListener
//                }
//
//                if(caName == null){
//                    Toast.makeText(
//                        requireContext(),
//                        "Please select Chargearea first",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    return@setOnClickListener
//                }
//
//                if(colonyName == null){
//                    Toast.makeText(
//                        requireContext(),
//                        "Please select colony first",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    return@setOnClickListener
//                }

                if(districtName == null){
                    Toast.makeText(
                        requireContext(),
                        "Please select district first",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                talukaSpinnerDialog?.showSpinerDialog()
                talukaSpinnerDialog?.setCancellable(false)
                talukaSpinnerDialog?.setShowKeyboard(false)
            }

            spinnerCity.setOnClickListener {

                if(gaName == null){
                    Toast.makeText(
                        requireContext(),
                        "Please select ga first",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if(districtName == null){
                    Toast.makeText(
                        requireContext(),
                        "Please select district first",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if(talukaName == null){
                    Toast.makeText(
                        requireContext(),
                        "Please select taluka first",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                citySpinnerDialog?.showSpinerDialog()
                citySpinnerDialog?.setCancellable(false)
                citySpinnerDialog?.setShowKeyboard(false)
            }

            spinnerArea.setOnClickListener {

                if(gaName == null){
                    Toast.makeText(
                        requireContext(),
                        "Please select ga first",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if(districtName == null){
                    Toast.makeText(
                        requireContext(),
                        "Please select district first",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if(talukaName == null){
                    Toast.makeText(
                        requireContext(),
                        "Please select taluka first",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if(cityName == null){
                    Toast.makeText(
                        requireContext(),
                        "Please select city first",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                areaSpinnerDialog?.showSpinerDialog()
                areaSpinnerDialog?.setCancellable(false)
                areaSpinnerDialog?.setShowKeyboard(false)
            }

            spinnerPincode.setOnClickListener {

                if(gaName == null){
                    Toast.makeText(
                        requireContext(),
                        "Please select ga first",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if(districtName == null){
                    Toast.makeText(
                        requireContext(),
                        "Please select district first",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if(talukaName == null){
                    Toast.makeText(
                        requireContext(),
                        "Please select taluka first",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if(cityName == null){
                    Toast.makeText(
                        requireContext(),
                        "Please select city first",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if(areaName == null){
                    Toast.makeText(
                        requireContext(),
                        "Please select area first",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                pincodeSpinnerDialog?.showSpinerDialog()
                pincodeSpinnerDialog?.setCancellable(false)
                pincodeSpinnerDialog?.setShowKeyboard(false)
            }

             val downDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_down_24)
            val upDrawable =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_up_24)

             tvUnregistered.setOnClickListener {
                if (!unregFlag) {
                    rvUnregistred.visibility = View.GONE
                    tvUnregistered.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        downDrawable,
                        null
                    )
                    unregFlag = !unregFlag
                } else {
                    rvUnregistred.visibility = View.VISIBLE
                    tvUnregistered.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        upDrawable,
                        null
                    )
                    unregFlag = !unregFlag
                }
            }

            tvConsentAttachment.setOnClickListener {
                if (!consentFlag) {
                    rvConsent.visibility = View.GONE
                    tvConsentAttachment.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        downDrawable,
                        null
                    )
                    consentFlag = !consentFlag
                } else {
                    rvConsent.visibility = View.VISIBLE
                    tvConsentAttachment.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        upDrawable,
                        null
                    )
                    consentFlag = !consentFlag
                }
            }

            tvWarningForm.setOnClickListener {
                if (!warningFlag) {
                    rvWarning.visibility = View.GONE
                    tvWarningForm.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        downDrawable,
                        null
                    )
                    warningFlag = !warningFlag
                } else {
                    rvWarning.visibility = View.VISIBLE
                    tvWarningForm.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        upDrawable,
                        null
                    )
                    warningFlag = !warningFlag
                }
            }

            getAttachmentList()
            attachmentObserver()

            btnSubmit.setOnClickListener {

                if(etCustomerName.text.isNullOrBlank()){
                    etCustomerName.error = "Enter Customer Name"
                    etCustomerName.requestFocus()
                    return@setOnClickListener
                }

                if(etMobile.text.isNullOrBlank()){
                    etMobile.error = "Enter Mobile Number"
                    etMobile.requestFocus()
                    return@setOnClickListener
                }

//                if(etAddress.text.isNullOrBlank()){
//                    etAddress.error = "Enter address"
//                    etAddress.requestFocus()
//                    return@setOnClickListener
//                }

                if(etTower.text.isNullOrBlank()){
                    etTower.error = "Enter tower number"
                    etTower.requestFocus()
                    return@setOnClickListener
                }

                if(etDoor.text.isNullOrBlank()){
                    etDoor.error = "Enter door number"
                    etDoor.requestFocus()
                    return@setOnClickListener
                }

                if(etFloor.text.isNullOrBlank() || etFloor.text == "Select Floor"){
                    etFloor.error = "Enter Floor number"
                    etFloor.requestFocus()
                    return@setOnClickListener
                }

                if(spinnerFloor.text.isNullOrBlank() || spinnerFloor.text.equals("Select Type")){
                    spinnerFloor.error = "Select Floor facing"
                    spinnerFloor.requestFocus()
                    return@setOnClickListener
                }

                if(spinnerGassification.text.isNullOrBlank() || spinnerGassification.text.equals("Select Type")){
                    spinnerGassification.error = "Select gasification"
                    spinnerGassification.requestFocus()
                    return@setOnClickListener
                }

                if(gaName.isNullOrBlank() || spinnerGeoArea.text.equals("Select GA")){
                    spinnerGeoArea.error = "Select GA"
                    spinnerGeoArea.requestFocus()
                    return@setOnClickListener
                }
///// com.google.gson.JsonSyntaxException: java.lang.IllegalStateException: Expected a
// string but was BEGIN_ARRAY at line 1 column 12583 path
// $.legal_entity_list[0].ga_id_list[0].Zonal[1].charge_area_list[0].colony_data_list
                if(zonalName.isNullOrBlank() || spinnerZonal.text.equals("Select Zone")){
                    spinnerZonal.error = "Select Zone"
                    spinnerZonal.requestFocus()
                    return@setOnClickListener
                }

                if(caName.isNullOrBlank() || spinnerChargeArea.text.equals("Select Chargearea")){
                    spinnerChargeArea.error = "Select Chargearea"
                    spinnerChargeArea.requestFocus()
                    return@setOnClickListener
                }

                if(colonyName.isNullOrBlank() || spinnerColony.text.equals("Select Colony")){
                    spinnerColony.error = "Select Colony"
                    spinnerColony.requestFocus()
                    return@setOnClickListener
                }

                if(districtName.isNullOrBlank() || spinnerDistrict.text.equals("Select District")){
                    spinnerDistrict.error = "Select District"
                    spinnerDistrict.requestFocus()
                    return@setOnClickListener
                }

                if(talukaName.isNullOrBlank() || spinnerTaluka.text.equals("Select Taluka")){
                    spinnerTaluka.error = "Select Taluka"
                    spinnerTaluka.requestFocus()
                    return@setOnClickListener
                }

                if(cityName.isNullOrBlank() || spinnerCity.text.equals("Select City")){
                    spinnerCity.error = "Select City"
                    spinnerCity.requestFocus()
                    return@setOnClickListener
                }

                if(areaName.isNullOrBlank() || spinnerArea.text.equals("Select Area")){
                    spinnerArea.error = "Select Area"
                    spinnerArea.requestFocus()
                    return@setOnClickListener
                }

                if(pincode.isNullOrBlank() || spinnerPincode.text.equals("Select Pincode")){
                    spinnerPincode.error = "Select Pincode"
                    spinnerPincode.requestFocus()
                    return@setOnClickListener
                }

                if(etLandmark.text.isNullOrBlank()){
                    etLandmark.error = "Enter nearby landmark"
                    etLandmark.requestFocus()
                    return@setOnClickListener
                }

                if(spinnerGcStatus.text.isNullOrBlank() || spinnerGcStatus.text.equals("Select GC status")){
                    spinnerGcStatus.error = "Enter GC Status"
                    spinnerGcStatus.requestFocus()
                    return@setOnClickListener
                }

                gcStatusCode = tpiStatusMap[spinnerGcStatus.text.toString()].toString()

                if(!isFailed){
                    if(spinnerDate.text.isNullOrBlank() || spinnerDate.text.equals("Select Date")){
                        spinnerDate.error = "Enter GC Date"
                        spinnerDate.requestFocus()
                        return@setOnClickListener
                    }

                    if (spinnerPotential.text.isNullOrBlank() || spinnerPotential.text.equals("Select Potential")) {
                        spinnerPotential.error = "Select Potential"
                        spinnerPotential.requestFocus()
                        return@setOnClickListener
                    }

                    if (!isConsentRadioTapped) {
                        Toast.makeText(requireContext(), "Select Consent Taken", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    if (!isWarningPlateRadioTapped) {
                        Toast.makeText(requireContext(), "Select Warning Plate available", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    if (hasConsent){
                        if (consentCount == 0){
                            tvConsentAttachment.error = "Upload consent image"
                            tvConsentAttachment.requestFocus()
                            return@setOnClickListener
                        }
                    }

                    if (hasWarning){
                        if (warningCount == 0){
                            tvWarningForm.error = "Upload warning plate image"
                            tvWarningForm.requestFocus()
                            return@setOnClickListener
                        }
                    }

                    if (unregCount == 0){
                        tvUnregistered.error = "Upload Unreg GC Image"
                        tvUnregistered.requestFocus()
                        return@setOnClickListener
                    }

                }

                if (hasConsent){
                    if (consentCount == 0){
                        tvConsentAttachment.error = "Upload consent image"
                        tvConsentAttachment.requestFocus()
                        return@setOnClickListener
                    }
                }

                if (hasWarning){
                    if (warningCount == 0){
                        tvWarningForm.error = "Upload warning plate image"
                        tvWarningForm.requestFocus()
                        return@setOnClickListener
                    }
                }

                val params = HashMap<String, String>()

                params["customer_name"] = etCustomerName.text.toString()
                params["mobile_number"] = etMobile.text.toString()
                params["tower_no"] = etTower.text.toString()
                params["house_no"] = etDoor.text.toString()
                params["floor_no"] = etFloor.text.toString()
                params["floor_facing"] = spinnerFloor.text.toString()
                params["gassification"] = spinnerGassification.text.toString()
                params["session_id"] = args.sessionId
                params["latitude"] = AppCache.latitude.toString()
                params["longitude"] = AppCache.longitude.toString()
                params["geo_id"] = gaId!!
                params["zonal_id"] = zonalId!!
                params["charge_area_id"] = caId!!
                params["colony_id"] = colonyId ?: ""
                params["district_id"] = districtId!!
                params["taluka_id"] = talukaId!!
                params["city_id"] = cityId!!
                params["area_id"] = areaId!!
                params["pincode_id"] = pincodeId!!
                params["state_name"] = stateName!!
                params["landmark"] = etLandmark.text.toString()
                params["land_mark"] = etLandmark.text.toString()
                params["consent_taken"] = if (hasConsent) "1" else "0"
                params["warning_plate_avb"] = if (hasWarning) "1" else "0"
                params["status_type_id"] = gcStatusCode!!
                params["status_type"] = spinnerGcStatus.text.toString()
                params["gc_date"] = spinnerDate.text.toString()
                params["gc_number"] = ""
                params["potential"] = spinnerPotential.text.toString()
                params["location"] = "${AppCache.latitude} ${AppCache.longitude}"

                viewModel.unregisterCustomer(params)
                setupUnregisterObserver()

//                val directions = RegistrationFragmentDirections.actionRegistrationFragmentToGcStatusFragment(
//                    customerName = args.gcUnregModel?.customerName,
//                    mobile = args.gcUnregModel?.mobileNumber,
//                    address = args.gcUnregModel?.landmark,
//                    sessionId = args.sessionId,
//                    appNo = null,
//                    bpNo = null,
//                    status = args.gcUnregModel?.statusType,
//                    potentialId = args.gcUnregModel?.potential,
//                    gcDate = args.gcUnregModel?.gcDate,
//                    gcNumber = null,
//                    gcApplication = null,
//                    lmcStatus = args.gcUnregModel?.lmcStatus,
//                    gcType = args.gcUnregModel?.gcStatus,
//                    lmcGcAlignment = args.gcUnregModel?.lmcGcAlignment,
//                    gcContractor = args.gcUnregModel?.gcContractor,
//                    gcSupervisor = args.gcUnregModel?.gcSupervisor,
//                    statusTypeId = args.gcUnregModel?.statusTypeId ?: 0,
//                    subStatusId = args.gcUnregModel?.subStatusCode ?: 0,
//                    gcModel = GcUnregisterModel(
//                        customerName = etCustomerName.text.toString(),
//                        mobileNumber = etMobile.text.toString(),
//                        towerNo = etTower.text.toString(),
//                        houseNo = etDoor.text.toString(),
//                        floorNo = etFloor.text.toString(),
//                        floorFacing = spinnerFloor.text.toString(),
//                        gassification = spinnerGassification.text.toString(),
//                        gaId = gaId!!,
//                        zonalId = zonalId!!,
//                        caId = caId!!,
//                        colonyId = colonyId ?: "NA",
//                        districtId = districtId!!,
//                        talukaId = talukaId!!,
//                        cityId = cityId!!,
//                        areaId = areaId!!,
//                        pincodeId = pincodeId!!,
//                        state = stateName!!,
//                        landmark = etLandmark.text.toString()
//                    ),
//                    type = "gc_unreg"
//
//                )

            }
        }

        return binding.root
    }

    fun getBitmap(filePath:String): Bitmap?{
        var bitmap: Bitmap?=null
        try{
            var f:File = File(filePath)
            var options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            bitmap = BitmapFactory.decodeStream(FileInputStream(f),null,options)
        }catch (e:Exception){
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            Log.e("getBitmap: ",e.message.toString() )
        }
        return bitmap
    }


    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                Toast.makeText(
                    requireContext(),
                    "Camera permission is needed to submit",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                takePhoto()
            }

        }

    private fun takePhoto() {

        photoURI = null
        val timeStamp = SimpleDateFormat("ddMMyyyy_HHmmss").format(Date())
        val capturedImage = File(requireActivity().externalCacheDir, "${timeStamp}.jpg")
        if (capturedImage.exists()) {
            capturedImage.delete()
        }
        capturedImage.createNewFile()
        photoURI = if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(
                requireActivity(), BuildConfig.APPLICATION_ID + ".fileprovider",
                capturedImage
            )
        } else {
            Uri.fromFile(capturedImage)
        }

//        val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
//        intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

        var chooserIntent: Intent? = null

        var intentList = mutableListOf<Intent>()

        val pickIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )


// collect all gallery intents
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"

        val listGallery: List<ResolveInfo> = requireActivity().packageManager.queryIntentActivities(galleryIntent, 0)

        for (res in listGallery) {

            val intent = Intent(galleryIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            intentList = AppUtils.addIntentsToList(requireContext(), intentList, intent)

        }

        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePhotoIntent.putExtra("return-data", true)
        takePhotoIntent.putExtra(
            MediaStore.EXTRA_OUTPUT, photoURI
        )
//        intentList = addIntentsToList(requireContext(),
//            intentList, pickIntent)
        intentList = AppUtils.addIntentsToList(requireContext(),
            intentList, takePhotoIntent)

        if (intentList.size > 0) {
            chooserIntent = Intent.createChooser(
                intentList.removeAt(intentList.size-1),
                "Select Source"
            )
            chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS,
                intentList.toTypedArray<Parcelable>()
            )
        }

        cameraActions.launch(chooserIntent)
    }


    private fun setupUnregisterObserver() {
        viewModel.unregisterResponse.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.LOADING->{
                        setDialog(true)
                    }
                    Status.SUCCESS->{
                        setDialog(false)
                        if(!it.data!!.error){
                            findNavController().navigateUp()
                        }
                        Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                    }
                    Status.ERROR->{
                        setDialog(false)
                    }
                }
            }
        }
    }

    private fun setUpObserver() {
        viewModel.tpiListResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it.status) {
                    Status.LOADING -> {
                        setDialog(true)
                    }
                    Status.SUCCESS -> {
                        setDialog(false)
                        if (!it.data!!.error) {


                            it.data.tpiList.forEach { tpi ->
                                tpiStatusMap[tpi.statusType] = tpi.id
                                tpiMap[tpi.statusType] = tpi.subStateList
                                tfStatus[tpi.id] = tpi.status
                            }

                            it.data.tpiList.forEach {tpi->
                                tpi.subStateList.forEach {
                                    tpiSubStatus[it.subStatusType] = it.subId
                                }
                            }
                            val list = mutableListOf<String>()
                            tpiStatusMap.keys.forEach {
                                list.add(it)
                            }

                            it.data.potentialList.forEach { potential ->
                                tpiPotentialMap[potential.potentialType] = potential.id
                            }

                            val potentialList = mutableListOf<String>()
                            tpiPotentialMap.keys.forEach {
                                potentialList.add(it)
                            }


                            potentialSpinnerDialog = SpinnerDialog(
                                activity,
                                potentialList as java.util.ArrayList<String>,
                                "Select Category",
                                "Close"
                            )


                            potentialSpinnerDialog?.bindOnSpinerListener { potential, _ ->
                                binding.apply {
                                    spinnerPotential.text = potential
                                    spinnerPotential.error = null

                                    gcPotential = potential
                                }
                            }

                            statusSpinnerDialog = SpinnerDialog(
                                activity,
                                list as ArrayList<String>,
                                "Select Status Type",
                                "Close"
                            )

                            statusSpinnerDialog?.bindOnSpinerListener { item, position ->
                                val id = tpiStatusMap[item]
                                val status = tfStatus[id]

                                binding.apply {
                                    if(status!!.contains("failed",true)){
                                        toggleVisibility(View.GONE)
                                        isFailed = true
                                    }else{
                                        toggleVisibility(View.VISIBLE)
                                        isFailed = false
                                    }
                                    spinnerGcStatus.text = item
                                    gcStatus = item
                                }
                            }



                            it.data.floorList.forEach { floor->
                                tpiFloorMap[floor.floor] = floor.id
                            }

                            val fList = mutableListOf<String>()
                            it.data.fList.forEach {
                                fList.add(it.floorNo)
                            }

                            fSpinnerDialog = SpinnerDialog(
                                activity,
                                fList as ArrayList<String>,
                                "Select Floor",
                                "Close"
                            )

                            fSpinnerDialog?.bindOnSpinerListener { item, position ->
                                binding.apply {
                                    etFloor.text = item
                                    etFloor.error = null
                                    floorNo = item
                                }
                            }

                            legalEntity.addAll(it.data.entityList)
                            val gaList = mutableListOf<String>()
                            it.data.entityList.forEach {
                                it.gaIdList?.forEach {
                                    it?.gaName?.let { it1 -> gaList.add(it1) }
                                }
                            }

                            gaSpinnerDialog = SpinnerDialog(
                                activity,
                                gaList as ArrayList<String>,
                                "Select GA",
                                "Close"
                            )

                            gaSpinnerDialog?.bindOnSpinerListener { item, position ->
                                val zonalList = mutableListOf<String>()
                                val districtList = mutableListOf<String>()
                                gaName = item
                                binding.spinnerGeoArea.text = item
                                binding.apply {
                                    spinnerGeoArea.error = null
                                    spinnerChargeArea.text = "Select Chargearea"
                                    spinnerColony.text = "Select Colony"
                                    spinnerTaluka.text = "Select Taluka"
                                    spinnerDistrict.text = "Select District"
                                    spinnerCity.text = "Select City"
                                    spinnerArea.text = "Select Area"
                                    spinnerPincode.text = "Select Pincode"
                                }
                                it.data.entityList.forEach {
                                    it.gaIdList!!.forEach {
                                        if (item == it!!.gaName) {
                                            gaId = it.gaId.toString()
                                            it.zonal!!.forEach {
                                                zonalList.add(it!!.zoneName!!)
                                            }
                                            it.districtList!!.forEach {
                                                districtList.add(it!!.districName!!)
                                            }
                                        }
                                    }
                                }

                                districtSpinnerDialog = SpinnerDialog(
                                    activity,
                                    districtList as ArrayList<String>,
                                    "Select District",
                                    "Close"
                                )

                                districtSpinnerDialog?.bindOnSpinerListener { item, position ->
                                    val talukaList = mutableListOf<String>()
                                    districtName = item
                                    binding.spinnerDistrict.text = item
                                    binding.apply {
                                        spinnerDistrict.error = null

                                        spinnerTaluka.text = "Select Taluka"
                                        spinnerCity.text = "Select City"
                                        spinnerArea.text = "Select Area"
                                        spinnerPincode.text = "Select Pincode"
                                    }
                                    it.data.entityList.forEach {
                                        it.gaIdList!!.forEach {
                                            if (gaName == it!!.gaName) {
                                                it.districtList!!.forEach {
                                                    if (item == it!!.districName) {
                                                        districtId = it.districId.toString()
                                                        stateName = it.stateName
                                                        stateId = it.stateId
                                                        binding.etState.text = stateName
                                                        it.talukaList?.forEach {
                                                            talukaList.add(it!!.talukaName!!)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    talukaSpinnerDialog = SpinnerDialog(
                                        activity,
                                        talukaList as ArrayList<String>,
                                        "Select Taluka",
                                        "Close"
                                    )

                                    talukaSpinnerDialog?.bindOnSpinerListener { item, position ->
                                        val cityList = mutableListOf<String>()
                                        talukaName = item
                                        binding.spinnerTaluka.text = item
                                        binding.apply {
                                            spinnerTaluka.error = null
                                            spinnerCity.text = "Select City"
                                            spinnerArea.text = "Select Area"
                                            spinnerPincode.text = "Select Pincode"
                                        }
                                        it.data.entityList.forEach {
                                            it.gaIdList!!.forEach {
                                                if (gaName == it!!.gaName) {
                                                    it.districtList!!.forEach {
                                                        if (districtName == it!!.districName) {
                                                            it.talukaList?.forEach {
                                                                if (item == it!!.talukaName) {
                                                                    talukaId =
                                                                        it.talukaId.toString()
                                                                    it.cityList?.forEach {
                                                                        cityList.add(it?.cityName!!)
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        citySpinnerDialog = SpinnerDialog(
                                            activity,
                                            cityList as ArrayList<String>,
                                            "Select City",
                                            "Close"
                                        )

                                        citySpinnerDialog?.bindOnSpinerListener { item, position ->
                                            val areaList = mutableListOf<String>()
                                            cityName = item
                                            binding.spinnerCity.text = item
                                            binding.apply {
                                                spinnerCity.error = null
                                                spinnerArea.text = "Select Area"
                                                spinnerPincode.text = "Select Pincode"
                                            }
                                            it.data.entityList.forEach {
                                                it.gaIdList!!.forEach {
                                                    if (gaName == it!!.gaName) {
                                                        it.districtList!!.forEach {
                                                            if (districtName == it!!.districName) {
                                                                it.talukaList?.forEach {
                                                                    if (talukaName == it!!.talukaName) {
                                                                        it.cityList?.forEach {
                                                                            if (item == it!!.cityName) {
                                                                                cityId =
                                                                                    it.cityId.toString()
                                                                                it.areaList?.forEach {
                                                                                    areaList.add(it?.areaName!!)
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            areaSpinnerDialog = SpinnerDialog(
                                                activity,
                                                areaList as ArrayList<String>,
                                                "Select Area",
                                                "Close"
                                            )

                                            areaSpinnerDialog?.bindOnSpinerListener { item, position ->
                                                val pincodeList = mutableListOf<String>()
                                                areaName = item
                                                binding.spinnerArea.text = item
                                                binding.apply {
                                                    spinnerArea.error = null
                                                    spinnerPincode.text = "Select Pincode"
                                                }
                                                it.data.entityList.forEach {
                                                    it.gaIdList!!.forEach {
                                                        if (gaName == it!!.gaName) {
                                                            it.districtList!!.forEach {
                                                                if (districtName == it!!.districName) {
                                                                    it.talukaList?.forEach {
                                                                        if (talukaName == it!!.talukaName) {
                                                                            it.cityList?.forEach {
                                                                                if (cityName == it!!.cityName) {
                                                                                    it.areaList?.forEach {
                                                                                        if (item == it!!.areaName) {
                                                                                            areaId =
                                                                                                it.areaId.toString()
                                                                                            it.pinCode?.forEach {
                                                                                                pincodeList.add(
                                                                                                    it!!.pincodeNo!!
                                                                                                )
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                pincodeSpinnerDialog = SpinnerDialog(
                                                    activity,
                                                    pincodeList as ArrayList<String>,
                                                    "Select Pincode",
                                                    "Close"
                                                )

                                                pincodeSpinnerDialog?.bindOnSpinerListener { item, position ->
                                                    pincode = item
                                                    binding.spinnerPincode.text = item
                                                    binding.spinnerPincode.error = null
                                                    it.data.entityList.forEach {
                                                        it.gaIdList!!.forEach {
                                                            if (gaName == it!!.gaName) {
                                                                it.districtList!!.forEach {
                                                                    if (districtName == it!!.districName) {
                                                                        it.talukaList?.forEach {
                                                                            if (talukaName == it!!.talukaName) {
                                                                                it.cityList?.forEach {
                                                                                    if (cityName == it!!.cityName) {
                                                                                        it.areaList?.forEach {
                                                                                            if (areaName == it!!.areaName) {
                                                                                                it.pinCode?.forEach {
                                                                                                    if (item == it!!.pincodeNo) {
                                                                                                        pincodeId =
                                                                                                            it.pincodeId.toString()
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                            }

                                        }

                                    }
                                }



                                zonalSpinnerDialog = SpinnerDialog(
                                    activity,
                                    zonalList as ArrayList<String>,
                                    "Select Zone",
                                    "Close"
                                )

                                zonalSpinnerDialog?.bindOnSpinerListener { item, position ->
                                    val chargeAreaList = mutableListOf<String>()
                                    zonalName = item
                                    binding.spinnerZonal.text = item
                                    binding.apply {
                                        spinnerZonal.error = null
                                        spinnerChargeArea.text = "Select Chargearea"
                                        spinnerColony.text = "Select Colony"
                                    }
                                    it.data.entityList.forEach {
                                        it.gaIdList!!.forEach {
                                            if (gaName == it!!.gaName) {
                                                it.zonal!!.forEach {
                                                    if (item == it!!.zoneName) {
                                                        zonalId = it.zoneId.toString()
                                                        it.chargeAreaList!!.forEach {
                                                            chargeAreaList.add(it!!.chargeArea!!)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    chargeAreaSpinnerDialog = SpinnerDialog(
                                        activity,
                                        chargeAreaList as ArrayList<String>,
                                        "Select Chargearea",
                                        "Close"
                                    )

                                    chargeAreaSpinnerDialog?.bindOnSpinerListener { item, position ->
                                        val colonyList = mutableListOf<String>()
                                        caName = item
                                        binding.spinnerChargeArea.text = item
                                        binding.apply {
                                            spinnerChargeArea.error = null
                                            spinnerColony.text = "Select Colony"
                                        }
                                        it.data.entityList.forEach {
                                            it.gaIdList!!.forEach {
                                                if (gaName == it!!.gaName) {
                                                    it.zonal!!.forEach { it ->
                                                        if (zonalName == it!!.zoneName) {
                                                            it.chargeAreaList!!.forEach { it ->
                                                                if (item == it!!.chargeArea) {
                                                                    colonyList.add(
                                                                        it.colonyDataList
                                                                            ?: "No Colony"
                                                                    )
                                                                    caId = it.chargeId.toString()
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }


                                        colonySpinnerDialog = SpinnerDialog(
                                            activity,
                                            colonyList as ArrayList<String>,
                                            "Select Colony",
                                            "Close"
                                        )

                                        colonySpinnerDialog?.bindOnSpinerListener { item, position ->
                                            colonyName = item
                                            binding.spinnerColony.text = item
                                            binding.spinnerColony.error = null
                                        }

                                    }

                                }
                            }

                            if(args.gcUnregModel != null){
                                val zonalList = mutableListOf<String>()
                                val districtList = mutableListOf<String>()
                                val talukaList = mutableListOf<String>()
                                val cityList = mutableListOf<String>()
                                val areaList = mutableListOf<String>()
                                val pincodeList = mutableListOf<String>()
                                val chargeAreaList = mutableListOf<String>()
                                val colonyList = mutableListOf<String>()
                                gaName = args.gcUnregModel!!.gaName
                                zonalName = args.gcUnregModel!!.zonalName
                                caName = args.gcUnregModel!!.caName
                                colonyName = args.gcUnregModel!!.colonyName
                                districtName = args.gcUnregModel!!.districtName
                                talukaName = args.gcUnregModel!!.talukaName
                                cityName = args.gcUnregModel!!.cityName
                                areaName = args.gcUnregModel!!.areaName
                                pincode = args.gcUnregModel!!.pincodeNo
                                stateName = args.gcUnregModel!!.state
                                gaId = args.gcUnregModel!!.gaId
                                zonalId = args.gcUnregModel!!.zonalId
                                caId = args.gcUnregModel!!.caId
                                colonyId = args.gcUnregModel!!.colonyId
                                districtId = args.gcUnregModel!!.districtId
                                talukaId = args.gcUnregModel!!.talukaId
                                cityId = args.gcUnregModel!!.cityId
                                areaId = args.gcUnregModel!!.areaId
                                pincodeId = args.gcUnregModel!!.pincodeId
                                stateId = args.gcUnregModel!!.stateId

                                it.data.entityList.forEach {
                                    it.gaIdList!!.forEach {
                                        if (gaName == it!!.gaName) {
                                            it.zonal!!.forEach {
                                                zonalList.add(it!!.zoneName!!)
                                                if (zonalName == it!!.zoneName) {
                                                    it.chargeAreaList!!.forEach {
                                                        chargeAreaList.add(it!!.chargeArea!!)
                                                        if (caName == it!!.chargeArea) {
                                                            colonyList.add(
                                                                it.colonyDataList
                                                                    ?: "No Colony"
                                                            )
                                                            caId = it.chargeId.toString()
                                                        }
                                                    }
                                                }
                                            }
                                            it.districtList!!.forEach {
                                                districtList.add(it!!.districName!!)
                                                if (districtName == it!!.districName) {
                                                    it.talukaList?.forEach {
                                                        talukaList.add(it!!.talukaName!!)
                                                        if (talukaName == it!!.talukaName) {
                                                            it.cityList?.forEach {
                                                                cityList.add(it!!.cityName!!)
                                                                if (cityName == it!!.cityName) {
                                                                    it.areaList?.forEach {
                                                                        areaList.add(it!!.areaName!!)
                                                                        if (areaName == it!!.areaName) {
                                                                            it.pinCode?.forEach {
                                                                                pincodeList.add(it!!.pincodeNo!!)
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                districtSpinnerDialog = SpinnerDialog(
                                    activity,
                                    districtList as ArrayList<String>,
                                    "Select District",
                                    "Close"
                                )

                                districtSpinnerDialog?.bindOnSpinerListener { item, position ->
                                    val talukaList = mutableListOf<String>()
                                    districtName = item
                                    binding.spinnerDistrict.text = item
                                    binding.apply {
                                        spinnerDistrict.error = null
                                        spinnerTaluka.text = "Select Taluka"
                                        spinnerCity.text = "Select City"
                                        spinnerArea.text = "Select Area"
                                        spinnerPincode.text = "Select Pincode"
                                    }
                                    it.data.entityList.forEach {
                                        it.gaIdList!!.forEach {
                                            if (gaName == it!!.gaName) {
                                                it.districtList!!.forEach {
                                                    if (item == it!!.districName) {
                                                        districtId = it.districId.toString()
                                                        stateName = it.stateName
                                                        stateId = it.stateId
                                                        binding.etState.text = stateName
                                                        it.talukaList?.forEach {
                                                            talukaList.add(it!!.talukaName!!)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    talukaSpinnerDialog = SpinnerDialog(
                                        activity,
                                        talukaList as ArrayList<String>,
                                        "Select Taluka",
                                        "Close"
                                    )

                                    talukaSpinnerDialog?.bindOnSpinerListener { item, position ->
                                        val cityList = mutableListOf<String>()
                                        talukaName = item
                                        binding.spinnerTaluka.text = item
                                        binding.apply {
                                            spinnerTaluka.error = null
                                            spinnerCity.text = "Select City"
                                            spinnerArea.text = "Select Area"
                                            spinnerPincode.text = "Select Pincode"
                                        }
                                        it.data.entityList.forEach {
                                            it.gaIdList!!.forEach {
                                                if (gaName == it!!.gaName) {
                                                    it.districtList!!.forEach {
                                                        if (districtName == it!!.districName) {
                                                            it.talukaList?.forEach {
                                                                if (item == it!!.talukaName) {
                                                                    talukaId =
                                                                        it.talukaId.toString()
                                                                    it.cityList?.forEach {
                                                                        cityList.add(it?.cityName!!)
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        citySpinnerDialog = SpinnerDialog(
                                            activity,
                                            cityList as ArrayList<String>,
                                            "Select City",
                                            "Close"
                                        )

                                        citySpinnerDialog?.bindOnSpinerListener { item, position ->
                                            val areaList = mutableListOf<String>()
                                            cityName = item
                                            binding.spinnerCity.text = item
                                            binding.apply {
                                                spinnerCity.error = null
                                                spinnerArea.text = "Select Area"
                                                spinnerPincode.text = "Select Pincode"
                                            }
                                            it.data.entityList.forEach {
                                                it.gaIdList!!.forEach {
                                                    if (gaName == it!!.gaName) {
                                                        it.districtList!!.forEach {
                                                            if (districtName == it!!.districName) {
                                                                it.talukaList?.forEach {
                                                                    if (talukaName == it!!.talukaName) {
                                                                        it.cityList?.forEach {
                                                                            if (item == it!!.cityName) {
                                                                                cityId =
                                                                                    it.cityId.toString()
                                                                                it.areaList?.forEach {
                                                                                    areaList.add(it?.areaName!!)
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            areaSpinnerDialog = SpinnerDialog(
                                                activity,
                                                areaList as ArrayList<String>,
                                                "Select Area",
                                                "Close"
                                            )

                                            areaSpinnerDialog?.bindOnSpinerListener { item, position ->
                                                val pincodeList = mutableListOf<String>()
                                                areaName = item
                                                binding.spinnerArea.text = item
                                                binding.apply {
                                                    spinnerArea.error = null
                                                    spinnerPincode.text = "Select Pincode"
                                                }
                                                it.data.entityList.forEach {
                                                    it.gaIdList!!.forEach {
                                                        if (gaName == it!!.gaName) {
                                                            it.districtList!!.forEach {
                                                                if (districtName == it!!.districName) {
                                                                    it.talukaList?.forEach {
                                                                        if (talukaName == it!!.talukaName) {
                                                                            it.cityList?.forEach {
                                                                                if (cityName == it!!.cityName) {
                                                                                    it.areaList?.forEach {
                                                                                        if (item == it!!.areaName) {
                                                                                            areaId =
                                                                                                it.areaId.toString()
                                                                                            it.pinCode?.forEach {
                                                                                                pincodeList.add(
                                                                                                    it!!.pincodeNo!!
                                                                                                )
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                pincodeSpinnerDialog = SpinnerDialog(
                                                    activity,
                                                    pincodeList as ArrayList<String>,
                                                    "Select Pincode",
                                                    "Close"
                                                )

                                                pincodeSpinnerDialog?.bindOnSpinerListener { item, position ->
                                                    pincode = item
                                                    binding.spinnerPincode.text = item
                                                    binding.spinnerPincode.error = null
                                                    it.data.entityList.forEach {
                                                        it.gaIdList!!.forEach {
                                                            if (gaName == it!!.gaName) {
                                                                it.districtList!!.forEach {
                                                                    if (districtName == it!!.districName) {
                                                                        it.talukaList?.forEach {
                                                                            if (talukaName == it!!.talukaName) {
                                                                                it.cityList?.forEach {
                                                                                    if (cityName == it!!.cityName) {
                                                                                        it.areaList?.forEach {
                                                                                            if (areaName == it!!.areaName) {
                                                                                                it.pinCode?.forEach {
                                                                                                    if (item == it!!.pincodeNo) {
                                                                                                        pincodeId =
                                                                                                            it.pincodeId.toString()
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                            }

                                        }

                                    }
                                }

                                talukaSpinnerDialog = SpinnerDialog(
                                    activity,
                                    talukaList as ArrayList<String>,
                                    "Select Taluka",
                                    "Close"
                                )

                                talukaSpinnerDialog?.bindOnSpinerListener { item, position ->
                                    val cityList = mutableListOf<String>()
                                    talukaName = item
                                    binding.spinnerTaluka.text = item
                                    binding.apply {
                                        spinnerTaluka.error = null
                                        spinnerCity.text = "Select City"
                                        spinnerArea.text = "Select Area"
                                        spinnerPincode.text = "Select Pincode"
                                    }
                                    it.data.entityList.forEach {
                                        it.gaIdList!!.forEach {
                                            if (gaName == it!!.gaName) {
                                                it.districtList!!.forEach {
                                                    if (districtName == it!!.districName) {
                                                        it.talukaList?.forEach {
                                                            if (item == it!!.talukaName) {
                                                                talukaId =
                                                                    it.talukaId.toString()
                                                                it.cityList?.forEach {
                                                                    cityList.add(it?.cityName!!)
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    citySpinnerDialog = SpinnerDialog(
                                        activity,
                                        cityList as ArrayList<String>,
                                        "Select City",
                                        "Close"
                                    )

                                    citySpinnerDialog?.bindOnSpinerListener { item, position ->
                                        val areaList = mutableListOf<String>()
                                        cityName = item
                                        binding.spinnerCity.text = item
                                        binding.apply {
                                            spinnerCity.error = null
                                            spinnerArea.text = "Select Area"
                                            spinnerPincode.text = "Select Pincode"
                                        }
                                        it.data.entityList.forEach {
                                            it.gaIdList!!.forEach {
                                                if (gaName == it!!.gaName) {
                                                    it.districtList!!.forEach {
                                                        if (districtName == it!!.districName) {
                                                            it.talukaList?.forEach {
                                                                if (talukaName == it!!.talukaName) {
                                                                    it.cityList?.forEach {
                                                                        if (item == it!!.cityName) {
                                                                            cityId =
                                                                                it.cityId.toString()
                                                                            it.areaList?.forEach {
                                                                                areaList.add(it?.areaName!!)
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        areaSpinnerDialog = SpinnerDialog(
                                            activity,
                                            areaList as ArrayList<String>,
                                            "Select Area",
                                            "Close"
                                        )

                                        areaSpinnerDialog?.bindOnSpinerListener { item, position ->
                                            val pincodeList = mutableListOf<String>()
                                            areaName = item
                                            binding.spinnerArea.text = item
                                            binding.apply {
                                                spinnerArea.error = null
                                                spinnerPincode.text = "Select Pincode"
                                            }

                                            it.data.entityList.forEach {
                                                it.gaIdList!!.forEach {
                                                    if (gaName == it!!.gaName) {
                                                        it.districtList!!.forEach {
                                                            if (districtName == it!!.districName) {
                                                                it.talukaList?.forEach {
                                                                    if (talukaName == it!!.talukaName) {
                                                                        it.cityList?.forEach {
                                                                            if (cityName == it!!.cityName) {
                                                                                it.areaList?.forEach {
                                                                                    if (item == it!!.areaName) {
                                                                                        areaId =
                                                                                            it.areaId.toString()
                                                                                        it.pinCode?.forEach {
                                                                                            pincodeList.add(
                                                                                                it!!.pincodeNo!!
                                                                                            )
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            pincodeSpinnerDialog = SpinnerDialog(
                                                activity,
                                                pincodeList as ArrayList<String>,
                                                "Select Pincode",
                                                "Close"
                                            )

                                            pincodeSpinnerDialog?.bindOnSpinerListener { item, position ->
                                                pincode = item
                                                binding.spinnerPincode.text = item
                                                binding.spinnerPincode.error = null

                                                it.data.entityList.forEach {
                                                    it.gaIdList!!.forEach {
                                                        if (gaName == it!!.gaName) {
                                                            it.districtList!!.forEach {
                                                                if (districtName == it!!.districName) {
                                                                    it.talukaList?.forEach {
                                                                        if (talukaName == it!!.talukaName) {
                                                                            it.cityList?.forEach {
                                                                                if (cityName == it!!.cityName) {
                                                                                    it.areaList?.forEach {
                                                                                        if (areaName == it!!.areaName) {
                                                                                            it.pinCode?.forEach {
                                                                                                if (item == it!!.pincodeNo) {
                                                                                                    pincodeId =
                                                                                                        it.pincodeId.toString()
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                        }

                                    }

                                }


                                citySpinnerDialog = SpinnerDialog(
                                    activity,
                                    cityList as ArrayList<String>,
                                    "Select City",
                                    "Close"
                                )

                                citySpinnerDialog?.bindOnSpinerListener { item, position ->
                                    val areaList = mutableListOf<String>()
                                    cityName = item
                                    binding.spinnerCity.text = item
                                    binding.apply {
                                        spinnerCity.error = null
                                        spinnerArea.text = "Select Area"
                                        spinnerPincode.text = "Select Pincode"
                                    }
                                    it.data.entityList.forEach {
                                        it.gaIdList!!.forEach {
                                            if (gaName == it!!.gaName) {
                                                it.districtList!!.forEach {
                                                    if (districtName == it!!.districName) {
                                                        it.talukaList?.forEach {
                                                            if (talukaName == it!!.talukaName) {
                                                                it.cityList?.forEach {
                                                                    if (item == it!!.cityName) {
                                                                        cityId =
                                                                            it.cityId.toString()
                                                                        it.areaList?.forEach {
                                                                            areaList.add(it?.areaName!!)
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    areaSpinnerDialog = SpinnerDialog(
                                        activity,
                                        areaList as ArrayList<String>,
                                        "Select Area",
                                        "Close"
                                    )

                                    areaSpinnerDialog?.bindOnSpinerListener { item, position ->
                                        val pincodeList = mutableListOf<String>()
                                        areaName = item
                                        binding.spinnerArea.text = item
                                        binding.apply {
                                            spinnerArea.error = null
                                            spinnerPincode.text = "Select Pincode"
                                        }
                                        it.data.entityList.forEach {
                                            it.gaIdList!!.forEach {
                                                if (gaName == it!!.gaName) {
                                                    it.districtList!!.forEach {
                                                        if (districtName == it!!.districName) {
                                                            it.talukaList?.forEach {
                                                                if (talukaName == it!!.talukaName) {
                                                                    it.cityList?.forEach {
                                                                        if (cityName == it!!.cityName) {
                                                                            it.areaList?.forEach {
                                                                                if (item == it!!.areaName) {
                                                                                    areaId =
                                                                                        it.areaId.toString()
                                                                                    it.pinCode?.forEach {
                                                                                        pincodeList.add(
                                                                                            it!!.pincodeNo!!
                                                                                        )
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        pincodeSpinnerDialog = SpinnerDialog(
                                            activity,
                                            pincodeList as ArrayList<String>,
                                            "Select Pincode",
                                            "Close"
                                        )

                                        pincodeSpinnerDialog?.bindOnSpinerListener { item, position ->
                                            pincode = item
                                            binding.spinnerPincode.text = item
                                            binding.spinnerPincode.error = null

                                            it.data.entityList.forEach {
                                                it.gaIdList!!.forEach {
                                                    if (gaName == it!!.gaName) {
                                                        it.districtList!!.forEach {
                                                            if (districtName == it!!.districName) {
                                                                it.talukaList?.forEach {
                                                                    if (talukaName == it!!.talukaName) {
                                                                        it.cityList?.forEach {
                                                                            if (cityName == it!!.cityName) {
                                                                                it.areaList?.forEach {
                                                                                    if (areaName == it!!.areaName) {
                                                                                        it.pinCode?.forEach {
                                                                                            if (item == it!!.pincodeNo) {
                                                                                                pincodeId =
                                                                                                    it.pincodeId.toString()
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    }

                                }


                                areaSpinnerDialog = SpinnerDialog(
                                    activity,
                                    areaList as ArrayList<String>,
                                    "Select Area",
                                    "Close"
                                )

                                areaSpinnerDialog?.bindOnSpinerListener { item, position ->
                                    val pincodeList = mutableListOf<String>()
                                    areaName = item
                                    binding.spinnerArea.text = item
                                    binding.apply {
                                        spinnerArea.error = null
                                        spinnerPincode.text = "Select Pincode"
                                    }
                                    it.data.entityList.forEach {
                                        it.gaIdList!!.forEach {
                                            if (gaName == it!!.gaName) {
                                                it.districtList!!.forEach {
                                                    if (districtName == it!!.districName) {
                                                        it.talukaList?.forEach {
                                                            if (talukaName == it!!.talukaName) {
                                                                it.cityList?.forEach {
                                                                    if (cityName == it!!.cityName) {
                                                                        it.areaList?.forEach {
                                                                            if (item == it!!.areaName) {
                                                                                areaId =
                                                                                    it.areaId.toString()
                                                                                it.pinCode?.forEach {
                                                                                    pincodeList.add(
                                                                                        it!!.pincodeNo!!
                                                                                    )
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    pincodeSpinnerDialog = SpinnerDialog(
                                        activity,
                                        pincodeList as ArrayList<String>,
                                        "Select Pincode",
                                        "Close"
                                    )

                                    pincodeSpinnerDialog?.bindOnSpinerListener { item, position ->
                                        pincode = item
                                        binding.spinnerPincode.text = item
                                        binding.spinnerPincode.error = null

                                        it.data.entityList.forEach {
                                            it.gaIdList!!.forEach {
                                                if (gaName == it!!.gaName) {
                                                    it.districtList!!.forEach {
                                                        if (districtName == it!!.districName) {
                                                            it.talukaList?.forEach {
                                                                if (talukaName == it!!.talukaName) {
                                                                    it.cityList?.forEach {
                                                                        if (cityName == it!!.cityName) {
                                                                            it.areaList?.forEach {
                                                                                if (areaName == it!!.areaName) {
                                                                                    it.pinCode?.forEach {
                                                                                        if (item == it!!.pincodeNo) {
                                                                                            pincodeId =
                                                                                                it.pincodeId.toString()
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                }

                                pincodeSpinnerDialog = SpinnerDialog(
                                    activity,
                                    pincodeList as ArrayList<String>,
                                    "Select Pincode",
                                    "Close"
                                )

                                pincodeSpinnerDialog?.bindOnSpinerListener { item, position ->
                                    pincode = item
                                    binding.spinnerPincode.text = item
                                    binding.spinnerPincode.error = null

                                    it.data.entityList.forEach {
                                        it.gaIdList!!.forEach {
                                            if (gaName == it!!.gaName) {
                                                it.districtList!!.forEach {
                                                    if (districtName == it!!.districName) {
                                                        it.talukaList?.forEach {
                                                            if (talukaName == it!!.talukaName) {
                                                                it.cityList?.forEach {
                                                                    if (cityName == it!!.cityName) {
                                                                        it.areaList?.forEach {
                                                                            if (areaName == it!!.areaName) {
                                                                                it.pinCode?.forEach {
                                                                                    if (item == it!!.pincodeNo) {
                                                                                        pincodeId =
                                                                                            it.pincodeId.toString()
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }


                                zonalSpinnerDialog = SpinnerDialog(
                                    activity,
                                    zonalList as ArrayList<String>,
                                    "Select Zone",
                                    "Close"
                                )

                                zonalSpinnerDialog?.bindOnSpinerListener { item, position ->
                                    val chargeAreaList = mutableListOf<String>()
                                    zonalName = item
                                    binding.spinnerZonal.text = item
                                    binding.apply {
                                        spinnerZonal.error = null
                                        spinnerChargeArea.text = "Select Chargearea"
                                        spinnerColony.text = "Select Colony"
                                    }
                                    it.data.entityList.forEach {
                                        it.gaIdList!!.forEach {
                                            if (gaName == it!!.gaName) {
                                                it.zonal!!.forEach {
                                                    if (item == it!!.zoneName) {
                                                        zonalId = it.zoneId.toString()
                                                        it.chargeAreaList!!.forEach {
                                                            chargeAreaList.add(it!!.chargeArea!!)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    chargeAreaSpinnerDialog = SpinnerDialog(
                                        activity,
                                        chargeAreaList as ArrayList<String>,
                                        "Select Chargearea",
                                        "Close"
                                    )

                                    chargeAreaSpinnerDialog?.bindOnSpinerListener { item, position ->
                                        val colonyList = mutableListOf<String>()
                                        caName = item
                                        binding.spinnerChargeArea.text = item
                                        binding.apply {
                                            spinnerChargeArea.error = null
                                            spinnerColony.text = "Select Colony"
                                        }
                                        it.data.entityList.forEach {
                                            it.gaIdList!!.forEach {
                                                if (gaName == it!!.gaName) {
                                                    it.zonal!!.forEach {
                                                        if (zonalName == it!!.zoneName) {
                                                            it.chargeAreaList!!.forEach {
                                                                if (item == it!!.chargeArea) {
                                                                    colonyList.add(
                                                                        it.colonyDataList
                                                                            ?: "No Colony"
                                                                    )
                                                                    caId = it.chargeId.toString()
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }


                                        colonySpinnerDialog = SpinnerDialog(
                                            activity,
                                            colonyList as ArrayList<String>,
                                            "Select Colony",
                                            "Close"
                                        )

                                        colonySpinnerDialog?.bindOnSpinerListener { item, position ->
                                            colonyName = item
                                            binding.spinnerColony.text = item
                                            binding.spinnerColony.error = null

                                        }

                                    }

                                }


                                chargeAreaSpinnerDialog = SpinnerDialog(
                                    activity,
                                    chargeAreaList as ArrayList<String>,
                                    "Select Chargearea",
                                    "Close"
                                )

                                chargeAreaSpinnerDialog?.bindOnSpinerListener { item, position ->
                                    val colonyList = mutableListOf<String>()
                                    caName = item
                                    binding.spinnerChargeArea.text = item
                                    binding.apply {
                                        spinnerChargeArea.error = null
                                        spinnerColony.text = "Select Colony"
                                    }
                                    it.data.entityList.forEach {
                                        it.gaIdList!!.forEach {
                                            if (gaName == it!!.gaName) {
                                                it.zonal!!.forEach {
                                                    if (zonalName == it!!.zoneName) {
                                                        it.chargeAreaList!!.forEach {
                                                            if (item == it!!.chargeArea) {
                                                                colonyList.add(
                                                                    it.colonyDataList
                                                                        ?: "No Colony"
                                                                )
                                                                caId = it.chargeId.toString()
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }


                                    colonySpinnerDialog = SpinnerDialog(
                                        activity,
                                        colonyList as ArrayList<String>,
                                        "Select Colony",
                                        "Close"
                                    )

                                    colonySpinnerDialog?.bindOnSpinerListener { item, position ->
                                        colonyName = item
                                        binding.spinnerColony.text = item
                                        binding.spinnerColony.error = null
                                    }

                                }

                                colonySpinnerDialog = SpinnerDialog(
                                    activity,
                                    colonyList as ArrayList<String>,
                                    "Select Colony",
                                    "Close"
                                )

                                colonySpinnerDialog?.bindOnSpinerListener { item, position ->
                                    colonyName = item
                                    binding.spinnerColony.text = item
                                    binding.spinnerColony.error = null
                                }

                                binding.apply {

                                    spinnerGeoArea.text = gaName
                                    spinnerZonal.text = zonalName
                                    spinnerChargeArea.text = caName
                                    spinnerColony.text = colonyName
                                    spinnerDistrict.text = districtName
                                    spinnerTaluka.text = talukaName
                                    spinnerCity.text = cityName
                                    spinnerArea.text = areaName
                                    spinnerPincode.text = pincode
                                    etState.text = stateName
                                    spinnerFloor.text = args.gcUnregModel!!.floorFacing
                                    spinnerGassification.text = args.gcUnregModel!!.gassification
                                    etCustomerName.setText(args.gcUnregModel!!.customerName)
                                    etMobile.setText(args.gcUnregModel!!.mobileNumber)
                                    etTower.setText(args.gcUnregModel!!.towerNo)
                                    etDoor.setText(args.gcUnregModel!!.houseNo)
                                    etFloor.setText(args.gcUnregModel!!.floorNo)
                                    etLandmark.setText(args.gcUnregModel!!.landmark)
                                }


                            }

                            it.data.gassifiedList.forEach {
                                tpiGassificationMap[it.status] = it.id
                            }
                            val floorList = mutableListOf<String>()
                            tpiFloorMap.keys.forEach {
                                floorList.add(it)
                            }

                            val gassifiedList = mutableListOf<String>()
                            tpiGassificationMap.keys.forEach {
                                gassifiedList.add(it)
                            }

                            floorSpinnerDialog = SpinnerDialog(
                                activity,
                                floorList as ArrayList<String>,
                                "Select Floor Facing",
                                "Close"
                            )

                            gassifiedSpinnerDialog = SpinnerDialog(
                                activity,
                                gassifiedList as ArrayList<String>,
                                "Select Gasification",
                                "Close"
                            )

                            floorSpinnerDialog?.bindOnSpinerListener { potential, _ ->
                                binding.apply {
                                    spinnerFloor.text = potential
                                    spinnerFloor.error = null
                                    floorfacing = potential
                                }
                            }

                            gassifiedSpinnerDialog?.bindOnSpinerListener { potential, _ ->
                                binding.apply {
                                    spinnerGassification.text = potential
                                    spinnerGassification.error = null
                                    gassification = potential
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

        cameraActions =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK && photoURI!=null) {

                    photoURI.let { uri ->
                        val parcelFileDescriptor =
                            requireActivity().contentResolver.openFileDescriptor(uri!!, "r", null)
                                ?: return@registerForActivityResult
                        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                        val timeStamp: String = SimpleDateFormat("dd-MM-yyy HH:mm:ss").format(Date())
                        lateinit var file:File
                        if(it.data != null && it.data?.data != null){
                            file = File(requireActivity().externalCacheDir,requireActivity().contentResolver.getFileName(it.data?.data!!))
                            var bitmap =  MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,it.data?.data!!)
                            val location = AppUtils.getAddress(
                                AppCache.latitude!!,
                                AppCache.longitude!!,requireContext())
                            val imageText = "$timeStamp \n $location"
                            var result = drawTextToBitmap(bitmap!!, text = imageText)
                            val os: OutputStream = BufferedOutputStream(FileOutputStream(file))
                            result?.compress(Bitmap.CompressFormat.JPEG, 10, os)
//                            inputStream.copyTo(os)
                            os.close()
                        }else{
                            file = File(requireActivity().externalCacheDir,requireActivity().contentResolver.getFileName(uri))
                            var bitmap = getBitmap(file.path)
                            val location = AppUtils.getAddress(
                                AppCache.latitude!!,
                                AppCache.longitude!!,requireContext())
                            val imageText = "$timeStamp \n $location"
                            var result = drawTextToBitmap(bitmap!!, text = imageText)

                            val os: OutputStream = BufferedOutputStream(FileOutputStream(file))
                            result?.compress(Bitmap.CompressFormat.JPEG, 10, os)
//                        inputStream.copyTo(os)
                            os.close()
                        }
//                        var bitmap = getBitmap(it.data?.data!!.path!!)
//                        binding.ivProfile.setImageBitmap(bitmap)



                        if (requireActivity().contentResolver.getFileSize(uri)
                                .toLong() / 1024 < 5120
                        ) {
                            val fileList = ArrayList<File>()
                            fileList.add(file)

                            val request = GcUploadRequestModel(
                                status = "1", mobile = binding.etMobile.text.toString(), sessionId = args.sessionId
                            )
                            when(imageType) {
                                GcUnregType.CONSENT -> {
                                    viewModel.uploadGcAttachment(
                                        request,
                                        file,
                                        Constants.CONSENT_FORM_UPLOAD_FILE
                                    )
                                }
                                GcUnregType.WARNING -> {
                                    viewModel.uploadGcAttachment(
                                        request,
                                        file,
                                        Constants.WARNING_PLATE_FILE
                                    )
                                }
                                GcUnregType.ATTACHMENT -> {
                                    viewModel.uploadGcAttachment(
                                        request,
                                        file,
                                        Constants.UN_REG_GC_IMAGE_FILE
                                    )
                                }
                                else -> {

                                }
                            }

                            setUploadObserver()
                            Toast.makeText(
                                requireActivity(),
                                "${file.name} success",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            Toast.makeText(
                                requireActivity(),
                                "${file.name} exceeds maximum size of 5Mb",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
//                    try {
//                        val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, photoURI)
////                        val image = drawTextToBitmap(bitmap, text = "Venkatesh")
//                        binding.ivProfile.setImageBitmap(bitmap)
//                        val file = File(photoURI!!.path)
//
//
//                    } catch (e: FileNotFoundException) {
//                        e.printStackTrace()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }

                }
            }
    }

    private fun setUploadObserver(){
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
                        }
                        getAttachmentList()
                    }
                    Status.ERROR->{
                        setDialog(false)
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }
    }

    private fun getAttachmentList() {
        val isometricParams = HashMap<String,String>()
        isometricParams["type"] = Constants.UN_REG_GC_IMAGE
        isometricParams["unregstatus"] = "1"
        if(args.gcUnregModel != null){
            isometricParams["mobile_number"] = args.gcUnregModel!!.mobileNumber
        }else isometricParams["mobile_number"] = binding.etMobile.text.toString()


        viewModel.viewAttachments(isometricParams)

        val viewParams = HashMap<String,String>()
        viewParams["type"] = Constants.CONSENT_FORM_UPLOAD
        viewParams["unregstatus"] = "1"
        if(args.gcUnregModel != null){
            viewParams["mobile_number"] = args.gcUnregModel!!.mobileNumber
        }else viewParams["mobile_number"] = binding.etMobile.text.toString()

        viewModel.viewAttachments(viewParams)

        val gcParams = HashMap<String,String>()
        gcParams["type"] = Constants.WARNING_PLATE
        gcParams["unregstatus"] = "1"
        if(args.gcUnregModel != null){
            gcParams["mobile_number"] = args.gcUnregModel!!.mobileNumber
        }else gcParams["mobile_number"] = binding.etMobile.text.toString()
        viewModel.viewAttachments(gcParams)

    }

    private fun attachmentObserver(){
        viewModel.viewAttachmentResponse.observeForever {
            if(it.data!=null){
                when(it.status){
                    Status.LOADING->{

                    }
                    Status.SUCCESS->{
                        if(!it.data.error){
                            when(it.data.type){
                                Constants.CONSENT_FORM_UPLOAD->{
                                    consentAdapter = ViewAttachmentAdapter(
                                        requireContext(),
                                        it.data.attachmentList,
                                        {attachment ->  attachmentItemClicked(attachment) },
                                        {attachment ->  deleteItemClicked(attachment) }
                                    )
                                    binding.rvConsent.adapter = consentAdapter
                                    consentAdapter!!.notifyDataSetChanged()
                                    binding.tvConsentAttachment.error = null

                                    val wordToSpan: Spannable =
                                        SpannableString("Consent Form Upload * (${consentAdapter!!.itemCount})")
                                    wordToSpan.setSpan(
                                        ForegroundColorSpan(Color.RED),
                                        20,
                                        22,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                    )
                                    binding.tvConsentAttachment.text = wordToSpan
                                    consentCount = consentAdapter!!.itemCount

                                }
                                Constants.WARNING_PLATE->{
                                    warningAdapter = ViewAttachmentAdapter(
                                        requireContext(),
                                        it.data.attachmentList,
                                        {attachment ->  attachmentItemClicked(attachment) },
                                        {attachment ->  deleteItemClicked(attachment) }
                                    )
                                    binding.rvWarning.adapter = warningAdapter
                                    warningAdapter!!.notifyDataSetChanged()
                                    binding.tvWarningForm.error = null

                                    val wordToSpan: Spannable =
                                        SpannableString("Warning Plate Upload * (${warningAdapter!!.itemCount})")
                                    wordToSpan.setSpan(
                                        ForegroundColorSpan(Color.RED),
                                        21,
                                        23,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                    )
                                    binding.tvWarningForm.text = wordToSpan

                                    warningCount = warningAdapter!!.itemCount
                                }
                                Constants.UN_REG_GC_IMAGE->{
                                    unRegAdapter = ViewAttachmentAdapter(
                                        requireContext(),
                                        it.data.attachmentList,
                                        {attachment ->  attachmentItemClicked(attachment) },
                                        {attachment ->  deleteItemClicked(attachment) }
                                    )
                                    binding.rvUnregistred.adapter = unRegAdapter
                                    unRegAdapter!!.notifyDataSetChanged()
                                    binding.tvUnregistered.error = null
                                    val wordToSpan: Spannable =
                                        SpannableString("Un Registered GC Image * (${unRegAdapter!!.itemCount})")
                                    wordToSpan.setSpan(
                                        ForegroundColorSpan(Color.RED),
                                        23,
                                        25,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                    )
                                    binding.tvUnregistered.text = wordToSpan
                                    unregCount = unRegAdapter!!.itemCount
                                }
                                else -> {}
                            }

                            if(warningAdapter == null){
                                val wordToSpan: Spannable =
                                    SpannableString("Warning Plate Upload * (0)")
                                wordToSpan.setSpan(
                                    ForegroundColorSpan(Color.RED),
                                    21,
                                    23,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                                binding.tvWarningForm.text = wordToSpan
                                warningCount = 0
                            }

                            if(unRegAdapter == null){
                                val wordToSpan: Spannable =
                                    SpannableString("Un Registered GC Image * (0)")
                                wordToSpan.setSpan(
                                    ForegroundColorSpan(Color.RED),
                                    23,
                                    25,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                                binding.tvUnregistered.text = wordToSpan
                                unregCount = 0
                            }

                            if(consentAdapter == null){
                                val wordToSpan: Spannable =
                                    SpannableString("Consent Form Upload * (0)")
                                wordToSpan.setSpan(
                                    ForegroundColorSpan(Color.RED),
                                    20,
                                    22,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                                binding.tvConsentAttachment.text = wordToSpan
                                consentCount = 0
                            }

                        } else {
                            if(warningAdapter == null){
                                val wordToSpan: Spannable =
                                    SpannableString("Warning Plate Upload * (0)")
                                wordToSpan.setSpan(
                                    ForegroundColorSpan(Color.RED),
                                    21,
                                    23,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                                binding.tvWarningForm.text = wordToSpan
                                warningCount = 0
                            }

                            if(unRegAdapter == null){
                                val wordToSpan: Spannable =
                                    SpannableString("Un Registered GC Image * (0)")
                                wordToSpan.setSpan(
                                    ForegroundColorSpan(Color.RED),
                                    23,
                                    25,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                                binding.tvUnregistered.text = wordToSpan
                                unregCount = 0
                            }

                            if(consentAdapter == null){
                                val wordToSpan: Spannable =
                                    SpannableString("Consent Form Upload * (0)")
                                wordToSpan.setSpan(
                                    ForegroundColorSpan(Color.RED),
                                    20,
                                    22,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                                binding.tvConsentAttachment.text = wordToSpan
                                consentCount = 0
                            }
                        }
                    }
                    Status.ERROR->{

                    }
                }
            }
        }
    }


    private fun deleteItemClicked(attachment: Attachment) {

    }

    private fun attachmentItemClicked(attachment: Attachment) {
        imageLayout?.progressBar?.visibility =
            View.VISIBLE
        Glide.with(requireContext())
            .load(attachment.image)
            .error(R.drawable.alert)
            .listener(
                object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        imageLayout?.progressBar?.visibility =
                            View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        imageLayout?.progressBar?.visibility =
                            View.GONE
                        return false
                    }
                }
            )
            .into(imageLayout?.ivSourceImage!!)
        imageLayout?.tvImageTitle!!.text = attachment.fileName
        imageLayout?.btnDelete!!.visibility = View.VISIBLE
        imageLayout?.ivBack!!.setOnClickListener {
            imageDialog?.dismiss()
        }
        val deleteParams = HashMap<String,String>()
        deleteParams["gcunregstatus"] = "1"
        deleteParams["mobile_no"] = binding.etMobile.text.toString()
        deleteParams["session_id"] = args.sessionId.toString()
        deleteParams["type"] = attachment.type
        deleteParams["image"] = attachment.fileName

        imageLayout?.btnDelete!!.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setCancelable(false)
            builder.setTitle("Delete Attachment")
            builder.setMessage("Are you sure want to delete the file?")
            builder.setPositiveButton("Yes"
            ) { p0, p1 ->
                viewModel.deleteAttachment(deleteParams)
                viewModel.deleteAttachmentResponse.observe(viewLifecycleOwner) {
                    if (it != null) {

                        when (it.status) {
                            Status.SUCCESS -> {
                                if (!it.data!!.error) {
                                    imageDialog!!.dismiss()
                                    binding.rvUnregistred.adapter = null
                                    unRegAdapter = null
                                    binding.rvWarning.adapter = null
                                    warningAdapter = null
                                    binding.rvConsent.adapter = null
                                    consentAdapter = null
                                    getAttachmentList()
                                }
                                Toast.makeText(
                                    requireContext(),
                                    it.data.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            Status.LOADING -> {

                            }
                            Status.ERROR -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to delete file",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        }
                    }
                }
            }
            builder.setNegativeButton("No",null)
            val alert = builder.create()
            alert.show()
        }

        imageDialog?.show()
    }

    private fun drawTextToBitmap(bitmap: Bitmap, textSize: Int = 15, text: String): Bitmap {

        var drawableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val scale = requireContext().resources.displayMetrics.density
        val canvas = Canvas(drawableBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        paint.color = Color.rgb(238, 82, 34)
        paint.textSize = (25 * scale)
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE)

        val bounds = Rect()

        var noOfLines = 0
        for (line in text.split("\n")) {
            noOfLines++
        }

        paint.getTextBounds(text, 0, text.length, bounds)
//        val x: Int = (drawableBitmap.width - bounds.width()) / 6
//        val y: Int = (drawableBitmap.height + bounds.height()) / 5

        val x: Int = 20
        var y: Int = (drawableBitmap.height  - bounds.height()*noOfLines)

        val mPaint = Paint()
        mPaint.color = requireContext().getColor(R.color.black_transparent)
        val left = 0
        val top: Int = drawableBitmap.height - bounds.height() * (noOfLines + 1)
        val right: Int = drawableBitmap.width
        val bottom: Int = drawableBitmap.height
        canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)

        for (line in text.split("\n")) {
            canvas.drawText(line, x.toFloat(), y.toFloat(), paint)
            y += (paint.descent() - paint.ascent()).toInt()
        }

//        canvas.drawText(text, x*scale, y*scale+500f, paint)
        return drawableBitmap
    }


    fun toggleVisibility(visibility:Int){
        binding.apply {
            tvDate.visibility = visibility
            spinnerDate.visibility = visibility
//            tvLine.visibility = visibility
//            etLine.visibility = visibility
            tvApplication.visibility = visibility
            etApplication.visibility = visibility
            rgConsent.visibility = visibility
            rgWarning.visibility = visibility
            tvPotential.visibility = visibility
            spinnerPotential.visibility = visibility
            tvUnregistered.visibility = visibility
            ivUnregistered.visibility = visibility
            rvUnregistred.visibility = visibility
        }
    }


    private fun setDialog(show: Boolean) {
        if (show) dialog!!.show() else dialog!!.dismiss()
    }

}