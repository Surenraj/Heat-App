package com.thinkgas.heatapp.ui.gc

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.*
import android.content.pm.ResolveInfo
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import com.thinkgas.heatapp.data.remote.model.*
import com.thinkgas.heatapp.databinding.CommentDialogBinding
import com.thinkgas.heatapp.databinding.FragmentGcStatusBinding
import com.thinkgas.heatapp.databinding.GcUnregDialogBinding
import com.thinkgas.heatapp.databinding.LayoutViewImageBinding
import com.thinkgas.heatapp.ui.common.adapters.ViewAttachmentAdapter
import com.thinkgas.heatapp.ui.lmc.LmcStatusFragment
import com.thinkgas.heatapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@AndroidEntryPoint
class GcStatusFragment : Fragment() {

    private var _binding: FragmentGcStatusBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<GcStatusViewModel>()
    private val args by navArgs<GcStatusFragmentArgs>()
    var statusSpinnerDialog: SpinnerDialog? = null
    var subStatusSpinner: SpinnerDialog? = null
    var potentialSpinnerDialog: SpinnerDialog? = null
    var hasGcPlate = false
    private var dialogBinding:GcUnregDialogBinding? = null
    private var dialog: Dialog? = null

    private var tpiMap: MutableMap<String, List<SubState>> = mutableMapOf()
    private var tpiStatusMap: MutableMap<String, Int> = mutableMapOf()
    private var tpiSubStatus: MutableMap<String, Int> = mutableMapOf()
    private var tpiPotentialMap: MutableMap<String, Int> = mutableMapOf()
    private var tpiSubStatusMap: MutableMap<String, Int> = mutableMapOf()
    private var tfStatus: MutableMap<Int, String> = mutableMapOf()

    private  var gcAdapter: ViewAttachmentAdapter? = null
    private  var rccAdapter: ViewAttachmentAdapter? = null
    private  var warningAdapter: ViewAttachmentAdapter? = null
    private var imageDialog: Dialog? = null
    private var imageLayout: LayoutViewImageBinding? = null
    private var imageType: GcImageType? = null

    private lateinit var cameraActions: ActivityResultLauncher<Intent>
    lateinit var currentPhotoPath: String
    private var gcStatus: String? = null
    private var gcStatusType:String? = null
    private var gcStatusCode: String? = null
    private var gcSubStatus: String? = null
    private var gcSubStatusCode: String? = null
    private var gcPotential: String? = null
    private var gcNumber: String? = null
    private var gcDate: String? = null
    private var gcApplication: String? = null
    private var gcContractor: String? = null
    private var gcSupervisor: String? = null
    private var fileList: java.util.ArrayList<File>? = ArrayList()
    private var photoURI: Uri? = null
    private var lmcStatus: Int? = 1
    private var gcType: Int? = 1
    private var lmcGcAlignment: Int? = 1

    private var gcAttachmentCount = 0
    private var rccCount = 0
    private var warningCount = 0

    private var isFailed = false
    private var isPassed = false
    private var isHold = false

    var day = 0
    var month: Int = 0
    var year: Int = 0
    var hour: Int = 0
    var minute: Int = 0
    var myDay = 0
    var myMonth: Int = 0
    var myYear: Int = 0
    var myHour: Int = 0
    var myMinute: Int = 0

    companion object {
        var attachmentFlag = false
        var rccFlag = false
        var warningFlag = false

    }

    enum class GcImageType{
        RCC,
        GC_ATTACHMENT,
        WARNING_PLATE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGcStatusBinding.inflate(inflater, container, false)

        binding.apply {
            tvName.text = args.customerName
            tvAddress.text = args.address
            tvMobile.text = args.mobile
            tvAppNo.text = args.appNo
            tvBpNo.text = args.bpNo


            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setView(R.layout.progress)
            dialog = builder.create()

            tvLocation.setOnClickListener {

                val directions = GcStatusFragmentDirections.actionGcStatusFragmentToMapFragment2(
                    latitude = AppCache.latitude.toString(), longitude = AppCache.longitude.toString(),
                    type = "GC",
                )
                findNavController().navigate(directions)
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
                    val directions = GcStatusFragmentDirections.actionGcStatusFragmentToLoginFragment()
                    findNavController().navigate(directions)

                }
                logoutBuilder.setNegativeButton("No") { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                val logoutAlert = logoutBuilder.create()
                logoutAlert.show()
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

            when(args.status){
                "pending"->{
                    if(AppCache.isTpi){
                        tfTitle.text = "Supervisor GC Claimed"
                    }else {
                        tfTitle.text = "GC Pending"
                    }
                }
                "hold"->{
                    if(AppCache.isTpi){
                        tfTitle.text = "Supervisor GC Hold"
                    }else {
                        tfTitle.text = "GC Hold"
                    }
                }
                "done"->{
                    if(AppCache.isTpi){
                        tfTitle.text = "GC Approval Pending"
                    }else {
                        tfTitle.text = "GC Done"
                    }
                }
                "unclaimed"->{
                    if(AppCache.isTpi){
                        tfTitle.text = "Supervisor GC Unclaimed"
                    }else {
                        tfTitle.text = "GC Unclaimed"
                    }
                }
                "failed"->{
                    tfTitle.text = "GC Failed"
                }
                "approved"->{
                    tfTitle.text = "GC Approved"
                }

                "declined"->{
                    tfTitle.text = "GC Declined"
                }
            }

            tvMobile.setOnClickListener {
                val i = Intent(Intent.ACTION_DIAL)
                val p = "tel:" + args.mobile
                i.data = Uri.parse(p)
                startActivity(i)
            }

            spinnerType.setOnClickListener {
                statusSpinnerDialog?.showSpinerDialog()
                statusSpinnerDialog?.setCancellable(false)
                statusSpinnerDialog?.setShowKeyboard(false)
            }


            imageLayout = LayoutViewImageBinding.inflate(LayoutInflater.from(requireActivity()))
            imageDialog = Dialog(requireActivity(), R.style.list_dialog_style)
            imageLayout?.root?.let { imageDialog?.setContentView(it) }

            ivAttachments.setOnClickListener {
                imageType = GcImageType.GC_ATTACHMENT
                requestCameraPermission.launch(
                    Manifest.permission.CAMERA
                )
            }

            ivWarning.setOnClickListener {
                imageType = GcImageType.WARNING_PLATE
                requestCameraPermission.launch(
                    Manifest.permission.CAMERA
                )
            }

            ivIsometric.setOnClickListener {
                imageType = GcImageType.RCC
                requestCameraPermission.launch(
                    Manifest.permission.CAMERA
                )
            }

            if(AppCache.isTpi){
                ivAttachments.visibility = View.GONE
                ivIsometric.visibility = View.GONE
                ivWarning.visibility = View.GONE
                btnSubmit.visibility = View.GONE
                if(args.status == "done" || args.status == "failed") {
                    btnApprove.visibility = View.VISIBLE
                    btnDecline.visibility = View.VISIBLE
                }
                spinnerType.isEnabled = false
                spinnerType.setTextColor(Color.parseColor("#545454"))
                spinnerStatus.isEnabled = false
                spinnerStatus.setTextColor(Color.parseColor("#545454"))
                spinnerDate.isEnabled = false
                spinnerDate.setTextColor(Color.parseColor("#545454"))
                spinnerPotential.isEnabled = false
                spinnerPotential.setTextColor(Color.parseColor("#545454"))
//                etSupervisor.isEnabled = false
//                etLine.isEnabled = false
//                etContractor.isEnabled = false
//                etSupervisor.isEnabled = false
                etApplication.isEnabled = false
            }

            ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }

            btnApprove.setOnClickListener {
                submitGcApproval("Approved")
            }

            btnDecline.setOnClickListener {
                submitGcApproval("Decline")
            }

//            rgCsTwo.setOnCheckedChangeListener { radioGroup, i ->
//                lmcGcAlignment = if(rbCsTwoYes.id == i) 1 else 0
//            }
//            rgCsThree.setOnCheckedChangeListener { radioGroup, i ->
//                lmcStatus = if(rbCsThreeYes.id == i) 1 else 0
//            }
//            rgCsFour.setOnCheckedChangeListener { radioGroup, i ->
//                gcType = if(rbCsFourYes.id == i) 1 else 0
//            }

            if(args.status == "hold" || args.status == "done" || (args.statusTypeId != 0 && args.status!= "failed")){
                var _lmcGcAlignment = -1
                var _lmcStatus = -1
                var _gcType = -1
                var isValid = true

               try {
                   _lmcGcAlignment = args.lmcGcAlignment?.toInt() ?: 0
                   _lmcStatus = args.lmcStatus?.toInt() ?: 0
                   _gcType = args.gcType?.toInt() ?: 0

               }catch (e: NumberFormatException) {
                   isValid = false
                   println(" issue on parsing $e")
               }

                if (isValid) {
                    if (args.lmcGcAlignment != null && args.lmcGcAlignment != "-1") {
                        lmcGcAlignment = _lmcGcAlignment
                    }

                    if (args.lmcStatus != null && args.lmcStatus != "-1") {
                        lmcStatus = _lmcStatus
                    }

                    if (args.gcType != null && args.gcType != "-1") {
                        gcType = _gcType
                    }

                    updateView(args.status.toString())
//                if(args.lmcGcAlignment == "0")  rgCsTwo.check(rgCsTwo.getChildAt(1).id) else rgCsTwo.check(rgCsTwo.getChildAt(0).id)
//
//                if(args.lmcStatus == "0")  rgCsThree.check(rgCsThree.getChildAt(1).id) else rgCsThree.check(rgCsThree.getChildAt(0).id)
//
//                if(args.gcType == "0")  rgCsFour.check(rgCsFour.getChildAt(1).id) else rgCsFour.check(rgCsFour.getChildAt(0).id)
//
                }

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


            btnSubmit.setOnClickListener {
                val params = HashMap<String, String>()
                if (gcStatus.isNullOrBlank()) {
                    spinnerType.error = "Select spinner type"
                    spinnerType.requestFocus()
                    return@setOnClickListener
                }
                if (gcSubStatus.isNullOrBlank()) {
                    spinnerStatus.error = "Select spinner status"
                    spinnerStatus.requestFocus()
                    return@setOnClickListener
                }

                if (isPassed) {
                    if (gcDate.isNullOrBlank() || spinnerDate.text.equals("Select Date")) {
                        spinnerDate.error = "Select any date"
                        spinnerDate.requestFocus()
                        return@setOnClickListener
                    }

                    if (gcPotential.isNullOrBlank() || gcPotential.toString() == "Select Potential") {
                        spinnerPotential.error = "Select Potential"
                        spinnerPotential.requestFocus()
                        return@setOnClickListener
                    }

                    if (gcAttachmentCount == 0) {
                        tvAttachments.error = "LMC Alignment Image required"
                        tvAttachments.requestFocus()
                        return@setOnClickListener
                    }

                    if (rccCount == 0) {
                        tvRcc.error = "RCC Guard Image required"
                        tvRcc.requestFocus()
                        return@setOnClickListener
                    }

                    if (warningCount == 0) {
                        tvWarning.error = "Warning plate required"
                        tvWarning.requestFocus()
                        return@setOnClickListener
                    }

                    if (AppCache.latitude == 0.0 || AppCache.longitude == 0.0) {
                        tvLocation.error = "Select Location"
                        tvLocation.requestFocus()
                        return@setOnClickListener
                    }
                }

                if (isFailed) {
                    if (etDescription.text.isNullOrEmpty()) {
                        etDescription.error = "Please Enter Comments"
                        etDescription.requestFocus()
                        return@setOnClickListener
                    }

                    gcStatusCode = tpiStatusMap[gcStatus].toString()
                    gcSubStatusCode = tpiSubStatusMap[gcSubStatus].toString()

                    params["application_number"] = args.appNo.toString()
                    params["bp_number"] = args.bpNo.toString()
                    params["customer_info"] = args.customerName.toString()
                    params["status_type_id"] = gcStatusCode!!
                    params["status_type"] = gcStatus!!
                    params["sub_status_id"] = gcSubStatusCode!!
                    params["sub_status"] = gcSubStatus!!
                    params["description"] = etDescription.text.toString()
                    params["follow_up_date"] = tvDateTime.text.toString()
                    params["fs_session_id"] = args.sessionId.toString()
                    params["approval_status"] = "Nil"
                    params["comments"] = ""
                    viewModel.submitGc(params)

                    return@setOnClickListener
                }

//
//                if(gcNumber.isNullOrBlank()){
//                    etLine.error = "Enter GC Line Number"
//                    etLine.requestFocus()
//                    return@setOnClickListener
//                }



//                if(gcApplication.isNullOrBlank()){
//                    etApplication.error = "Enter GC application number"
//                    etApplication.requestFocus()
//                    return@setOnClickListener
//                }

//                if(gcContractor.isNullOrBlank()){
//                    etContractor.error = "Enter GC Contractor"
//                    etContractor.requestFocus()
//                    return@setOnClickListener
//                }
//
//                if(gcSupervisor.isNullOrBlank()){
//                    etSupervisor.error = "Enter GC Supervisor"
//                    etSupervisor.requestFocus()
//                    return@setOnClickListener
//                }

                if (isHold) {
                    if (tvDateTime.text.isNullOrEmpty() || tvDateTime.text == "Select Date & Time") {
                        tvDateTime.error = "Select Follow up Data & Time"
                        tvDateTime.requestFocus()
                        return@setOnClickListener
                    }

                    if (etDescription.text.isNullOrEmpty()) {
                        etDescription.error = "Please Enter Comments"
                        etDescription.requestFocus()
                        return@setOnClickListener
                    }
                }

                gcStatusCode = tpiStatusMap[gcStatus].toString()
                gcSubStatusCode = tpiSubStatusMap[gcSubStatus].toString()
                params["application_number"] = args.appNo.toString()
                params["bp_number"] = args.bpNo.toString()
                params["customer_info"] = args.customerName.toString()
                params["status_type_id"] = gcStatusCode.toString()
                params["status_type"] = gcStatus.toString()
                params["sub_status_id"] = gcSubStatusCode.toString()
                params["sub_status"] = gcSubStatus.toString()
                params["gc_date"] = gcDate.toString()
                params["gc_number"] = ""
                params["potential"] = gcPotential.toString()
                params["registered_customer_application_list"] = ""
                params["lmc_status"] = lmcStatus.toString()
                params["approval_status"] = "Nil"
                params["comments"] = ""
                params["lmc_gc_alignment"] = lmcGcAlignment.toString()
                params["gc_type"] = gcType.toString()
                params["fs_session_id"] = args.sessionId.toString()
                params["gc_contractor"] = ""
                params["gc_supervisor"] = ""
                params["location"] = "${AppCache.latitude} ${AppCache.longitude}"
                params["description"] = etDescription.text.toString()
                params["follow_up_date"] = tvDateTime.text.toString()

                viewModel.submitGc(params)

            }

            spinnerStatus.setOnClickListener {
                if (spinnerType.text.contains("select type", true)) {
                    Toast.makeText(
                        requireContext(),
                        "Please select status type first",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                subStatusSpinner?.showSpinerDialog()
                subStatusSpinner?.setCancellable(false)
                subStatusSpinner?.setShowKeyboard(false)
            }

            spinnerPotential.setOnClickListener {
                potentialSpinnerDialog?.showSpinerDialog()
                potentialSpinnerDialog?.setCancellable(false)
                potentialSpinnerDialog?.setShowKeyboard(false)
            }

        }

        val params = HashMap<String, String>()
        params["version_code"] = "1"
        params["os_type"] = "android"
        params["mobile_no"] = args.mobile.toString()

        viewModel.getTpiListTypes(params)

        setUpObserver()

        getAttachmentList()

        viewModel.gcSubmitResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it.status) {
                    Status.LOADING -> {
                        setDialog(true)
                    }
                    Status.SUCCESS -> {
                        setDialog(false)
                        if (it.data != null) {
                            if (!it.data.error) {
                                Toast.makeText(
                                    requireContext(),
                                    it.data.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                findNavController().popBackStack(R.id.gcHomeFragment, false)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    it.data.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
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

        val downDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_down_24)
        val upDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_up_24)
//
        binding.apply {
            tvAttachments.setOnClickListener {
                if (!attachmentFlag) {
                    rvAttachment.visibility = View.GONE
                    tvAttachments.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        downDrawable,
                        null
                    )
                    attachmentFlag = !attachmentFlag
                } else {
                    rvAttachment.visibility = View.VISIBLE
                    tvAttachments.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        upDrawable,
                        null
                    )
                   attachmentFlag = !attachmentFlag
                }
            }
            tvRcc.setOnClickListener {
                if (!rccFlag) {
                    rvRcc.visibility = View.GONE
                    tvRcc.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        downDrawable,
                        null
                    )
                    rccFlag = !rccFlag
                } else {
                    rvRcc.visibility = View.VISIBLE
                    tvRcc.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        upDrawable,
                        null
                    )
                    rccFlag = !rccFlag
                }
            }

            tvWarning.setOnClickListener {
                if (!warningFlag) {
                    rvWarning.visibility = View.GONE
                    tvWarning.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        downDrawable,
                        null
                    )
                    warningFlag = !warningFlag
                } else {
                    rvWarning.visibility = View.VISIBLE
                    tvWarning.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        upDrawable,
                        null
                    )
                    warningFlag = !warningFlag
                }
            }
        }

        return binding.root
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
                        if (it.data != null) {
                            if (!it.data.error) {
                                if (args.type == "gc_unreg") {
                                    gcItemClicked(it.data.gcNumber)
                                } else findNavController().popBackStack(
                                    R.id.dashboardFragment,
                                    false
                                )
                            }
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    Status.ERROR->{
                        setDialog(false)
                    }
                }
            }
        }
    }

    private fun gcItemClicked(number:String) {
       dialogBinding = GcUnregDialogBinding.inflate(LayoutInflater.from(requireContext()))
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        dialogBinding?.tvName?.text = number
        builder.setCancelable(false)
        builder.setView(dialogBinding?.root)
        val alert = builder.create()
        alert.show()
        dialogBinding?.btnNo?.visibility = View.GONE

        if(warningCount > 0){
            dialogBinding?.apply {
                btnYes.text = "Close"
                tvMobileTitle.visibility = View.GONE
            }
        }

        dialogBinding?.btnYes?.setOnClickListener {
            if (warningCount > 0){
                alert.dismiss()
                findNavController().popBackStack(R.id.gcUnregisteredListFragment,false)
                return@setOnClickListener
            }
            imageType = GcImageType.WARNING_PLATE
            hasGcPlate = true
            requestCameraPermission.launch(
                Manifest.permission.CAMERA
            )

        }

        dialogBinding?.btnNo?.setOnClickListener {
            alert.dismiss()
            findNavController().popBackStack(R.id.gcUnregisteredListFragment,false)
        }


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
        binding.tvDateTime.text = AppUtils.getFollowUpDateTime("$myDay/$myMonth/$myYear $myHour:$myMinute")
        LmcStatusFragment.dateTime = "$myDay/$myMonth/$myYear $myHour:$myMinute"
    }

    private fun getAttachmentList() {
        val viewParams = HashMap<String,String>()
        viewParams["bp_number"] = args.bpNo.toString()
        viewParams["application_number"] = args.appNo.toString()
        viewParams["session_id"] = args.sessionId.toString()
        viewParams["type"] = Constants.LMC_GC_ALIGNMENT_IMAGE

        viewModel.viewAttachments(viewParams)

        val isometricParams = HashMap<String,String>()
        isometricParams["bp_number"] = args.bpNo.toString()
        isometricParams["application_number"] = args.appNo.toString()
        isometricParams["session_id"] = args.sessionId.toString()
        isometricParams["type"] = Constants.RCC_GUARD_PHOTO_GROUND

        viewModel.viewAttachments(isometricParams)

        val warningParams = HashMap<String, String>()
        warningParams["bp_number"] = args.bpNo.toString()
        warningParams["application_number"] = args.appNo.toString()
        warningParams["session_id"] = args.sessionId.toString()
        warningParams["type"] = Constants.WARNING_PLATE_GC
        viewModel.viewAttachments(warningParams)

        attachmentObserver()
    }


    private fun attachmentObserver(){
        viewModel.viewAttachmentResponse.observe(viewLifecycleOwner) {
            if(it.data!=null){
                when(it.status){
                    Status.LOADING->{

                    }
                    Status.SUCCESS->{
                        if(!it.data.error){
                            when(it.data.type){
                                Constants.LMC_GC_ALIGNMENT_IMAGE->{
                                    gcAdapter = ViewAttachmentAdapter(
                                        requireContext(),
                                        it.data.attachmentList,
                                        {attachment ->  attachmentItemClicked(attachment) },
                                        {attachment ->  deleteItemClicked(attachment) }
                                    )
                                    binding.rvAttachment.adapter = gcAdapter
                                    gcAdapter?.notifyDataSetChanged()
                                    binding.tvAttachments.error = null
                                    binding.tvAttachments.text = "LMC Alignment Image (${gcAdapter?.itemCount})"
                                    gcAttachmentCount = gcAdapter?.itemCount ?: 0
//                                    if(hasGcPlate){
//                                        dialogBinding!!.btnYes.visibility = View.GONE
//                                        dialogBinding!!.btnNo.visibility = View.VISIBLE
//
//                                        dialogBinding!!.btnNo.text = "Close"
//                                        dialogBinding!!.tvMobileTitle.text = "Image Uploaded Successfully."
//                                    }

                                }
                                Constants.RCC_GUARD_PHOTO_GROUND->{
                                    rccAdapter = ViewAttachmentAdapter(
                                        requireContext(),
                                        it.data.attachmentList,
                                        {attachment ->  attachmentItemClicked(attachment) },
                                        {attachment ->  deleteItemClicked(attachment) }
                                    )
                                    binding.rvRcc.adapter = rccAdapter
                                    rccAdapter?.notifyDataSetChanged()
                                    binding.tvRcc.error = null
                                    binding.tvRcc.text = "Rcc Guard (${rccAdapter?.itemCount ?: 0})"
                                    rccCount = rccAdapter?.itemCount ?: 0
                                }
                                Constants.WARNING_PLATE_GC->{
                                    warningAdapter = ViewAttachmentAdapter(
                                        requireContext(),
                                        it.data.attachmentList,
                                        {attachment ->  attachmentItemClicked(attachment) },
                                        {attachment ->  deleteItemClicked(attachment) }
                                    )
                                    binding.rvWarning.adapter = warningAdapter
                                    warningAdapter?.notifyDataSetChanged()
                                    binding.tvWarning.error = null
                                    binding.tvWarning.text = "Warning Plate (${warningAdapter?.itemCount ?: 0})"
                                    warningCount = warningAdapter?.itemCount ?: 0
                                }
//                                Constants.WARNING_PLATE->{
//                                    if(args.type == "gc_unreg"){
//                                    warningAdapter = ViewAttachmentAdapter(
//                                        requireContext(),
//                                        it.data.attachmentList,
//                                        {attachment ->  attachmentItemClicked(attachment) },
//                                        {attachment ->  deleteItemClicked(attachment) }
//                                    )
//                                    binding.rvWarning.adapter = warningAdapter
//                                    warningAdapter!!.notifyDataSetChanged()
//                                    binding.tvWarning.error = null
//                                    binding.tvWarning.text = "Warning Plate (${warningAdapter!!.itemCount})"
//                                    warningCount = warningAdapter!!.itemCount
//                                    if(args.type == "gc_unreg" && warningCount> 0){
//                                        binding.apply {
//                                            tvWarning.visibility = View.VISIBLE
//                                            ivWarning.visibility = View.VISIBLE
//                                            rvWarning.visibility = View.VISIBLE
//                                        }
//                                    }
//                                    }
//                                }
                                else -> {}
                            }
                            if(gcAdapter == null){
                                binding.tvAttachments.text = "LMC Alignment Image (0)"
                                gcAttachmentCount = 0
                            }

                            if(rccAdapter == null){
                                binding.tvRcc.text = "Rcc Guard (0)"
                                rccCount = 0
                            }

                            if(warningAdapter == null){
                                binding.tvWarning.text = "Warning Plate (0)"
                                warningCount = 0
                            }

                        } else {
                            if(gcAdapter == null){
                                binding.tvAttachments.text = "LMC Alignment Image (0)"
                                gcAttachmentCount = 0
                            }

                            if(rccAdapter == null){
                                binding.tvRcc.text = "Rcc Guard (0)"
                                rccCount = 0
                            }

                            if(warningAdapter == null){
                                binding.tvWarning.text = "Warning Plate (0)"
                                warningCount = 0
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
        imageLayout?.tvImageTitle?.text = attachment.fileName
        imageLayout?.btnDelete?.visibility = View.VISIBLE
        imageLayout?.ivBack?.setOnClickListener {
            imageDialog?.dismiss()
        }
        val deleteParams = HashMap<String,String>()
        deleteParams["bp_number"] = args.bpNo.toString()
        deleteParams["application_number"] = args.appNo.toString()
        deleteParams["session_id"] = args.sessionId.toString()
        deleteParams["type"] = attachment.type
        deleteParams["image"] = attachment.fileName

        imageLayout?.btnDelete?.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setCancelable(false)
            builder.setTitle("Delete Attachment")
            builder.setMessage("Are you sure want to delete the file?")
            builder.setPositiveButton("Yes",object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    viewModel.deleteAttachment(deleteParams)
                    viewModel.deleteAttachmentResponse.observe(viewLifecycleOwner){
                        if(it != null){

                            when(it.status){
                                Status.SUCCESS->{
                                    if (it.data != null) {
                                        if (!it.data.error) {
                                            imageDialog?.dismiss()
                                            binding.rvAttachment.adapter = null
                                            gcAdapter = null
                                            binding.rvRcc.adapter = null
                                            rccAdapter = null
                                            binding.rvWarning.adapter = null
                                            warningAdapter = null
                                            getAttachmentList()
                                        }
                                        Toast.makeText(
                                            requireContext(),
                                            it.data.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                Status.LOADING->{

                                }
                                Status.ERROR->{
                                    Toast.makeText(requireContext(), "Failed to delete file", Toast.LENGTH_SHORT).show()

                                }
                            }
                        }
                    }
                }

            })
            builder.setNegativeButton("No",null)
            val alert = builder.create()
            alert.show()
        }

        imageDialog?.show()
    }

    private fun updateView(statusType: String) {
        binding.apply {
            when (statusType.lowercase()) {
                "passed", "done" -> {
                    tvDate.visibility = View.VISIBLE
                    spinnerDate.visibility = View.VISIBLE
                    tvPotential.visibility = View.VISIBLE
                    spinnerPotential.visibility = View.VISIBLE
                    tvApplication.visibility = View.VISIBLE
                    etApplication.visibility = View.VISIBLE
                    tvAttachments.visibility = View.VISIBLE
                    ivAttachments.visibility = View.VISIBLE
                    rvAttachment.visibility = View.VISIBLE
                    tvRcc.visibility = View.VISIBLE
                    ivIsometric.visibility = View.VISIBLE
                    rvRcc.visibility = View.VISIBLE
                    tvWarning.visibility = View.VISIBLE
                    ivWarning.visibility = View.VISIBLE
                    rvWarning.visibility = View.VISIBLE

                    tvLocation.visibility = View.VISIBLE

                    tvDescription.visibility = View.GONE
                    etDescription.visibility = View.GONE

                    tvFollowTitle.visibility = View.GONE
                    tvDateTime.visibility = View.GONE
                }

                "hold" -> {
                    tvDate.visibility = View.GONE
                    spinnerDate.visibility = View.GONE
                    tvPotential.visibility = View.GONE
                    spinnerPotential.visibility = View.GONE
                    tvApplication.visibility = View.GONE
                    etApplication.visibility = View.GONE
                    tvAttachments.visibility = View.GONE
                    ivAttachments.visibility = View.GONE
                    rvAttachment.visibility = View.GONE
                    tvRcc.visibility = View.GONE
                    ivIsometric.visibility = View.GONE
                    rvRcc.visibility = View.GONE
                    tvWarning.visibility = View.GONE
                    ivWarning.visibility = View.GONE
                    rvWarning.visibility = View.GONE

                    tvLocation.visibility = View.GONE

                    tvDescription.visibility = View.VISIBLE
                    etDescription.visibility = View.VISIBLE

                    tvFollowTitle.visibility = View.VISIBLE
                    tvDateTime.visibility = View.VISIBLE
                }

                "failed" -> {
                    tvDate.visibility = View.GONE
                    spinnerDate.visibility = View.GONE
                    tvPotential.visibility = View.GONE
                    spinnerPotential.visibility = View.GONE
                    tvApplication.visibility = View.GONE
                    etApplication.visibility = View.GONE
                    tvAttachments.visibility = View.GONE
                    ivAttachments.visibility = View.GONE
                    rvAttachment.visibility = View.GONE
                    tvRcc.visibility = View.GONE
                    ivIsometric.visibility = View.GONE
                    rvRcc.visibility = View.GONE
                    tvWarning.visibility = View.GONE
                    ivWarning.visibility = View.GONE
                    rvWarning.visibility = View.GONE

                    tvLocation.visibility = View.GONE

                    tvDescription.visibility = View.VISIBLE
                    etDescription.visibility = View.VISIBLE

                    tvFollowTitle.visibility = View.GONE
                    tvDateTime.visibility = View.GONE
                }
            }

            if (args.gcDate.isNullOrEmpty()) {
                spinnerDate.text = "Select Date"
            }

            if (args.potentialId.isNullOrEmpty()) {
                spinnerPotential.text = "Select Potential"
            }

            if (args.folloUpDate.isNullOrEmpty()) {
                tvDateTime.text = "Select Date & Time"
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
                        if (it.data != null) {
                            if (!it.data.error) {
                                it.data.tpiList.forEach { tpi ->
                                    tpiStatusMap[tpi.statusType] = tpi.id
                                    tpiMap[tpi.statusType] = tpi.subStateList
                                    tfStatus[tpi.id] = tpi.status
                                }

                                it.data.tpiList.forEach { tpi ->
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

                                    Log.d("asasasasasas", status!!)

                                    binding.apply {
                                        spinnerStatus.text = "Select sub status"
                                        spinnerStatus.error = null
                                        gcSubStatus = null

                                        when (status.toString().toLowerCase()) {
                                            "hold" -> {
                                                isHold = true
                                                isPassed = false
                                                isFailed = false
                                                toggleVisibility(View.VISIBLE)
                                            }

                                            "done", "passed" -> {
                                                isHold = false
                                                isPassed = true
                                                isFailed = false
                                                toggleVisibility(View.VISIBLE)
                                            }

                                            "failed" -> {
                                                toggleVisibility(View.GONE)
                                                isHold = false
                                                isPassed = false
                                                isFailed = true
                                            }
                                        }
                                        Log.d("statatdststs", "$isHold -- $isPassed -- $isFailed")
                                        spinnerType.text = item
                                        spinnerType.error = null
                                        spinnerStatus.text = "Select Type"
                                        gcStatus = item

                                        updateView(item.toString())
                                    }
                                    val statusList = mutableListOf<String>()
                                    tpiMap[item]?.forEach {
                                        statusList.add(it.subStatusType)
                                        tpiSubStatusMap[it.subStatusType] = it.subId
                                    }
                                    subStatusSpinner = SpinnerDialog(
                                        activity,
                                        statusList as ArrayList<String>,
                                        "Select Status Type",
                                        "Close"
                                    )
                                    subStatusSpinner?.bindOnSpinerListener { itemNew, _ ->
                                        binding.apply {
                                            spinnerStatus.text = itemNew
                                            spinnerStatus.error = null
                                            gcSubStatus = itemNew
                                        }
                                    }
                                }

                                if (args.status == "hold" || args.status == "done" || args.status == "failed" || args.statusTypeId != 0) {
                                    val statusType =
                                        tpiStatusMap.entries.find { it.value == args.statusTypeId }?.key
                                    val subStatus =
                                        tpiSubStatus.entries.find { it.value == args.subStatusId }?.key
//                                val potential = tpiPotentialMap.entries.find { it.value == args.potentialId }?.key
                                    val potential = args.potentialId

                                    when (args.status.toString().toLowerCase()) {
                                        "hold" -> {
                                            isHold = true
                                            isPassed = false
                                            isFailed = false
                                        }
                                        "done", "passed" -> {
                                            isPassed = true
                                            isHold = false
                                            isFailed = false
                                        }
                                        "failed" -> {
                                            isFailed = true
                                            isPassed = false
                                            isHold = false
                                        }
                                    }

                                    binding.apply {
                                        spinnerType.text = statusType
                                        if (subStatus != null) {
                                            spinnerStatus.text = subStatus

                                            when (args.status.toString().toLowerCase()) {
                                                "hold" -> {
                                                    isHold = true
                                                    isPassed = false
                                                    isFailed = false
                                                }
                                                "done", "passed" -> {
                                                    isPassed = true
                                                    isHold = false
                                                    isFailed = false
                                                }
                                                "failed" -> {
                                                    isFailed = true
                                                    isPassed = false
                                                    isHold = false
                                                }
                                            }

                                        }
                                        if (potential != null) {
                                            spinnerPotential.text = potential
                                        }

//                                    etSupervisor.setText(args.gcSupervisor)
//                                    etContractor.setText(args.gcContractor)
//                                    etLine.setText(args.gcNumber)
                                        spinnerDate.text = args.gcDate
                                        etApplication.setText(args.gcApplication)

                                        updateView(statusType.toString())

                                        etDescription.setText(args.description)
                                        tvDateTime.text = args.folloUpDate
                                    }
                                    gcStatusCode = args.statusTypeId.toString()
                                    gcStatus = statusType
                                    gcSubStatus = subStatus
                                    gcSubStatusCode = args.subStatusId.toString()
                                    gcPotential = potential
                                    gcDate = args.gcDate
                                    gcApplication = args.gcApplication
                                    gcNumber = args.gcNumber
                                    gcSupervisor = args.gcSupervisor
                                    gcContractor = args.gcContractor



                                    if (args.status == "failed") {
                                        toggleVisibility(View.GONE)
                                        isFailed = true
                                    }

                                    val statusList = mutableListOf<String>()
                                    tpiMap[statusType]?.forEach {
                                        statusList.add(it.subStatusType)
                                        tpiSubStatusMap[it.subStatusType] = it.subId
                                    }
                                    subStatusSpinner = SpinnerDialog(
                                        activity,
                                        statusList as ArrayList<String>,
                                        "Select Status Type",
                                        "Close"
                                    )

                                    subStatusSpinner?.bindOnSpinerListener { itemNew, _ ->
                                        binding.apply {
                                            spinnerStatus.text = itemNew
                                            spinnerStatus.error = null
                                            gcSubStatus = itemNew
                                        }
                                    }

                                }

                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Please try again",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
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

        cameraActions = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
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
                            val location = AppUtils.getAddress(AppCache.latitude!!,AppCache.longitude!!,requireContext())
                            val imageText = "$timeStamp \n $location"
                            var result = drawTextToBitmap(bitmap!!, text = imageText)
                            val os: OutputStream = BufferedOutputStream(FileOutputStream(file))
                            result?.compress(Bitmap.CompressFormat.JPEG, 10, os)
//                            inputStream.copyTo(os)
                            os.close()
                        }else{
                            file = File(requireActivity().externalCacheDir,requireActivity().contentResolver.getFileName(uri))
                            var bitmap = getBitmap(file.path)
                            val location = AppUtils.getAddress(AppCache.latitude!!,AppCache.longitude!!,requireContext())
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

                            if(args.type == "gc_list") {
                                val request = UploadRequestModel(
                                    bpNumber = args.bpNo.toString(),
                                    appNo = args.appNo.toString(),
                                    sessionId = args.sessionId.toString()
                                )
                                when (imageType) {
                                    GcImageType.GC_ATTACHMENT -> {
                                        viewModel.uploadAttachment(
                                            request,
                                            file,
                                            Constants.LMC_GC_ALIGNMENT_IMAGE_FILE
                                        )
                                    }
                                    GcImageType.RCC -> {
                                        viewModel.uploadAttachment(
                                            request,
                                            file,
                                            Constants.RCC_GUARD_PHOTO_GROUND_FILE
                                        )

                                    }
                                    GcImageType.WARNING_PLATE -> {
                                        viewModel.uploadAttachment(
                                            request,
                                            file,
                                            Constants.WARNING_PLATE_GC_FILE
                                        )

                                    }
                                    else -> {}
                                }
                            }else{
                                val request = args.gcModel?.let { it1 ->
                                    GcUploadRequestModel(
                                        status = "1", mobile = it1.mobileNumber, sessionId = args.sessionId.toString()

                                    )
                                }
                                if (request != null) {
                                    when (imageType) {
                                        GcImageType.GC_ATTACHMENT -> {
                                            viewModel.uploadGcAttachment(
                                                request,
                                                file,
                                                Constants.LMC_GC_ALIGNMENT_IMAGE_FILE
                                            )
                                        }

                                        GcImageType.WARNING_PLATE -> {
                                            viewModel.uploadGcAttachment(
                                                request,
                                                file,
                                                Constants.WARNING_PLATE_FILE
                                            )
                                        }

                                        GcImageType.RCC -> {
                                            viewModel.uploadGcAttachment(
                                                request,
                                                file,
                                                Constants.RCC_GUARD_PHOTO_GROUND_FILE
                                            )

                                        }

                                        else -> {}
                                    }
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

    private fun submitGcApproval(status:String){
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
                    bpNumber = args.bpNo.toString(), appNo = args.appNo.toString(), sessionId = args.sessionId.toString()
                )
                setUploadObserver(dialogBinding)
                viewModel.uploadAttachment(request,file, Constants.FS_TPI_SIGNATURE_FILE)
            }
            btnSubmit.setOnClickListener {
                if(signature.isEnabled){
                    Toast.makeText(requireContext(), "Signature is needed", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val params = HashMap<String, String>()
                params["application_number"] = args.appNo!!
                params["bp_number"] = args.bpNo!!
                params["customer_info"] = args.customerName!!
                params["status_type_id"] = gcStatusCode!!
                params["status_type"] = gcStatus!!
                params["sub_status_id"] = gcSubStatusCode!!
                params["sub_status"] = gcSubStatus!!
//                params["pipeline_category_id"] = fsPipelineCode!!
//                params["pipeline_category"] = fsPipeline!!
//                params["description"] = args.description.toString()
                params["fs_session_id"] = args.sessionId!!
                params["approval_status"] = status
                params["comments"] = etComment.text.toString()
                viewModel.submitGc(params)
                alert.dismiss()

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

                    }
                    Status.SUCCESS->{
                        Toast.makeText(requireContext(), it.data?.message, Toast.LENGTH_SHORT).show()
                        binding.signature.isEnabled = false
                        binding.btnClear.visibility =  View.GONE
                        binding.btnSignature.visibility = View.GONE
                    }
                    Status.ERROR->{
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun getBitmap(filePath:String):Bitmap?{
        var bitmap:Bitmap?=null
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
            intentList = AppUtils.addIntentsToList(requireContext(),
                intentList, intent)
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


    private fun setUploadObserver(){
        viewModel.uploadResponse.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.LOADING->{
                        setDialog(true)
                    }
                    Status.SUCCESS->{
                        setDialog(false)
                        if(it.data?.error == true) {
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT)
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

    private fun toggleVisibility(visibility:Int){
        binding.apply {
            tvDate.visibility = visibility
            spinnerDate.visibility = visibility
//            tvLine.visibility = visibility
//            etLine.visibility = visibility
            tvApplication.visibility = visibility
            etApplication.visibility = visibility
//            tvCsTwo.visibility =visibility
//            rgCsTwo.visibility = visibility
//            tvCsThree.visibility = visibility
//            rgCsThree.visibility = visibility
//            tvCsFour.visibility = visibility
//            rgCsFour.visibility = visibility
            tvPotential.visibility = visibility
            spinnerPotential.visibility = visibility
//            tvContractor.visibility = visibility
//            etContractor.visibility = visibility
//            tvSupervisor.visibility = visibility
//            etSupervisor.visibility = visibility
            tvAttachments.visibility = visibility
            ivAttachments.visibility = visibility
            rvAttachment.visibility = visibility
            tvRcc.visibility = visibility
            ivIsometric.visibility = visibility
            rvRcc.visibility = visibility
            tvWarning.visibility = visibility
            ivWarning.visibility = visibility
            rvWarning.visibility = visibility
        }
    }

    private fun setDialog(show: Boolean) {
        if (show) dialog?.show() else dialog?.dismiss()
    }

}