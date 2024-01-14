package com.thinkgas.heatapp.ui.lmc

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.Manifest
import android.app.*
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
import com.thinkgas.heatapp.data.cache.AppCache.lmcGasType
import com.thinkgas.heatapp.data.cache.AppCache.lmcMeterDetails
import com.thinkgas.heatapp.data.cache.AppCache.lmcMeterList
import com.thinkgas.heatapp.data.cache.AppCache.lmcMeterStatusList
import com.thinkgas.heatapp.data.cache.AppCache.lmcPropertyList
import com.thinkgas.heatapp.data.cache.AppCache.lmcTypeList
import com.thinkgas.heatapp.data.cache.AppCache.tpiMap
import com.thinkgas.heatapp.data.cache.AppCache.tpiStatusMap
import com.thinkgas.heatapp.data.cache.AppCache.tpiSubStatusMap
import com.thinkgas.heatapp.data.remote.model.Attachment
import com.thinkgas.heatapp.data.remote.model.UploadRequestModel
import com.thinkgas.heatapp.databinding.CommentDialogBinding
import com.thinkgas.heatapp.databinding.FragmentLmcStatusBinding
import com.thinkgas.heatapp.databinding.LayoutViewImageBinding
import com.thinkgas.heatapp.ui.common.adapters.ViewAttachmentAdapter
import com.thinkgas.heatapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@AndroidEntryPoint
class LmcStatusFragment : Fragment() {
    private var _binding: FragmentLmcStatusBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<LmcStatusFragmentArgs>()
    private val viewModel by viewModels<LmcStatusViewModel>()
    var statusSpinnerDialog: SpinnerDialog? = null
    var subStatusSpinner: SpinnerDialog? = null
    var lmcSpinnerDialog: SpinnerDialog? = null
    var isFailed = false
    var isHold = false
    private var imageDialog: Dialog? = null
    private var imageLayout: LayoutViewImageBinding? = null
    private var attachmentCount = 0
    private var dialog: Dialog? = null



    private lateinit var cameraActions: ActivityResultLauncher<Intent>
    private var photoURI: Uri? = null

    private var viewAttachmentAdapter: ViewAttachmentAdapter? = null


//    private var tpiMap: MutableMap<String, List<SubState>> = mutableMapOf()
//    private var tpiStatusMap: MutableMap<String, Int> = mutableMapOf()
//    private var tpiSubStatusMap: MutableMap<String, Int> = mutableMapOf()

    companion object{
        var fsStatus: String? = null
        var fsStatusCode: String? = null
        var fsSubStatus: String? = null
        var fsSubStatusCode: String? = null
        var lmcExecution: String? = null
        var dateTime:String? = null
        var description:String? = null
        var attachmentFlag = false
    }


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
    private var hasFollowUp = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLmcStatusBinding.inflate(inflater, container, false)

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.progress)
        dialog = builder.create()

        val params = HashMap<String, String>()
        params["version_code"] = "1"
        params["os_type"] = "android"
        params["mobile_no"] = args.mobile

        viewModel.getTpiListTypes(params)
        setUpObserver()

        binding.apply {

            when(args.status){
                "pending"->{
                    if(AppCache.isTpi){
                        lmcStatus.text = "Supervisor LMC Claimed"
                    }else {
                        lmcStatus.text = "LMC Pending"
                    }
                }
                "hold"->{
                    if(AppCache.isTpi){
                        lmcStatus.text = "Supervisor LMC Hold"
                    }else {
                        lmcStatus.text = "LMC Hold"
                    }
                }
                "done"->{
                    if(AppCache.isTpi){
                        lmcStatus.text = "LMC Approval Pending"
                    }else {
                        lmcStatus.text = "LMC Done"
                    }
                }
                "unclaimed"->{
                    if(AppCache.isTpi){
                        lmcStatus.text = "Supervisor LMC Unclaimed"
                    }else {
                        lmcStatus.text = "LMC Unclaimed"
                    }

                }
                "failed"->{
                    lmcStatus.text = "LMC Failed"
                }
                "approved"->{
                    lmcStatus.text = "LMC Approved"
                }

                "declined"->{
                    lmcStatus.text = "LMC Declined"
                }
            }

            btnUpload.setOnClickListener {
                requestCameraPermission.launch(
                    Manifest.permission.CAMERA
                )
            }

            val downDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_down_24)
            val upDrawable =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_up_24)

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
                    val directions = LmcStatusFragmentDirections.actionLmcStatusFragmentToLoginFragment()
                    findNavController().navigate(directions)
                }
                logoutBuilder.setNegativeButton("No") { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                val logoutAlert = logoutBuilder.create()
                logoutAlert.show()
            }


            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
            if(fsStatus != null){
                spinnerType.text = fsStatus
            }

            if(fsSubStatus != null){
                spinnerStatus.text = fsSubStatus
            }

            if(lmcExecution != null){
                spinnerLmc.text = lmcExecution
            }

            cbFollowUp.setOnCheckedChangeListener { compoundButton, isChecked ->
                if (isChecked) {
                    toggleFollowUpVisibility(true)
                    hasFollowUp = true
                    btnSubmit.text = "Submit"
                } else {
                    toggleFollowUpVisibility(false)
                    hasFollowUp = false
                    btnSubmit.text = "Next"
                }
            }

            tvAddress.text = args.address
            tvName.text = args.customerName
            tvMobile.text = args.mobile
            tvAppNo.text = args.appNo
            tvBpNo.text = args.bpNo

            imageLayout = LayoutViewImageBinding.inflate(LayoutInflater.from(requireActivity()))
            imageDialog = Dialog(requireActivity(), R.style.list_dialog_style)
            imageDialog!!.setContentView(imageLayout!!.root)

            spinnerType.setOnClickListener {
                statusSpinnerDialog?.showSpinerDialog()
                statusSpinnerDialog?.setCancellable(false)
                statusSpinnerDialog?.setShowKeyboard(false)
            }

            spinnerLmc.setOnClickListener {
                lmcSpinnerDialog?.showSpinerDialog()
                lmcSpinnerDialog?.setCancellable(false)
                lmcSpinnerDialog?.setShowKeyboard(false)
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

            if(args.status == "hold" || args.status == "done" || args.status == "failed"){
                val statusType = tpiStatusMap.entries.find {
                    it.value == (args.statusId?.toInt() ?: 0)
                }?.key
                val subStatus = tpiSubStatusMap.entries.find { it.value == args.subStatusId?.toInt() }?.key

                binding.apply {
                    fsStatus = args.statusType
                    fsSubStatus = args.substatus
                    fsStatusCode = args.statusId
                    fsSubStatusCode = args.subStatusId
                    lmcExecution = args.lmcExecution
                    spinnerType.text = args.statusType
                    spinnerStatus.text = args.substatus
                    spinnerLmc.text = args.lmcExecution
                    cbFollowUp.visibility = View.GONE
                    tvFollowText.visibility = View.GONE

                    isFailed = args.statusType!!.contains("failed",true)
                    isHold = args.statusType!!.contains("hold",true)

                    val statusList = mutableListOf<String>()
                    tpiMap[args.statusType]?.forEach {
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
                            fsSubStatus = itemNew
                        }
                    }



                    if(args.statusType!!.contains("failed",true)){
                        btnSubmit.text ="SUBMIT"
                        isFailed = true
                        spinnerLmc.visibility = View.GONE
                        tvLmcExecution.visibility = View.GONE
                    }

                    if(args.statusType!!.contains("hold",true)){
                        btnSubmit.text ="SUBMIT"
                        isHold = true
                        cbFollowUp.visibility = View.VISIBLE
                        tvFollowText.visibility = View.VISIBLE
                        cbFollowUp.isChecked = true
                        tvDateTime.text = args.followUpDate
                        etDescription.setText(args.description)
                    }
                }

            }

            if(AppCache.isTpi){
                if(args.status == "failed"){
                    btnSubmit.visibility = View.GONE
                    btnApprove.visibility = View.VISIBLE
                    btnDecline.visibility = View.VISIBLE
                }
                btnSubmit.text = "Next"
                spinnerType.isEnabled = false
                spinnerType.setTextColor(Color.parseColor("#545454"))
                spinnerStatus.isEnabled = false
                spinnerStatus.setTextColor(Color.parseColor("#545454"))
                spinnerLmc.isEnabled = false
                spinnerLmc.setTextColor(Color.parseColor("#545454"))
            }

            btnApprove.setOnClickListener {
                submitLmcApproval("Approved")
            }

            btnDecline.setOnClickListener {
                submitLmcApproval("Declined")

            }

            btnSubmit.setOnClickListener {

//                if (fsStatus.isNullOrBlank()) {
//                    Toast.makeText(
//                        requireContext(),
//                        "Please select status type",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    return@setOnClickListener
//                }
//                if (fsSubStatus.isNullOrBlank()) {
//                    Toast.makeText(requireContext(), "Please select substatus", Toast.LENGTH_SHORT)
//                        .show()
//                    return@setOnClickListener
//                }

                if(args.status == "pending") {
                    fsStatusCode = tpiStatusMap[fsStatus].toString()
                    fsSubStatusCode = tpiSubStatusMap[fsSubStatus].toString()
                }

                if(isHold && !AppCache.isTpi){
                    if(!cbFollowUp.isChecked){
                        Toast.makeText(requireContext(), "Please provide follow up details.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    if(tvDateTime.text.isBlank()){
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
                    params["lmc_session_id"]=args.sessionId
                    params["application_number"] = args.appNo
                    params["bp_number"] = args.bpNo
                    params["status_type_id"] = fsStatusCode!!
                    params["status_type"] = fsStatus!!
                    params["sub_status_id"] = fsSubStatusCode!!
                    params["sub_status"] = fsSubStatus!!
                    params["tpi_id"] = args.tpiId
                    params["approval_status"] = "Nil"
                    params["comments"] = "Nil"
                    params["follow_up_date"] = tvDateTime.text.toString()
                    params["description"] = etDescription.text.toString()
                    viewModel.submitLmc(params)
                    setupSubmitObserver()
                    return@setOnClickListener
                }

                if((isFailed) && !AppCache.isTpi){
                    if(etDescription.text.isBlank()){
                        etDescription.error = "Please enter comments"
                        etDescription.requestFocus()
                        return@setOnClickListener
                    }
                    val params = HashMap<String,String>()
                    params["lmc_session_id"]=args.sessionId
                    params["application_number"] = args.appNo
                    params["bp_number"] = args.bpNo
                    params["status_type_id"] = fsStatusCode!!
                    params["status_type"] = fsStatus!!
                    params["sub_status_id"] = fsSubStatusCode!!
                    params["sub_status"] = fsSubStatus!!
                    params["tpi_id"] = args.tpiId
                    params["approval_status"] = "Nil"
                    params["comments"] = "Nil"
                    params["description"] = etDescription.text.toString()

                    viewModel.submitLmc(params)
                    setupSubmitObserver()
                    return@setOnClickListener
                }

                if(args.status == "pending") {
                    if (!hasFollowUp) {

                        if(fsStatus.isNullOrBlank()){
                            spinnerType.error = "Please Select status"
                            spinnerType.requestFocus()
                            return@setOnClickListener
                        }

                        if(fsSubStatus.isNullOrBlank()){
                            spinnerStatus.error = "Please Select substatus"
                            spinnerStatus.requestFocus()
                            return@setOnClickListener
                        }

                        if(lmcExecution.isNullOrBlank()){
                            spinnerLmc.error = "Please Select LMC Execution"
                            spinnerLmc.requestFocus()
                            return@setOnClickListener
                        }

                        val directions =
                            LmcStatusFragmentDirections.actionLmcStatusFragmentToLmcConnectionFragment(
                                lmcStatusType = fsStatus.toString(),
                                lmcSubStatus = fsSubStatus.toString(),
                                appNo = args.appNo,
                                bpNo = args.bpNo,
                                customerInfo = args.customerName,
                                tpiId = args.tpiId,
                                followUpDate = dateTime,
                                description = "",
                                sessionId = args.sessionId,
                                lmcExecution = lmcExecution,
                                mobile = args.mobile,
                                firstName = args.firstName,
                                middleName = args.middleName,
                                lastName = args.lastName,
                                email = args.email,
                                lmcConnectionModel = null
                            )
                        findNavController().navigate(directions)
                    } else {
                        if (dateTime.isNullOrBlank()) {
                            tvDateTime.error = "Please select date & time"
                            tvDateTime.requestFocus()
                            return@setOnClickListener
                        }

                        if (etDescription.text.isNullOrBlank()) {
                            etDescription.error = "Please enter description"
                            etDescription.requestFocus()
                            return@setOnClickListener
                        }

//                        if(attachmentCount == 0){
//                            tvAttachments.error = "Please add attachments"
//                            tvAttachments.requestFocus()
//                            return@setOnClickListener
//                        }

                        val params = HashMap<String, String>()
                        params["application_number"] = args.appNo
                        params["bp_number"] = args.bpNo
                        params["status_type_id"] = fsStatusCode!!
                        params["status_type"] = fsStatus!!
                        params["sub_status_id"] = fsSubStatusCode!!
                        params["sub_status"] = fsSubStatus!!
                        params["follow_up_date"] = dateTime!!
                        params["description"] = etDescription.text.toString()
                        params["lmc_session_id"] = args.sessionId

                        viewModel.updateFollowUpStatus(params)

                        setUpFollowUpObserver()

                    }
                }



                if(args.status == "hold" || args.status == "done" || args.status == "failed"){

                    val directions =
                        LmcStatusFragmentDirections.actionLmcStatusFragmentToLmcConnectionFragment(
                            lmcStatusType = spinnerType.text.toString(),
                            lmcSubStatus = spinnerStatus.text.toString(),
                            appNo = args.appNo,
                            bpNo = args.bpNo,
                            customerInfo = args.customerName,
                            tpiId = args.tpiId,
                            followUpDate = dateTime,
                            description = tvDescription.text.toString(),
                            sessionId = args.sessionId,
                            mobile = args.mobile,
                            firstName = args.firstName,
                            middleName = args.middleName,
                            lastName = args.lastName,
                            email = args.email,
                            status = args.status,
                            statusId = args.statusId,
                            subStatusId = args.subStatusId,
                            lmcType = args.lmcType,
                            meterDetails = args.meterDetails,
                            meterNo = args.meterNo,
                            meterType = args.meterType,
                            initialReading = args.initialReading,
                            regNo = args.regNo,
                            giNo = args.giNo,
                            cuNo = args.cuNo,
                            avNo = args.avNo,
                            ivNo = args.ivNo,
                            pipeLength = args.pipeLength,
                            propertyType = args.propertyType,
                            gasType = args.gasType,
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
                            meterSerialNo = args.meterSerialNo,
                            confirmStatus = args.confirmStatus,
                            extraGiLength = args.extraGiLength,
                            extraMlLength = args.extxraMlLength,
                            acTape = args.acTape,
                            lmcExecution = lmcExecution,
                            lmcConnectionModel = args.lmcConnectionModel
                        )
                    findNavController().navigate(directions)

                }

            }
        }

        getAttachmentList()

        viewModel.viewAttachmentResponse.observeForever {
            if(it.data!=null){
                when(it.status){
                    Status.LOADING->{
                        setDialog(true)
                    }
                    Status.SUCCESS->{
                        setDialog(false)
                        if(!it.data.error){
                            when(it.data.type){
                                Constants.LMC_FOLLOW_UP_FILE->{
                                    viewAttachmentAdapter = ViewAttachmentAdapter(
                                        requireContext(),
                                        it.data.attachmentList,
                                        {attachment ->  attachmentItemClicked(attachment) },
                                        {attachment ->  deleteItemClicked(attachment) }
                                    )
                                    binding.rvAttachment.adapter = viewAttachmentAdapter
                                    viewAttachmentAdapter!!.notifyDataSetChanged()
                                    binding.tvAttachments.error = null
                                    binding.tvAttachments.text = "Attachments (${viewAttachmentAdapter!!.itemCount})"
                                    attachmentCount = viewAttachmentAdapter!!.itemCount

                                }
                                else -> {}
                            }

                            if(viewAttachmentAdapter == null){
                                binding.tvAttachments.text = "Attachments (0)"
                                attachmentCount = 0
                            }

                        } else {

                            if(viewAttachmentAdapter == null){
                                binding.tvAttachments.text = "Attachments (0)"
                                attachmentCount = 0
                            }

                        }
                    }
                    Status.ERROR->{
                        setDialog(false)
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

                        if (requireActivity().contentResolver.getFileSize(uri)
                                .toLong() / 1024 < 5120
                        ) {
                            val fileList = ArrayList<File>()
                            fileList.add(file)

                            val request=UploadRequestModel(
                                bpNumber = args.bpNo, appNo = args.appNo, sessionId = args.sessionId
                            )
                            viewModel.uploadAttachment(request,file,Constants.LMC_FOLLOW_UP_FILE_TYPE)
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

        return binding.root
    }

    private fun deleteItemClicked(attachment: Attachment) {

    }

    private fun submitLmcApproval(status:String){
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
                    bpNumber = args.bpNo, appNo = args.appNo, sessionId = args.sessionId
                )
                viewModel.uploadAttachment(request,file, Constants.LMC_TPI_SIGNATURE_FILE)
                setSignatureObserver(dialogBinding)
            }
            btnSubmit.setOnClickListener {
                if(signature.isEnabled){
                    Toast.makeText(requireContext(), "Signature is needed", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val params = java.util.HashMap<String, String>()
                params["application_number"] = args.appNo
                params["bp_number"] = args.bpNo
                params["lmc_session_id"] = args.sessionId
                params["approval_status"] = status
                params["comments"] = etComment.text.toString()
                viewModel.submitLmc(params)
                alert.dismiss()
                setupObserver()

            }
        }

        dialogBinding.btnClose.setOnClickListener {
            alert.dismiss()
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
                            findNavController().popBackStack(R.id.lmcHomeFragment,false)
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


    private fun setSignatureObserver(binding:CommentDialogBinding) {
        viewModel.uploadResponse.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.LOADING->{
                        setDialog(true)
                    }
                    Status.SUCCESS->{
                        setDialog(false)
                        if(!it.data?.error!!) {
                            binding.apply {
                                signature.isEnabled = false
                                btnSignature.visibility = View.GONE
                                btnClear.visibility = View.GONE
                            }

                        }
                        Toast.makeText(requireContext(), it.data!!.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                    Status.ERROR->{
                        setDialog(false)
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }
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
        deleteParams["bp_number"] = args.bpNo
        deleteParams["application_number"] = args.appNo
        deleteParams["session_id"] = args.sessionId
        deleteParams["type"] = attachment.type
        deleteParams["image"] = attachment.fileName

        imageLayout?.btnDelete!!.setOnClickListener {
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
                                    if(!it.data!!.error){
                                        imageDialog!!.dismiss()
//                                        binding.rvAttachment.adapter = null
                                        binding.rvAttachment.adapter = null
                                        viewAttachmentAdapter = null
                                        getAttachmentList()
                                    }
                                    Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
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

    private fun getAttachmentList() {
        val viewParams = java.util.HashMap<String, String>()
        viewParams["bp_number"] = args.bpNo
        viewParams["application_number"] = args.appNo
        viewParams["session_id"] = args.sessionId
        viewParams["type"] = Constants.LMC_FOLLOW_UP_FILE

        viewModel.viewAttachments(viewParams)

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

//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
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

    private fun setUpFollowUpObserver() {
        viewModel.followUpResponse.observe(viewLifecycleOwner){
            if(it.data!=null){
                when(it.status){
                    Status.LOADING->{
                        setDialog(true)
                    }
                    Status.SUCCESS->{
                        setDialog(false)
                        if (!it.data.error){
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                            requireActivity().onBackPressed()
                        }else{
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
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

    private val dateListener = DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
        myDay = i3
        myYear = i
        myMonth = i2
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
        dateTime = "$myDay/$myMonth/$myYear $myHour:$myMinute"
    }

    private fun setupSubmitObserver() {
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
                            findNavController().popBackStack(R.id.lmcHomeFragment,false)
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


    private fun setUpObserver() {
        viewModel.tpiListResponse.observeForever{ it ->
            if (it != null) {
                when (it.status) {
                    Status.LOADING -> {
                        setDialog(true)
                    }
                    Status.SUCCESS -> {
                        setDialog(false)
                        if (!it.data!!.error) {
                            if(args.status == "pending"){
                                tpiSubStatusMap.clear()
                                tpiStatusMap.clear()
                            }
                            tpiMap.clear()
                            lmcMeterDetails.clear()
                            lmcMeterList.clear()
                            lmcTypeList.clear()
                            lmcPropertyList.clear()
                            lmcGasType.clear()
                            lmcMeterStatusList.clear()
                            it.data.lmcList.forEach { tpi ->
                                tpiStatusMap[tpi.statusType] = tpi.id
                                tpiMap[tpi.statusType] = tpi.subStateList
                            }

                            val list = mutableListOf<String>()
                            tpiStatusMap.keys.forEach {
                                list.add(it)
                            }

                            it.data.lmcTypeList.forEach {lmcType->
                                lmcTypeList[lmcType.name] = lmcType.id
                            }

                            it.data.lmcMeterStatusList.forEach { type->
                                lmcMeterStatusList[type.name] = type.id
                            }

                            it.data.lmcMeterDetails.forEach { lmcType ->
                                lmcMeterDetails[lmcType.name] = lmcType.id
                            }

                            it.data.lmcMeterList.forEach { meter->
                                lmcMeterList[meter.name] = meter.id
                            }
                            it.data.lmcPropertyList.forEach { property->
                                lmcPropertyList[property.name] = property.id
                            }
                            it.data.lmcGasType.forEach { gas->
                                lmcGasType[gas.name] = gas.id
                            }

                            val meterStatusList = mutableListOf<String>()
                            lmcMeterStatusList.keys.forEach {
                                meterStatusList.add(it)
                            }

                            lmcSpinnerDialog = SpinnerDialog(
                                activity,
                                meterStatusList as ArrayList<String>,
                                "Select Meter Execution",
                                "Close"
                            )

                            lmcSpinnerDialog?.bindOnSpinerListener { itemNew, _ ->
                                binding.apply {
                                    spinnerLmc.text = itemNew
                                    lmcExecution = itemNew
                                }
                            }
                            statusSpinnerDialog = SpinnerDialog(
                                activity,
                                list as ArrayList<String>,
                                "Select Status Type",
                                "Close"
                            )
                            statusSpinnerDialog?.bindOnSpinerListener { item, position ->
                                binding.apply {
                                    spinnerType.text = item
                                    spinnerStatus.text = "Select Type"
                                    fsStatus = item
                                    isFailed = item.contains("failed",true)
                                    isHold = item.contains("hold",true)
                                    fsStatusCode = tpiStatusMap[fsStatus].toString()
                                    if(isFailed){
                                        btnSubmit.text = "SUBMIT"
                                        cbFollowUp.visibility = View.GONE
                                        tvFollowText.visibility = View.GONE
                                        cbFollowUp.isChecked = false
                                        tvLmcExecution.visibility = View.GONE
                                        spinnerLmc.visibility = View.GONE
                                        tvDescription.visibility = View.VISIBLE
                                        etDescription.visibility = View.VISIBLE

                                    }

                                    if(isHold){
                                        btnSubmit.text = "SUBMIT"
                                        cbFollowUp.visibility = View.VISIBLE
                                        tvFollowText.visibility = View.VISIBLE
                                        cbFollowUp.isChecked = false
                                        tvLmcExecution.visibility = View.GONE
                                        spinnerLmc.visibility = View.GONE
//                                        tvDescription.visibility = View.GONE
//                                        etDescription.visibility = View.GONE
                                    }

                                    if(item.contains("passed",true)){
                                        btnSubmit.text = "NEXT"
                                        tvLmcExecution.visibility = View.VISIBLE
                                        spinnerLmc.visibility = View.VISIBLE
                                        spinnerLmc.text = "Select LMC"
                                        cbFollowUp.visibility = View.GONE
                                        tvFollowText.visibility = View.GONE
                                        tvDescription.visibility = View.GONE
                                        etDescription.visibility = View.GONE
                                        cbFollowUp.isChecked = false

                                    }
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
                                        fsSubStatus = itemNew
                                        fsSubStatusCode = tpiSubStatusMap[fsSubStatus].toString()

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

    private fun toggleFollowUpVisibility(value: Boolean) {
        binding.apply {
            if (value) {
                tvDescription.visibility = View.VISIBLE
                etDescription.visibility = View.VISIBLE
//                tvAttachments.visibility = View.VISIBLE
//                rvAttachment.visibility = View.VISIBLE
//                btnUpload.visibility = View.VISIBLE
                tvDateTime.visibility = View.VISIBLE
                tvFollowTitle.visibility = View.VISIBLE
            } else {
                tvDescription.visibility = View.GONE
                etDescription.visibility = View.GONE
                btnUpload.visibility = View.GONE
                tvAttachments.visibility = View.GONE
                rvAttachment.visibility = View.GONE
                tvDateTime.visibility = View.GONE
                tvFollowTitle.visibility = View.GONE
            }
        }

    }

    private fun setDialog(show: Boolean) {
        if (show) dialog!!.show() else dialog!!.dismiss()
    }

}