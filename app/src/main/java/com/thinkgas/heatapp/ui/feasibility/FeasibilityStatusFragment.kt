package com.thinkgas.heatapp.ui.feasibility

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
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
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
import com.thinkgas.heatapp.data.remote.model.SubState
import com.thinkgas.heatapp.data.remote.model.UploadRequestModel
import com.thinkgas.heatapp.databinding.CommentDialogBinding
import com.thinkgas.heatapp.databinding.FragmentFeasibilityStatusBinding
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
class FeasibilityStatusFragment : Fragment() {

    private var _binding: FragmentFeasibilityStatusBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<FeasibilityStatusViewModel>()
    private val args by navArgs<FeasibilityStatusFragmentArgs>()
    var statusSpinnerDialog: SpinnerDialog? = null
    var subStatusSpinner: SpinnerDialog? = null
    var pipelineSpinner: SpinnerDialog? = null
    var riserSpinner: SpinnerDialog? = null
    var gcSpinner: SpinnerDialog? = null
    private var dialog: Dialog? = null
    private var tpiMap: MutableMap<String, List<SubState>> = mutableMapOf()
    private var tpiStatusMap: MutableMap<String, Int> = mutableMapOf()
    private var tpiSubStatusMap: MutableMap<String, Int> = mutableMapOf()
    private var tpiPipelineMap: MutableMap<String, Int> = mutableMapOf()
    private var tpiRiserMap: MutableMap<String, Int> = mutableMapOf()
    private var tpiSubStatus: MutableMap<String, Int> = mutableMapOf()
    private var tfStatus: MutableMap<Int, String> = mutableMapOf()
    private var gcStatus: MutableMap<String, Int> = mutableMapOf()
    private lateinit var cameraPermissionSetting: ActivityResultLauncher<Intent>
    private var mUri: Uri? = null
    private lateinit var cameraActions: ActivityResultLauncher<Intent>
    lateinit var currentPhotoPath: String
    private var fsStatus: String? = null
    private var tfStatusType:String? = null
    private var fsStatusCode: String? = null
    private var fsSubStatus: String? = null
    private var fsSubStatusCode: String? = null
    private var fsPipeline: String? = null
    private var fsPipelineCode: String? = null
    private var fsRiser: String? = null
    private var fsGC: String? = null
    private var fsSrNo: String? = null
    private var drsNo: String? = null
    private var giPipeLength: String? = null
    private var mlcpipeLength: String? = null
    private var fileList: java.util.ArrayList<File>? = ArrayList()
    private var photoURI: Uri? = null
    private var imageDialog: Dialog? = null
    private var imageLayout: LayoutViewImageBinding? = null
    private var imageType: FsImageType? = null
    private var attachmentCount = 0
    private var isometricDrawingCount = 0
    private var isFailed = false
    private var isPassed = false
    private var isHold = false

//    private lateinit var viewAttachmentAdapter: ViewAttachmentAdapter
    private var isometricAdapter: ViewAttachmentAdapter? = null



    companion object {
        var clickFlag = false
        var isometricFlag = false
    }

    enum class FsImageType{
        ISOMETRIC,
        ATTACHMENT,
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFeasibilityStatusBinding.inflate(inflater, container, false)
        binding.apply {
            tvName.text = args.customerName
            tvAddress.text = args.address
            tvMobile.text = args.mobile
            tvAppNo.text = args.appNo
            tvBpNo.text = args.bpNo

            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setView(R.layout.progress)
            dialog = builder.create()

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
                    val directions = FeasibilityStatusFragmentDirections.actionFeasibilityStatusFragmentToLoginFragment()
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
                        tfTitle.text = "Supervisor Feasibility Claimed"
                    }else {
                        tfTitle.text = "Feasibility Pending"
                    }
                }
                "hold"->{
                    if(AppCache.isTpi){
                        tfTitle.text = "Supervisor Feasibility Hold"
                    }else {
                        tfTitle.text = "Feasibility Hold"
                    }
                }
                "done"->{
                    if(AppCache.isTpi){
                        tfTitle.text = "Feasibility Approval Pending"
                    }else {
                        tfTitle.text = "Feasibility Done"
                    }
                }
                "unclaimed"->{
                    if(AppCache.isTpi){
                        tfTitle.text = "Supervisor Feasibility Unclaimed"
                    }else {
                        tfTitle.text = "Feasibility Unclaimed"
                    }

                }
                "failed"->{
                    tfTitle.text = "Feasibility Failed"
                }
                "approved"->{
                    tfTitle.text = "Feasibility Approved"
                }

                "declined"->{
                    tfTitle.text = "Feasibility Declined"
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
            imageDialog!!.setContentView(imageLayout!!.root)

//            ivAttachments.setOnClickListener {
//                imageType = FsImageType.ATTACHMENT
//                requestCameraPermission.launch(
//                    Manifest.permission.CAMERA
//                )
//            }

            ivIsometric.setOnClickListener {
                imageType = FsImageType.ISOMETRIC
                requestCameraPermission.launch(
                    Manifest.permission.CAMERA
                )
            }

            if(AppCache.isTpi){
//                ivAttachments.visibility = View.GONE
                ivIsometric.visibility = View.GONE
                btnSubmit.visibility = View.GONE
                if(args.status == "done" || args.status == "failed") {
                    btnApprove.visibility = View.VISIBLE
                    btnDecline.visibility = View.VISIBLE
                }
                spinnerType.isEnabled = false
                spinnerType.setTextColor(Color.parseColor("#545454"))
                spinnerStatus.isEnabled = false
                spinnerStatus.setTextColor(Color.parseColor("#545454"))
                spinnerPipeline.isEnabled = false
                spinnerPipeline.setTextColor(Color.parseColor("#545454"))
                spinnerRiser.isEnabled = false
                spinnerRiser.setTextColor(Color.parseColor("#545454"))
                etDescription.isEnabled = false
                etPipeLength.isEnabled = false
                etMlcLength.isEnabled = false
//                etDrsNo.isEnabled = false
//                etSrNo.isEnabled = false
            }

            ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }

            btnApprove.setOnClickListener {
                submitFeasibilityApproval("Approved")
            }

            btnDecline.setOnClickListener {
                submitFeasibilityApproval("Decline")
            }


            btnSubmit.setOnClickListener {
                if (fsStatus.isNullOrBlank()) {
                    spinnerType.error = "Select spinner type"
                    spinnerType.requestFocus()
                    return@setOnClickListener
                }
                if (fsSubStatus.isNullOrBlank()) {
                    spinnerStatus.error = "Select spinner status"
                    spinnerStatus.requestFocus()
                    return@setOnClickListener
                }

                if (isHold) {
                    if (tvDateTime.text.isNullOrEmpty() || tvDateTime.text.equals("Select Date & Time")) {
                        tvDateTime.error = "Select Date & Time"
                        tvDateTime.requestFocus()
                        return@setOnClickListener
                    }
                }

                fsStatusCode = tpiStatusMap[fsStatus].toString()
                fsSubStatusCode = tpiSubStatusMap[fsSubStatus].toString()
                fsPipelineCode = tpiPipelineMap[fsPipeline].toString()

                val params = HashMap<String, String>()

                if(!isFailed){
                    if (spinnerPipeline.isVisible && fsPipeline.isNullOrBlank()) {
                        spinnerPipeline.error = "Select any pipeline"
                        spinnerPipeline.requestFocus()
                        return@setOnClickListener
                    }
                    if (spinnerRiser.isVisible && fsRiser.isNullOrBlank()) {
                        spinnerRiser.error = "Select riser"
                        spinnerRiser.requestFocus()
                        return@setOnClickListener
                    }

                    if(fsRiser == "Yes"){

                        if(etRiserLength.text.isNullOrBlank()){
                            etRiserLength.error = "Enter riser length"
                            etRiserLength.requestFocus()
                            return@setOnClickListener
                        }


                    }

                    if(etPipeLength.isVisible && etPipeLength.text.isNullOrBlank()){
                        etPipeLength.error = "Enter GI pipelength"
                        etPipeLength.requestFocus()
                        return@setOnClickListener
                    }

                    if(etMlcLength.isVisible && etMlcLength.text.isNullOrBlank()){
                        etMlcLength.error = "Enter MLC length"
                        etMlcLength.requestFocus()
                        return@setOnClickListener
                    }

                    if(spinnerGc.isVisible && fsGC.isNullOrBlank()){
                        spinnerGc.error = " Select GC status"
                        spinnerGc.requestFocus()
                        return@setOnClickListener
                    }

//                    if(etDrsNo.text.isBlank()){
//                        etDrsNo.error = "Enter DRS Number"
//                        etDrsNo.requestFocus()
//                        return@setOnClickListener
//                    }

//                    if(etSrNo.text.isBlank()){
//                        etSrNo.error = "Enter SR Number"
//                        etSrNo.requestFocus()
//                        return@setOnClickListener
//                    }

//                if(attachmentCount == 0){
//                    tvAttachments.error = "Attachment required"
//                    tvAttachments.requestFocus()
//                    return@setOnClickListener
//                }

                    if(tvIsometric.isVisible && isometricDrawingCount == 0){
                        tvIsometric.error = "Isometric drawing required"
                        tvIsometric.requestFocus()
                        return@setOnClickListener
                    }

                    params["application_number"] = args.appNo
                    params["bp_number"] = args.bpNo
                    params["customer_info"] = args.customerName
                    params["status_type_id"] = fsStatusCode!!
                    params["status_type"] = fsStatus!!
                    params["sub_status_id"] = fsSubStatusCode!!
                    params["sub_status"] = fsSubStatus!!
                    params["pipeline_category_id"] = fsPipelineCode!!
                    params["pipeline_category"] = fsPipeline!!
                    params["description"] = etDescription.text.toString()
                    params["fs_session_id"] = args.sessionId
//                    params["sr_no"] = etSrNo.text.toString()
//                    params["drs_no"] = etDrsNo.text.toString()
                    params["mlc_pipelength"] = etMlcLength.text.toString()
                    params["approval_status"] = "Nil"
                    params["comments"] = ""
                    params["riser_status"]=fsRiser!!
                    params["riser_length"] = etRiserLength.text.toString()
                    params["gi_pipelength"] = etPipeLength.text.toString()
                    params["gc_status"] = fsGC!!
                    params["follow_up_date"] = tvDateTime.text.toString()
                }
                else{
                    if (etDescription.text.isNullOrBlank()){
                        etDescription.error = "Enter comments"
                        etDescription.requestFocus()
                        return@setOnClickListener
                    }

                    params["application_number"] = args.appNo
                    params["bp_number"] = args.bpNo
                    params["customer_info"] = args.customerName
                    params["status_type_id"] = fsStatusCode!!
                    params["status_type"] = fsStatus!!
                    params["sub_status_id"] = fsSubStatusCode!!
                    params["sub_status"] = fsSubStatus!!
//                    params["pipeline_category_id"] = fsPipelineCode!!
//                    params["pipeline_category"] = fsPipeline!!
                    params["description"] = etDescription.text.toString()
                    params["fs_session_id"] = args.sessionId
                    params["approval_status"] = "Nil"
                    params["comments"] = ""
                    params["follow_up_date"] = tvDateTime.text.toString()
//                    params["riser_status"]=fsRiser!!
//                    params["riser_length"] = etRiserLength.text.toString()
//                    params["gi_pipelength"] = etPipeLength.text.toString()
//                    params["gc_status"] = fsGC!!
                }

                viewModel.submitFeasibility(params)

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

            spinnerPipeline.setOnClickListener {
                pipelineSpinner?.showSpinerDialog()
                pipelineSpinner?.setCancellable(false)
                pipelineSpinner?.setShowKeyboard(false)
            }
            spinnerRiser.setOnClickListener {
                riserSpinner?.showSpinerDialog()
                riserSpinner?.setCancellable(false)
                riserSpinner?.setShowKeyboard(false)
            }

            spinnerGc.setOnClickListener {
                gcSpinner?.showSpinerDialog()
                gcSpinner?.setCancellable(false)
                gcSpinner?.setShowKeyboard(false)
            }

        }

        val params = HashMap<String, String>()
        params["version_code"] = "1"
        params["os_type"] = "android"
        params["mobile_no"] = args.mobile

        viewModel.getTpiListTypes(params)

        setUpObserver()

        getAttachmentList()



        viewModel.feasibilityResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it.status) {
                    Status.LOADING -> {
                        setDialog(true)
                    }
                    Status.SUCCESS -> {
                        setDialog(false)
                        if (!it.data!!.error) {
                            Toast.makeText(
                                requireContext(),
                                it.data.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().popBackStack(R.id.feasibilityHomeFragment,false)
                        } else {
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT)
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

        val downDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_down_24)
        val upDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_up_24)

        binding.apply {
//            tvAttachments.setOnClickListener {
//                if (!clickFlag) {
//                    rvAttachment.visibility = View.GONE
//                    tvAttachments.setCompoundDrawablesWithIntrinsicBounds(
//                        null,
//                        null,
//                        downDrawable,
//                        null
//                    )
//                    clickFlag = !clickFlag
//                } else {
//                    rvAttachment.visibility = View.VISIBLE
//                    tvAttachments.setCompoundDrawablesWithIntrinsicBounds(
//                        null,
//                        null,
//                        upDrawable,
//                        null
//                    )
//                    clickFlag = !clickFlag
//                }
//            }
            tvIsometric.setOnClickListener {
                if (!isometricFlag) {
                    rvIsometric.visibility = View.GONE
                    tvIsometric.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        downDrawable,
                        null
                    )
                    isometricFlag = !isometricFlag
                } else {
                    rvIsometric.visibility = View.VISIBLE
                    tvIsometric.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        upDrawable,
                        null
                    )
                    isometricFlag = !isometricFlag
                }
            }

        }
        return binding.root
    }

    private fun getAttachmentList() {
        val viewParams = HashMap<String,String>()
        viewParams["bp_number"] = args.bpNo
        viewParams["application_number"] = args.appNo
        viewParams["session_id"] = args.sessionId
        viewParams["type"] = Constants.FS_FILE_ATTACHMENT

        viewModel.viewAttachments(viewParams)

        val isometricParams = HashMap<String,String>()
        isometricParams["bp_number"] = args.bpNo
        isometricParams["application_number"] = args.appNo
        isometricParams["session_id"] = args.sessionId
        isometricParams["type"] = Constants.TFS_FILE_ISOMETRIC_ATTACHMENT

        viewModel.viewAttachments(isometricParams)
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
                                Constants.TFS_FILE_ISOMETRIC_ATTACHMENT->{
                                    isometricAdapter = ViewAttachmentAdapter(
                                        requireContext(),
                                        it.data.attachmentList,
                                        {attachment ->  attachmentItemClicked(attachment) },
                                        {attachment ->  deleteItemClicked(attachment) }
                                    )
                                    binding.rvIsometric.adapter = isometricAdapter
                                    isometricAdapter!!.notifyDataSetChanged()
                                    binding.tvIsometric.error = null
                                    binding.tvIsometric.text = "Isometric Drawing (${isometricAdapter!!.itemCount})"
                                    isometricDrawingCount = isometricAdapter!!.itemCount

                                }
//                                Constants.FS_FILE_ATTACHMENT->{
//                                    viewAttachmentAdapter = ViewAttachmentAdapter(
//                                        requireContext(),
//                                        it.data.attachmentList,
//                                        {attachment ->  attachmentItemClicked(attachment) },
//                                        {attachment ->  deleteItemClicked(attachment) }
//                                    )
//                                    binding.rvAttachment.adapter = viewAttachmentAdapter
//                                    viewAttachmentAdapter.notifyDataSetChanged()
//                                    binding.tvAttachments.error = null
//                                    binding.tvAttachments.text = "Attachments (${viewAttachmentAdapter.itemCount})"
//                                    attachmentCount = viewAttachmentAdapter.itemCount
//                                }
                                else -> {}
                            }

                            if(isometricAdapter == null){
                                binding.tvIsometric.text = "Isometric Drawing (0)"
                                isometricDrawingCount = 0
                            }

                        }else{

                            if(isometricAdapter == null){
                                binding.tvIsometric.text = "Isometric Drawing (0)"
                                isometricDrawingCount = 0
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
                                        binding.rvIsometric.adapter = null
                                        isometricAdapter = null
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

                            it.data.gcStatusList.forEach { gc->
                                gcStatus[gc.gcStatus] = gc.id
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
                            it.data.pipelineList.forEach { pipeline ->
                                tpiPipelineMap[pipeline.pipelineCategory] = pipeline.id
                            }
                            it.data.riserList.forEach { riser ->
                                tpiRiserMap[riser.riserCategory] = riser.id
                            }

                            val pipeLineList = mutableListOf<String>()
                            tpiPipelineMap.keys.forEach {
                                pipeLineList.add(it)
                            }

                            val riserList = mutableListOf<String>()
                            tpiRiserMap.keys.forEach {
                                riserList.add(it)
                            }

//                            val gcList = arrayListOf<String>("GC-1","GC-2","GC-3","GC-4","GC-5")
                            val gcList = mutableListOf<String>()

                            gcStatus.keys.forEach {
                                gcList.add(it)
                            }
                            gcSpinner = SpinnerDialog(
                                activity,
                                gcList as java.util.ArrayList<String>,
                                "Select GC",
                                "Close"
                            )

                            pipelineSpinner = SpinnerDialog(
                                activity,
                                pipeLineList as java.util.ArrayList<String>,
                                "Select Category",
                                "Close"
                            )

                            riserSpinner = SpinnerDialog(
                                activity,
                                riserList as java.util.ArrayList<String>,
                                "Select Riser",
                                "Close"
                            )

                            pipelineSpinner?.bindOnSpinerListener { pipeline, _ ->
                                binding.apply {
                                    spinnerPipeline.text = pipeline
                                    spinnerPipeline.error = null

                                    fsPipeline = pipeline
                                }
                            }
                            riserSpinner?.bindOnSpinerListener { riser, _ ->
                                binding.apply {
                                    spinnerRiser.text = riser
                                    spinnerRiser.error = null

                                    fsRiser = riser

                                    if(riser == "Yes"){
                                        tvRiserLength.visibility = View.VISIBLE
                                        etRiserLength.visibility = View.VISIBLE
                                    }else{
                                        tvRiserLength.visibility = View.GONE
                                        etRiserLength.visibility = View.GONE
                                    }
                                }
                            }

                            gcSpinner?.bindOnSpinerListener { gc, position ->
                                binding.apply {
                                    spinnerGc.text = gc
                                    spinnerGc.error = null
                                    fsGC = gc
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

                                when (status.toString().toLowerCase()) {
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
                                    spinnerStatus.text = "Select sub status"
                                    spinnerStatus.error = null
                                    fsSubStatus = null

                                    if(status!!.contains("failed",true) || status.contains("hold",true)){
                                        toggleVisibility(View.GONE)
                                        if (status.contains("hold",true)) {
                                            tvFollowTitle.visibility = View.VISIBLE
                                            tvDateTime.visibility = View.VISIBLE
                                            tvDateTime.isEnabled = true
                                            tvDateTime.isClickable = true
                                        } else {
                                            tvFollowTitle.visibility = View.GONE
                                            tvDateTime.visibility = View.GONE
                                        }
                                        tvDescription.visibility = View.VISIBLE
                                        etDescription.visibility = View.VISIBLE
                                        tvRiserLength.visibility = View.GONE
                                        etRiserLength.visibility = View.GONE
                                        isFailed = true
                                    }else{
                                        toggleVisibility(View.VISIBLE)

                                        if (status!!.contains("done", true)) {
                                            tvDescription.visibility = View.GONE
                                            etDescription.visibility = View.GONE
                                            tvFollowTitle.visibility = View.GONE
                                            tvDateTime.visibility = View.GONE
                                        } else {
                                            tvDescription.visibility = View.VISIBLE
                                            etDescription.visibility = View.VISIBLE
                                        }

                                        if (status!!.contains("failed",true)){
                                            tvFollowTitle.visibility = View.GONE
                                            tvDateTime.visibility = View.GONE
                                        }

                                        if(fsRiser == "Yes"){
                                            tvRiserLength.visibility = View.VISIBLE
                                            etRiserLength.visibility = View.VISIBLE
                                        }else{
                                            tvRiserLength.visibility = View.GONE
                                            etRiserLength.visibility = View.GONE
                                        }
                                        isFailed = false
                                    }
                                    spinnerType.text = item
                                    spinnerType.error = null
                                    spinnerStatus.text = "Select status type"
                                    fsStatus = item

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
                                        fsSubStatus = itemNew
                                    }
                                }
                            }

                            if(args.status == "hold" || args.status == "done" || args.status == "failed"){
                                val statusType = tpiStatusMap.entries.find { it.value == args.statusTypeId }?.key
                                val subStatus = tpiSubStatus.entries.find { it.value == args.subStatusId }?.key
                                val pipeline = tpiPipelineMap.entries.find { it.value == args.pipeLineId }?.key

                                when (args.status.toLowerCase()) {
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
                                    if(subStatus!=null){
                                        spinnerStatus.text = subStatus
                                    }
                                    if(pipeline!=null){
                                        spinnerPipeline.text = pipeline
                                    }
                                    if(args.riserStatus!=null){
                                        spinnerRiser.text = args.riserStatus
                                    }
                                    etDescription.setText(args.description)
                                    etRiserLength.setText(args.riserLength)
                                    etPipeLength.setText(args.pipeLength)
                                    etMlcLength.setText(args.mlcLength)
//                                    etSrNo.setText(args.srNo)
//                                    etDrsNo.setText(args.drsNo)
                                    if(args.gcStatus!=null){
                                        spinnerGc.text = args.gcStatus
                                    }
                                }
                                fsStatusCode = args.statusTypeId.toString()
                                fsStatus=statusType
                                fsSubStatus=subStatus
                                fsSubStatusCode=args.subStatusId.toString()
                                fsPipelineCode=args.pipeLineId.toString()
                                fsPipeline = pipeline
                                fsRiser = args.riserStatus
                                fsGC = args.gcStatus

                                if(args.riserStatus == "Yes"){
                                    binding.tvRiserLength.visibility = View.VISIBLE
                                    binding.etRiserLength.visibility = View.VISIBLE
                                }

                                if(args.status == "failed"){
                                    toggleVisibility(View.GONE)
                                    isFailed = true
                                    binding.apply {
                                        tvFollowTitle.visibility = View.GONE
                                        tvDateTime.visibility = View.GONE
                                        tvDescription.visibility = View.VISIBLE
                                        etDescription.visibility = View.VISIBLE
                                    }
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
                                        fsSubStatus = itemNew
                                    }
                                }

                                if (args.status == "hold") {
                                    binding.apply {
                                        toggleVisibility(View.GONE)
                                        tvFollowTitle.visibility = View.VISIBLE
                                        tvDateTime.visibility = View.VISIBLE
                                        tvDateTime.isEnabled = true
                                        tvDateTime.isClickable = true
                                        tvDescription.visibility = View.VISIBLE
                                        etDescription.visibility = View.VISIBLE
                                        tvRiserLength.visibility = View.GONE
                                        etRiserLength.visibility = View.GONE
                                        isFailed = true

//                                        if(args.followUpDate.toString() != "null") {
                                            tvDateTime.text = args.followUpDate.toString()
                                            tvDateTime.isEnabled = true
//                                        }
                                    }
                                }

                                if (args.status == "done") {
                                    binding.apply {
                                        tvDescription.visibility = View.GONE
                                        etDescription.visibility = View.GONE
                                        tvFollowTitle.visibility = View.GONE
                                        tvDateTime.visibility = View.GONE
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
//                        var bitmap = getBitmap(it.data?.data!!.path!!)
//                        binding.ivProfile.setImageBitmap(bitmap)



                        if (requireActivity().contentResolver.getFileSize(uri)
                                .toLong() / 1024 < 5120
                        ) {
                            val fileList = ArrayList<File>()
                            fileList.add(file)

                            val request=UploadRequestModel(
                                bpNumber = args.bpNo, appNo = args.appNo, sessionId = args.sessionId
                            )
                            when(imageType){
                                FsImageType.ATTACHMENT->{
                                    viewModel.uploadAttachment(request,file,Constants.FS_FILE_ATTACHMENT_TYPE)
                                }
                                FsImageType.ISOMETRIC->{
                                    viewModel.uploadAttachment(request,file,Constants.TFS_FILE_ISOMETRIC_ATTACHMENT_TYPE)

                                }
                                else -> {}
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

    private fun submitFeasibilityApproval(status:String){
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
                setUploadObserver(dialogBinding)
                viewModel.uploadAttachment(request,file, Constants.FS_TPI_SIGNATURE_FILE)
            }
            btnSubmit.setOnClickListener {
                if(signature.isEnabled){
                    Toast.makeText(requireContext(), "Signature is needed", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val params = HashMap<String, String>()
                params["application_number"] = args.appNo
                params["bp_number"] = args.bpNo
                params["customer_info"] = args.customerName
                params["status_type_id"] = fsStatusCode!!
                params["status_type"] = fsStatus!!
                params["sub_status_id"] = fsSubStatusCode!!
                params["sub_status"] = fsSubStatus!!
//                params["pipeline_category_id"] = fsPipelineCode!!
//                params["pipeline_category"] = fsPipeline!!
//                params["description"] = args.description.toString()
                params["fs_session_id"] = args.sessionId
                params["approval_status"] = status
                params["comments"] = etComment.text.toString()
                viewModel.submitFeasibility(params)

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
                        Toast.makeText(requireContext(), it.data!!.message, Toast.LENGTH_SHORT).show()
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


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("ddMMyyyy_HHmmss").format(Date())
        val storageDir: File? =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
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


    fun toggleVisibility(visibility:Int){
        binding.apply {
            tvPipeline.visibility = visibility
            spinnerPipeline.visibility = visibility
            tvRiser.visibility = visibility
            spinnerRiser.visibility = visibility
            tvPipeLength.visibility = visibility
            etPipeLength.visibility = visibility
            tvMlcLength.visibility = visibility
            etMlcLength.visibility = visibility
            tvGc.visibility =visibility
            spinnerGc.visibility = visibility
//            tvDrsNo.visibility = visibility
//            etDrsNo.visibility = visibility
//            tvSrNo.visibility = visibility
//            etSrNo.visibility = visibility
            tvIsometric.visibility = visibility
            rvIsometric.visibility = visibility
            ivIsometric.visibility = visibility
        }
    }


    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }

    private fun setDialog(show: Boolean) {
        if (show) dialog!!.show() else dialog!!.dismiss()
    }

}