package com.thinkgas.heatapp.ui.lmc

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
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
import com.thinkgas.heatapp.data.remote.model.Attachment
import com.thinkgas.heatapp.data.remote.model.UploadRequestModel
import com.thinkgas.heatapp.databinding.CommentDialogBinding
import com.thinkgas.heatapp.databinding.FragmentLmcFinalBinding
import com.thinkgas.heatapp.databinding.LayoutViewImageBinding
import com.thinkgas.heatapp.ui.common.adapters.ViewAttachmentAdapter
import com.thinkgas.heatapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@AndroidEntryPoint
class LmcFinalFragment : Fragment() {

    private var _binding: FragmentLmcFinalBinding? = null
    private val binding get() = _binding!!
    private val lmcStatusViewModel by viewModels<LmcStatusViewModel>()
    private val args by navArgs<LmcFinalFragmentArgs>()

    var pvcAvail:Int = 1
    var meterAvail:Int = 1
    var clampingAvail:Int = 1
    var gasAvail:Int = 1
    var cohAvail:Int = 1
    var paintingAvail:Int = 1
    var tfAvail:Int = 1
    var connectivityAvail:Int = 1
    var ecAvail:Int = 1
    var agAvail:Int = 1
    var csOne:Int = 1
    var csTwo:Int = 1
    var holeDrilled:Int = 1
    var mcvTesting:Int = 1
    var acknowledgeStatus = 1
    private var hasAcknowledged = true
    var acTape:Int = 1

    private var photoURI: Uri? = null
    private var imageDialog: Dialog? = null
    private var imageLayout: LayoutViewImageBinding? = null

    private lateinit var cameraPermissionSetting: ActivityResultLauncher<Intent>
    private lateinit var cameraActions: ActivityResultLauncher<Intent>
    lateinit var currentPhotoPath: String

    private  var isometricAdapter: ViewAttachmentAdapter? = null
    private  var installationAdapter: ViewAttachmentAdapter? = null
    private  var drawingAdapter: ViewAttachmentAdapter? = null
//    private lateinit var selfieAdapter: ViewAttachmentAdapter
    private  var signatureAdapter: ViewAttachmentAdapter? = null

    private var installationCount = 0
    private var isometricDrawingCount = 0
    private var drawingCount = 0
//    private var selfieCount = 0
    private var signatureCount = 0
    private var dialog: Dialog? = null

    private var imageType: ImageType? = null

    companion object {
        var isometricFlag = false
        var installationFlag = false
        var drawingFlag = false
//        var selfieFlag = false
    }

    enum class ImageType{
        ISOMETRIC,
        INSTALLATION,
        DRAWING,
        SELFIE,
        SIGNATURE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLmcFinalBinding.inflate(inflater, container, false)
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.progress)
        dialog = builder.create()
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
                    val directions = LmcFinalFragmentDirections.actionLmcFinalFragmentToLoginFragment()
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

            rgPvc.setOnCheckedChangeListener { radioGroup, i ->
                pvcAvail = if(rbPvcAvail.id == i) 1 else 0
            }

            rgTape.setOnCheckedChangeListener { radioGroup, i ->
                acTape = if(rbTapeYes.id == i) 1 else 0
            }

            rgMi.setOnCheckedChangeListener { radioGroup, i ->
                meterAvail = if(rbMiAvail.id == i) 1 else 0
            }

            rgClamping.setOnCheckedChangeListener { radioGroup, i ->
                clampingAvail = if(rbClampingDone.id == i) 1 else 0
            }
            rgGmt.setOnCheckedChangeListener { radioGroup, i ->
                gasAvail = if(rbGmtDone.id == i) 1 else 0
            }
            rgCoh.setOnCheckedChangeListener { radioGroup, i ->
                cohAvail = if(rbCohDone.id == i) 1 else 0
            }
            rgPainting.setOnCheckedChangeListener { radioGroup, i ->
                paintingAvail = if(rbPaintingDone.id == i) 1 else 0
            }
            rgTf.setOnCheckedChangeListener { radioGroup, i ->
                tfAvail = if(rbTfDone.id == i) 1 else 0
            }
            rgConnectivity.setOnCheckedChangeListener { radioGroup, i ->
                connectivityAvail = if(rbConnectivityDone.id == i) 1 else 0
            }
            rgEc.setOnCheckedChangeListener { radioGroup, i ->
                ecAvail = if(rbEcYes.id == i) 1 else 0
            }
            rgAg.setOnCheckedChangeListener { radioGroup, i ->
                agAvail = if(rbAgYes.id == i) 1 else 0
            }
            rgHd.setOnCheckedChangeListener { radioGroup, i ->
                holeDrilled = if(rbHdYes.id == i) 1 else 0
            }
            rgMcv.setOnCheckedChangeListener { radioGroup, i ->
                mcvTesting = if(rbMcvYes.id == i) 1 else 0
            }
//            rgCsOne.setOnCheckedChangeListener { radioGroup, i ->
//                csOne = if(rbCsOneYes.id == i) 1 else 0
//            }
//            rgCsTwo.setOnCheckedChangeListener { radioGroup, i ->
//                csTwo = if(rbCsTwoYes.id == i) 1 else 0
//            }

            if(args.status == "hold" || args.status == "done"){
                if(args.tfAvail != null && args.tfAvail != "-1"){
                    tfAvail = args.tfAvail!!.toInt()
                }
                if(args.holeDrilled != null  && args.holeDrilled != "-1"){
                    holeDrilled = args.holeDrilled!!.toInt()
                }
                if(args.mcvTesting != null && args.mcvTesting != "-1"){
                    mcvTesting = args.mcvTesting!!.toInt()
                }
                if(args.acTape != null && args.acTape != "-1"){
                    acTape = args.acTape!!.toInt()
                }
                args.apply {
                    if(pvcSleeve != null && pvcSleeve != "-1"){
                        pvcAvail = pvcSleeve!!.toInt()
                    }

                    if(meterInstallation != null && meterInstallation != "-1"){
                        meterAvail = meterInstallation!!.toInt()
                    }

                    if(clamping != null && clamping != "-1"){
                        clampingAvail = clamping!!.toInt()
                    }

                    if(gmTesting != null && gmTesting != "-1"){
                        gasAvail = gmTesting!!.toInt()
                    }

                    if(coh != null && coh != "-1"){
                        cohAvail = coh!!.toInt()
                    }

                    if(painting != null &&  painting != "-1"){
                        paintingAvail = painting!!.toInt()
                    }

                    if(connectivity != null && connectivity != "-1"){
                        connectivityAvail = connectivity!!.toInt()
                    }

                    if(endCap != null && endCap != "-1"){
                        ecAvail = endCap!!.toInt()
                    }

                    if(areagassified != null && areagassified != "-1"){
                        agAvail = areagassified!!.toInt()
                    }
//                    if(custStatus != null && custStatus != "-1"){
//                        csOne = custStatus!!.toInt()
//                    }
//                    if(convStatus != null && convStatus != "-1"){
//                        csTwo = convStatus!!.toInt()
//                    }

                }

                if(args.pvcSleeve == "0"){
                    rgPvc.check(rgPvc.getChildAt(1).id)
                } else {
                    rgPvc.check(rgPvc.getChildAt(0).id)
                }

                if(args.meterInstallation == "0")  rgMi.check(rgMi.getChildAt(1).id) else rgMi.check(rgMi.getChildAt(0).id)

                if(args.clamping == "0")  rgClamping.check(rgClamping.getChildAt(1).id) else rgClamping.check(rgClamping.getChildAt(0).id)

                if(args.gmTesting == "0")  rgGmt.check(rgGmt.getChildAt(1).id) else rgGmt.check(rgGmt.getChildAt(0).id)

                if(args.coh == "0")  rgCoh.check(rgCoh.getChildAt(1).id) else rgCoh.check(rgCoh.getChildAt(0).id)

                if(args.painting == "0")  rgPainting.check(rgPainting.getChildAt(1).id) else rgPainting.check(rgPainting.getChildAt(0).id)

                if(args.tfAvail == "0")  rgTf.check(rgTf.getChildAt(1).id) else rgTf.check(rgTf.getChildAt(0).id)

                if(args.connectivity == "0")  rgConnectivity.check(rgConnectivity.getChildAt(1).id) else rgConnectivity.check(rgConnectivity.getChildAt(0).id)

                if(args.endCap == "0")  rgEc.check(rgEc.getChildAt(1).id) else rgEc.check(rgEc.getChildAt(0).id)

                if(args.areagassified == "0")  rgAg.check(rgAg.getChildAt(1).id) else rgAg.check(rgAg.getChildAt(0).id)

                if(args.holeDrilled == "0")  rgHd.check(rgHd.getChildAt(1).id) else rgHd.check(rgHd.getChildAt(0).id)

                if(args.mcvTesting == "0")  rgMcv.check(rgMcv.getChildAt(1).id) else rgMcv.check(rgMcv.getChildAt(0).id)

//                if(args.custStatus == "0")  rgCsOne.check(rgCsOne.getChildAt(1).id) else rgCsOne.check(rgCsOne.getChildAt(0).id)
//
//                if(args.convStatus == "0")  rgCsTwo.check(rgCsTwo.getChildAt(1).id) else rgCsTwo.check(rgCsTwo.getChildAt(0).id)

                if(args.acTape == "0")  rgTape.check(rgTape.getChildAt(1).id) else rgTape.check(rgTape.getChildAt(0).id)

                if(args.confirmStatus == "1") {
                    cbAcknowledge.isChecked = true
                    hasAcknowledged = true
                }
            }

            if(AppCache.isTpi){
                btnSubmit.visibility = View.GONE
                btnApprove.visibility = View.VISIBLE
                btnDecline.visibility = View.VISIBLE
                signature.visibility = View.GONE
                btnSignature.visibility = View.GONE
                btnClear.visibility = View.GONE
                ivDrawing.visibility = View.GONE
                ivIsometric.visibility = View.GONE
                ivInstallation.visibility = View.GONE
//                ivSelfie.visibility = View.GONE
//                val params = signature.layoutParams
//                params.width = ViewGroup.LayoutParams.MATCH_PARENT
//                params.height = ViewGroup.LayoutParams.MATCH_PARENT
                cbAcknowledge.visibility = View.GONE
                tvAcknowledge.visibility = View.GONE
                rbPvcAvail.isEnabled = false
                rbPvcNa.isEnabled = false

                rbMiAvail.isEnabled = false
                rbMiNa.isEnabled = false

                rbClampingDone.isEnabled = false
                rbClampingNd.isEnabled = false

                rbGmtDone.isEnabled = false
                rbGmtNd.isEnabled = false

                rbCohDone.isEnabled = false
                rbCohNd.isEnabled = false

                rbPaintingDone.isEnabled = false
                rbPaintingNd.isEnabled = false

                rbTfDone.isEnabled = false
                rbTfNd.isEnabled = false

                rbConnectivityDone.isEnabled = false
                rbConnectivityNd.isEnabled = false

                rbEcYes.isEnabled = false
                rbEcNo.isEnabled = false

                rbAgYes.isEnabled = false
                rbAgNo.isEnabled = false

                rbHdNo.isEnabled = false
                rbHdYes.isEnabled = false

                rbMcvYes.isEnabled = false
                rbMcvNo.isEnabled = false

                rbTapeYes.isEnabled = false
                rbTapeNo.isEnabled = false

//                rbCsOneYes.isEnabled = false
//                rbCsOneNo.isEnabled = false
//
//                rbCsTwoYes.isEnabled = false
//                rbCsTwoNo.isEnabled = false

            }

            btnApprove.setOnClickListener {
                submitLmcApproval("Approved")
            }

            btnDecline.setOnClickListener {
                submitLmcApproval("Decline")

            }

            btnSignature.setOnClickListener {
                if(signature.isEmpty){
                    Toast.makeText(requireContext(), "Signature should not be empty", Toast.LENGTH_SHORT).show()
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
                imageType = ImageType.SIGNATURE
                lmcStatusViewModel.uploadAttachment(request,file, Constants.LMC_CUSTOMER_SIGNATURE)
                setUploadObserver()
            }

            btnClear.setOnClickListener {
                signature.clear()
            }

            imageLayout = LayoutViewImageBinding.inflate(LayoutInflater.from(requireActivity()))
            imageDialog = Dialog(requireActivity(), R.style.list_dialog_style)
            imageDialog!!.setContentView(imageLayout!!.root)

            getAttachmentList()
            setupAttachmentObserver()

            ivIsometric.setOnClickListener {
                imageType = ImageType.ISOMETRIC
                requestCameraPermission.launch(
                    Manifest.permission.CAMERA
                )
            }

            ivInstallation.setOnClickListener {
                imageType = ImageType.INSTALLATION
                requestCameraPermission.launch(
                    Manifest.permission.CAMERA
                )
            }

            ivDrawing.setOnClickListener {
                imageType = ImageType.DRAWING
                requestCameraPermission.launch(
                    Manifest.permission.CAMERA
                )
            }

//            ivSelfie.setOnClickListener {
//                imageType = ImageType.SELFIE
//                requestCameraPermission.launch(
//                    Manifest.permission.CAMERA
//                )
//            }

            hasAcknowledged = true
            acknowledgeStatus = 1
            cbAcknowledge.isChecked = true
//            cbAcknowledge.setOnCheckedChangeListener { compoundButton, isChecked ->
//                hasAcknowledged = isChecked
//                acknowledgeStatus = if (isChecked) 1 else 0
//            }

            btnSubmit.setOnClickListener {

                if(isometricDrawingCount == 0){
                    tvIsometric.error = "Attachment required"
                    tvIsometric.requestFocus()
                    return@setOnClickListener
                }

                if(installationCount == 0){
                    tvInstallation.error = "Attachment required"
                    tvInstallation.requestFocus()
                    return@setOnClickListener
                }

                if(drawingCount == 0){
                    tvDrawing.error = "Attachment required"
                    tvDrawing.requestFocus()
                    return@setOnClickListener
                }

//                if(selfieCount == 0){
//                    tvSelfie.error = "Attachment required"
//                    tvSelfie.requestFocus()
//                    return@setOnClickListener
//                }
                if(signatureCount == 0){
                    tvCustomer.error = "Signature required"
                    tvCustomer.requestFocus()
                    return@setOnClickListener
                }
                if(!hasAcknowledged){
                    Toast.makeText(requireContext(), "Please acknowledge before submitting", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val timeStamp: String = SimpleDateFormat("ddMMyyyy_HHmmss").format(Date())
                val statusTypeId = AppCache.tpiStatusMap[args.statusType].toString()
                val subStatusId = AppCache.tpiSubStatusMap[args.subStatus].toString()

                val params = HashMap<String,String>()
                params["application_number"] = args.appNo
                params["bp_number"] = args.bpNo
                params["customer_info"] = args.customerInfo
                params["status_type_id"] = statusTypeId
                params["status_type"] = args.statusType.toString()
                params["sub_status_id"] = subStatusId
                params["sub_status"] = args.subStatus.toString()
                params["tpi_id"] = args.tpiId
                params["follow_up_date"]=args.followUpDate.toString()
                params["description"]=args.description.toString()
                params["lmc_type"]=args.lmcType
                params["meter_details"]=args.meterDetails
                params["meter_no"]=args.meterNo
                params["meter_sno"]=args.meterSerialNo
                params["meter_type"]=args.meterType
                params["initial_meter_reading"]=args.initialReading
                params["regulator_number"]=args.regulatorNo
                params["GI_install_meter"]=args.giMeter
                params["CU_install_meter"]=args.cuMeter
                params["no_of_av"]=args.avNo
                params["extra_pipe_length"]=args.pipeLength
                params["property_type"]=args.propertyType
                params["gas_type"]=args.gasType
                params["pvc_sleeve"]=pvcAvail.toString()
                params["meter_installation"]=meterAvail.toString()
                params["clamping"]=clampingAvail.toString()
                params["gas_meter_testing"]=gasAvail.toString()
                params["cementing_of_holes"]=cohAvail.toString()
                params["painting_of_GI_pipe"]=paintingAvail.toString()
                params["TF_avail"]=tfAvail.toString()
                params["connectivity"]=connectivityAvail.toString()
                params["enc_cap"]=ecAvail.toString()
                params["area_gassified"]=agAvail.toString()
//                params["cust_sat_ready_to_get_status"]=csOne.toString()
//                params["ng_conv_date_status"]=csTwo.toString()
                params["lmc_acknowledge_status"]=acknowledgeStatus.toString()
                params["lmc_session_id"]=args.sessionId
                params["lmc_created_date_time"] = timeStamp
                params["no_of_iv"] = args.ivNo
                params["corrosion_tape"] = acTape.toString()
                params["hole_drilled"] = holeDrilled.toString()
                params["mcv_testing"] =  mcvTesting.toString()
                params["extra_gi_length"] = args.extraGiLength.toString()
                params["approval_status"] = "Nil"
                params["comments"] = ""
                params["extra_mlc_length"] = args.extraMlLength.toString()
                params["lmc_execution"] = args.lmcExecution.toString()
                params["gi_clamp"] = args.lmcConnectionModel!!.lmcGiClamp.toString()
                params["mlc_clamp"] = args.lmcConnectionModel!!.lmcMlcClamp.toString()
                params["gi_MF_elbow"] = args.lmcConnectionModel!!.lmcGiMfElbow.toString()
                params["gi_FF_elbow"] = args.lmcConnectionModel!!.lmcGiFfElbow.toString()
                params["gi_2_nipple"] = args.lmcConnectionModel!!.lmcGi2.toString()
                params["gi_3_nipple"] = args.lmcConnectionModel!!.lmcGi3.toString()
                params["gi_4_nipple"] = args.lmcConnectionModel!!.lmcGi4.toString()
                params["gi_6_nipple"] = args.lmcConnectionModel!!.lmcGi6.toString()
                params["gi_8_nipple"] = args.lmcConnectionModel!!.lmcGi8.toString()
                params["gi_tee"] = args.lmcConnectionModel!!.lmcGiTee.toString()
                params["mlc_tee"] = args.lmcConnectionModel!!.lmcMlcTee.toString()
                params["gi_socket"] = args.lmcConnectionModel!!.lmcGiSocket.toString()
                params["mlc_male_union"] = args.lmcConnectionModel!!.lmcMaleUnion.toString()
                params["mlc_female_union"] = args.lmcConnectionModel!!.lmcFemaleUnion.toString()
                params["meter_bracket"] = args.lmcConnectionModel!!.lmcMeterBracket.toString()
                params["meter_sticker"] = args.lmcConnectionModel!!.lmcMeterSticker.toString()
                params["plate_marker"] = args.lmcConnectionModel!!.lmcPlateMarker.toString()
                params["adaptor_GI_to_reg"] = args.lmcConnectionModel!!.lmcAdaptorGI.toString()
                params["adaptor_reg_to_meter"] = args.lmcConnectionModel!!.lmcAdaptorReg.toString()
                params["adaptor_meter_to_GI_pipe"] = args.lmcConnectionModel!!.lmcAdaptorMeter.toString()
                params["female_union_meter_MLC_pipe"] = args.lmcConnectionModel!!.lmcFemaleMeter.toString()
                lmcStatusViewModel.submitLmc(params)

                setupObserver()

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
//                                inputStream.copyTo(os)
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

                            if (requireActivity().contentResolver.getFileSize(uri).toLong() / 1024 < 5120
                            ) {
                                val fileList = ArrayList<File>()
                                fileList.add(file)

                                val request=UploadRequestModel(
                                    bpNumber = args.bpNo, appNo = args.appNo, sessionId = args.sessionId
                                )
                                when(imageType){
                                    ImageType.ISOMETRIC->{
                                        lmcStatusViewModel.uploadAttachment(request,file, Constants.LMC_ISOMETRIC_FILE+"[]")
                                    }
                                    ImageType.INSTALLATION->{
                                        lmcStatusViewModel.uploadAttachment(request,file, Constants.LMC_INSTALLATION_FILE+"[]")
                                    }
                                    ImageType.DRAWING->{
                                        lmcStatusViewModel.uploadAttachment(request,file, Constants.LMC_DRAWING_FILE+"[]")
                                    }
                                    ImageType.SELFIE->{
                                        lmcStatusViewModel.uploadAttachment(request,file, Constants.LMC_SELFIE_WITH_METER+"[]")
                                    }
                                    else -> {

                                    }
                                }
                                setUploadObserver()
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



            val downDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_down_24)
            val upDrawable =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_up_24)
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
            tvInstallation.setOnClickListener {
                if (!installationFlag) {
                    rvInstallation.visibility = View.GONE
                    tvInstallation.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        downDrawable,
                        null
                    )
                    installationFlag = !installationFlag
                } else {
                    rvInstallation.visibility = View.VISIBLE
                    tvInstallation.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        upDrawable,
                        null
                    )
                    installationFlag = !installationFlag
                }
            }
            tvDrawing.setOnClickListener {
                if (!drawingFlag) {
                    rvDrawing.visibility = View.GONE
                    tvDrawing.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        downDrawable,
                        null
                    )
                    drawingFlag = !drawingFlag
                } else {
                    rvDrawing.visibility = View.VISIBLE
                    tvDrawing.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        upDrawable,
                        null
                    )
                    drawingFlag = !drawingFlag
                }
            }
//            tvSelfie.setOnClickListener {
//                if (!selfieFlag) {
//                    rvSelfie.visibility = View.GONE
//                    tvSelfie.setCompoundDrawablesWithIntrinsicBounds(
//                        null,
//                        null,
//                        downDrawable,
//                        null
//                    )
//                    selfieFlag = !selfieFlag
//                } else {
//                    rvSelfie.visibility = View.VISIBLE
//                    tvSelfie.setCompoundDrawablesWithIntrinsicBounds(
//                        null,
//                        null,
//                        upDrawable,
//                        null
//                    )
//                    selfieFlag = !selfieFlag
//                }
//            }

        }
        return binding.root
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
                imageType = ImageType.SIGNATURE
                lmcStatusViewModel.uploadAttachment(request,file, Constants.LMC_TPI_SIGNATURE_FILE)
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
                lmcStatusViewModel.submitLmc(params)
                alert.dismiss()
                setupObserver()

            }
        }

        dialogBinding.btnClose.setOnClickListener {
            alert.dismiss()
        }

    }


    private fun getAttachmentList(){
        val isometricParams = java.util.HashMap<String, String>()
        isometricParams["bp_number"] = args.bpNo
        isometricParams["application_number"] = args.appNo
        isometricParams["session_id"] = args.sessionId
        isometricParams["type"] = Constants.LMC_ISOMETRIC_FILE

        lmcStatusViewModel.viewAttachments(isometricParams)

        val installationParams = java.util.HashMap<String, String>()
        installationParams["bp_number"] = args.bpNo
        installationParams["application_number"] = args.appNo
        installationParams["session_id"] = args.sessionId
        installationParams["type"] = Constants.LMC_INSTALLATION_FILE

        lmcStatusViewModel.viewAttachments(installationParams)

        val drawingParams = java.util.HashMap<String, String>()
        drawingParams["bp_number"] = args.bpNo
        drawingParams["application_number"] = args.appNo
        drawingParams["session_id"] = args.sessionId
        drawingParams["type"] = Constants.LMC_DRAWING_FILE

        lmcStatusViewModel.viewAttachments(drawingParams)

        val selfieParams = java.util.HashMap<String, String>()
        selfieParams["bp_number"] = args.bpNo
        selfieParams["application_number"] = args.appNo
        selfieParams["session_id"] = args.sessionId
        selfieParams["type"] = Constants.LMC_SELFIE_WITH_METER

        lmcStatusViewModel.viewAttachments(selfieParams)

        val signatureParams = java.util.HashMap<String, String>()
        signatureParams["bp_number"] = args.bpNo
        signatureParams["application_number"] = args.appNo
        signatureParams["session_id"] = args.sessionId
        signatureParams["type"] = Constants.LMC_CUSTOMER_SIGN

        lmcStatusViewModel.viewAttachments(signatureParams)
    }

    private fun setupAttachmentObserver() {
        lmcStatusViewModel.viewAttachmentResponse.observeForever {
            if(it.data!=null){
                when(it.status){
                    Status.LOADING->{
                        setDialog(true)
                    }
                    Status.SUCCESS->{
                        setDialog(false)
                        if(!it.data.error){
                            when(it.data.type){
                                Constants.LMC_ISOMETRIC_FILE->{
                                    isometricAdapter = ViewAttachmentAdapter(
                                        requireContext(),
                                        it.data.attachmentList,
                                        {attachment ->  attachmentItemClicked(attachment) },
                                        {attachment ->  deleteItemClicked(attachment) }
                                    )
                                    binding.rvIsometric.adapter = isometricAdapter
                                    binding.tvIsometric.text = "Isometric Drawing (${isometricAdapter!!.itemCount})"
                                    binding.tvIsometric.error = null
                                    isometricAdapter!!.notifyDataSetChanged()
                                    isometricDrawingCount = isometricAdapter!!.itemCount
                                }
                                Constants.LMC_DRAWING_FILE->{
                                    drawingAdapter = ViewAttachmentAdapter(
                                        requireContext(),
                                        it.data.attachmentList,
                                        {attachment ->  attachmentItemClicked(attachment) },
                                        {attachment ->  deleteItemClicked(attachment) }
                                    )
                                    binding.rvDrawing.adapter = drawingAdapter
                                    binding.tvDrawing.text = "PPT Gauge Image  (${drawingAdapter!!.itemCount})"
                                    binding.tvDrawing.error = null
                                    drawingAdapter!!.notifyDataSetChanged()
                                    drawingCount = drawingAdapter!!.itemCount

                                }
                                Constants.LMC_INSTALLATION_FILE->{
                                    installationAdapter = ViewAttachmentAdapter(
                                        requireContext(),
                                        it.data.attachmentList,
                                        {attachment ->  attachmentItemClicked(attachment) },
                                        {attachment ->  deleteItemClicked(attachment) }
                                    )
                                    binding.rvInstallation.adapter = installationAdapter
                                    binding.tvInstallation.text = "Installation Documents (${installationAdapter!!.itemCount})"
                                    binding.tvInstallation.error = null
                                    installationAdapter!!.notifyDataSetChanged()
                                    installationCount = installationAdapter!!.itemCount
                                }
//                                Constants.LMC_SELFIE_WITH_METER->{
//                                    selfieAdapter = ViewAttachmentAdapter(
//                                        requireContext(),
//                                        it.data.attachmentList,
//                                        {attachment ->  attachmentItemClicked(attachment) },
//                                        {attachment ->  deleteItemClicked(attachment) }
//                                    )
//                                    binding.rvSelfie.adapter = selfieAdapter
//                                    binding.tvSelfie.text = "Selfie With Meter (${selfieAdapter.itemCount})"
//
//                                    selfieAdapter.notifyDataSetChanged()
//                                    selfieCount = selfieAdapter.itemCount
//                                }
                                Constants.LMC_CUSTOMER_SIGN->{
                                    signatureAdapter = ViewAttachmentAdapter(
                                        requireContext(),
                                        it.data.attachmentList,
                                        {attachment ->  attachmentItemClicked(attachment) },
                                        {attachment ->  deleteItemClicked(attachment) }
                                    )
                                    binding.rvSignature.adapter = signatureAdapter
                                    binding.tvCustomer.text = "Customer Signature (${signatureAdapter!!.itemCount})"
                                    binding.tvCustomer.error = null
                                    signatureAdapter!!.notifyDataSetChanged()
                                    signatureCount = signatureAdapter!!.itemCount
                                    if(signatureAdapter!!.itemCount>0){
                                        binding.apply {
                                            signature.visibility = View.GONE
                                            btnSignature.visibility = View.GONE
                                            btnClear.visibility = View.GONE
                                        }

                                    }
                                }

                            }

                            if(isometricAdapter == null){
                                binding.tvIsometric.text = "Isometric Drawing (0)"
                                isometricDrawingCount = 0
                            }

                            if(drawingAdapter == null){
                                binding.tvDrawing.text = "PPT Gauge Image (0)"
                                drawingCount = 0
                            }

                            if(installationAdapter == null){
                                binding.tvInstallation.text = "Installation Documents (0)"
                                installationCount = 0
                            }

                            if(signatureAdapter == null){
                                binding.tvCustomer.text = "Customer Signature (0)"
                                binding.signature.visibility = View.VISIBLE
                                binding.btnSignature.visibility = View.VISIBLE
                                binding.btnClear.visibility = View.VISIBLE
                                binding.signature.clear()
                                binding.signature.isEnabled= true
                                signatureCount = 0
                            }


                        } else {
                            if(isometricAdapter == null){
                                binding.tvIsometric.text = "Isometric Drawing (0)"
                                isometricDrawingCount = 0
                            }

                            if(drawingAdapter == null){
                                binding.tvDrawing.text = "PPT Gauge Image (0)"
                                drawingCount = 0
                            }

                            if(installationAdapter == null){
                                binding.tvInstallation.text = "Installation Documents (0)"
                                installationCount = 0
                            }

                            if(signatureAdapter == null){
                                binding.tvCustomer.text = "Customer Signature (0)"
                                binding.signature.visibility = View.VISIBLE
                                binding.btnSignature.visibility = View.VISIBLE
                                binding.signature.clear()
                                binding.signature.isEnabled= true
                                binding.btnClear.visibility = View.VISIBLE
                                signatureCount = 0
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
                    lmcStatusViewModel.deleteAttachment(deleteParams)
                    lmcStatusViewModel.deleteAttachmentResponse.observe(viewLifecycleOwner){
                        if(it != null){

                            when(it.status){
                                Status.SUCCESS->{
                                    if(!it.data!!.error){
                                        imageDialog!!.dismiss()
//                                        binding.rvAttachment.adapter = null
                                        binding.rvDrawing.adapter = null
                                        binding.rvIsometric.adapter = null
                                        binding.rvInstallation.adapter = null
                                        binding.rvSignature.adapter = null
                                        drawingAdapter = null
                                        installationAdapter = null
                                        isometricAdapter = null
                                        signatureAdapter = null
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

    private fun setSignatureObserver(binding:CommentDialogBinding) {
        lmcStatusViewModel.uploadResponse.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.LOADING->{
                        setDialog(true)
                    }
                    Status.SUCCESS->{
                        setDialog(false)
                        if(imageType == ImageType.SIGNATURE){
                            binding.apply {
                                signature.isEnabled = false
                                btnSignature.visibility = View.GONE
                                btnClear.visibility = View.GONE
                            }
                        }
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


    private fun setUploadObserver(){
        lmcStatusViewModel.uploadResponse.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.LOADING->{
                        setDialog(true)
                    }
                    Status.SUCCESS->{
                        setDialog(false)
                        if(!it.data?.error!!) {
                            if (imageType == ImageType.SIGNATURE) {
                                binding.apply {
                                    signature.isEnabled = false
                                    btnSignature.visibility = View.GONE
                                    btnClear.visibility = View.GONE
                                }
                            }
                            getAttachmentList()
                        }else{
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT)
                                .show()
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

    private fun setupObserver() {
        lmcStatusViewModel.submitResponse.observe(viewLifecycleOwner) {
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

    private fun setDialog(show: Boolean) {
        if (show) dialog!!.show() else dialog!!.dismiss()
    }

}