package com.thinkgas.heatapp.ui.ng

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
import com.thinkgas.heatapp.databinding.FragmentNgVerificationBinding
import com.thinkgas.heatapp.databinding.LayoutViewImageBinding
import com.thinkgas.heatapp.ui.common.adapters.ViewAttachmentAdapter
import com.thinkgas.heatapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@AndroidEntryPoint
class NgVerificationFragment : Fragment()
{
    private var _binding:FragmentNgVerificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<NgApprovalViewModel>()
    private val args by navArgs<NgVerificationFragmentArgs>()

    private var acknowledgeId:String? = null
    private var photoURI: Uri? = null
    private var imageDialog: Dialog? = null
    private var imageLayout: LayoutViewImageBinding? = null

    private lateinit var cameraActions: ActivityResultLauncher<Intent>
    lateinit var currentPhotoPath: String
    private  var meterAdapter: ViewAttachmentAdapter? = null
    private  var installationAdapter: ViewAttachmentAdapter? = null
    private  var serviceAdapter: ViewAttachmentAdapter? = null
    private  var selfieAdapter: ViewAttachmentAdapter? = null
    private  var gasAdapter: ViewAttachmentAdapter? = null

    private var installationCount = 0
    private var meterCount = 0
    private var serviceCount = 0
    private var selfieCount = 0
    private var gasCount = 0

    private var imageType: NgImageType? = null

    private var dialog: Dialog? = null

    companion object {
        var meterFlag = false
        var installationFlag = false
        var serviceFlag = false
        var selfieFlag = false
        var gasFlag = false
    }

    enum class NgImageType{
        METER,
        INSTALLATION,
        SERVICE,
        SELFIE,
        GAS
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNgVerificationBinding.inflate(inflater,container,false)

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.progress)
        dialog = builder.create()

        binding.apply {
           tvBpNo.text = args.bpNo
           tvDate.text = args.assignedDate
            tvInitial.text = args.finalReading
            tvBurner.text = args.burnerType
            tvWork.text = args.workDate
//            cbAcknowledge.setOnCheckedChangeListener { view, isChecked ->
                cbAcknowledge.isChecked = true
                acknowledgeId = "1"
//            }
//            if(args.acknowledgeId != null){
//                acknowledgeId = args.acknowledgeId
//                cbAcknowledge.isChecked = args.acknowledgeId == "1"
//            }

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
                    val directions = NgVerificationFragmentDirections.actionNgVerificationFragmentToLoginFragment()
                    findNavController().navigate(directions)

                }
                logoutBuilder.setNegativeButton("No") { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                val logoutAlert = logoutBuilder.create()
                logoutAlert.show()


            }


            if(AppCache.isTpi){
              btnSubmit.visibility = View.GONE
              cbAcknowledge.visibility = View.GONE
              tvAcknowledge.visibility = View.GONE
              btnApprove.visibility = View.VISIBLE
              btnDecline.visibility = View.VISIBLE
                ivLiveGas.visibility = View.GONE
                ivService.visibility = View.GONE
                ivSelfie.visibility = View.GONE
                ivInstallation.visibility = View.GONE
                ivMeter.visibility = View.GONE
            }

            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }

            btnApprove.setOnClickListener {
                submitNgApproval("Approved")
            }
            btnDecline.setOnClickListener {
                submitNgApproval("Decline")
            }

            imageLayout = LayoutViewImageBinding.inflate(LayoutInflater.from(requireActivity()))
            imageDialog = Dialog(requireActivity(), R.style.list_dialog_style)
            imageDialog!!.setContentView(imageLayout!!.root)

            getAttachmentList()
            setupAttachmentObserver()

            ivMeter.setOnClickListener {
                imageType = NgImageType.METER
                requestCameraPermission.launch(
                    Manifest.permission.CAMERA
                )
            }

            ivInstallation.setOnClickListener {
                imageType = NgImageType.INSTALLATION
                requestCameraPermission.launch(
                    Manifest.permission.CAMERA
                )
            }

            ivSelfie.setOnClickListener {
                imageType = NgImageType.SELFIE
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }

            ivService.setOnClickListener {
                imageType = NgImageType.SERVICE
                requestCameraPermission.launch(
                    Manifest.permission.CAMERA
                )
            }

            ivLiveGas.setOnClickListener {
                imageType = NgImageType.GAS
                requestCameraPermission.launch(
                    Manifest.permission.CAMERA
                )
            }


            btnSubmit.setOnClickListener {
                if(selfieCount == 0){
                    tvSelfie.error = "Attachment Required"
                    tvSelfie.requestFocus()
                    return@setOnClickListener
                }

                if(meterCount == 0){
                    tvMeter.error = "Attachment Required"
                    tvMeter.requestFocus()
                    return@setOnClickListener
                }

                if(installationCount == 0){
                    tvInstallation.error = "Attachment Required"
                    tvInstallation.requestFocus()
                    return@setOnClickListener
                }

                if(serviceCount == 0){
                    tvService.error = "Attachment Required"
                    tvService.requestFocus()
                    return@setOnClickListener
                }


                if(args.mmtStatus == "1"){
                    if(gasCount == 0){
                        tvLiveGas.error = "Attachment Required"
                        tvLiveGas.requestFocus()
                        return@setOnClickListener
                    }

                }

                if(acknowledgeId.isNullOrBlank()){
                    Toast.makeText(requireContext(), "Please acknowledge before submitting", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                updateRfcNg()
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

                            if (requireActivity().contentResolver.getFileSize(uri)
                                    .toLong() / 1024 < 5120
                            ) {
                                val fileList = ArrayList<File>()
                                fileList.add(file)

                                val request=UploadRequestModel(
                                    bpNumber = args.bpNo!!, appNo = args.appNo, sessionId = args.sessionId!!
                                )
                                when(imageType){
                                    NgVerificationFragment.NgImageType.INSTALLATION->{
                                        viewModel.uploadAttachment(request,file, Constants.NG_INSTALLATION_PHOTO_FILE)
                                    }
                                    NgVerificationFragment.NgImageType.METER->{
                                        viewModel.uploadAttachment(request,file, Constants.NG_METER_PHOTO_FILE)
                                    }
                                    NgVerificationFragment.NgImageType.SELFIE->{
                                        viewModel.uploadAttachment(request,file, Constants.NG_SELFIE_WITH_METER_FILE)
                                    }
                                    NgVerificationFragment.NgImageType.SERVICE->{
                                        viewModel.uploadAttachment(request,file, Constants.NG_SERVICE_CARD_FILE)
                                    }
                                    NgVerificationFragment.NgImageType.GAS->{
                                        viewModel.uploadAttachment(request,file, Constants.NG_LIVE_GAS_READING_FILE)
                                    }
                                    else -> {

                                    }
                                }
                                setUploadAttchmentObserver()
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

            if(args.mmtStatus != "1"){
                rvLiveGas.visibility = View.GONE
                tvLiveGas.visibility = View.GONE
                ivLiveGas.visibility = View.GONE
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

            tvMeter.setOnClickListener {
                if (!meterFlag) {
                    rvMeter.visibility = View.GONE
                    tvMeter.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        downDrawable,
                        null
                    )
                    meterFlag = !meterFlag
                } else {
                    rvMeter.visibility = View.VISIBLE
                    tvMeter.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        upDrawable,
                        null
                    )
                    meterFlag = !meterFlag
                }
            }

            tvSelfie.setOnClickListener {
                if (!selfieFlag) {
                    rvSelfie.visibility = View.GONE
                    tvSelfie.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        downDrawable,
                        null
                    )
                    selfieFlag = !selfieFlag
                } else {
                    rvSelfie.visibility = View.VISIBLE
                    tvSelfie.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        upDrawable,
                        null
                    )
                    selfieFlag = !selfieFlag
                }
            }

            tvService.setOnClickListener {
                if (!serviceFlag) {
                    rvService.visibility = View.GONE
                    tvService.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        downDrawable,
                        null
                    )
                    serviceFlag = !serviceFlag
                } else {
                    rvService.visibility = View.VISIBLE
                    tvService.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        upDrawable,
                        null
                    )
                    serviceFlag = !serviceFlag
                }
            }

            tvLiveGas.setOnClickListener {
                if (!gasFlag) {
                    rvLiveGas.visibility = View.GONE
                    tvLiveGas.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        downDrawable,
                        null
                    )
                    gasFlag = !gasFlag
                } else {
                    rvLiveGas.visibility = View.VISIBLE
                    tvLiveGas.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        upDrawable,
                        null
                    )
                    gasFlag = !gasFlag
                }
            }

        }




        return binding.root
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
                    bpNumber = args.bpNo!!, appNo = args.appNo, sessionId = args.sessionId!!
                )
                setUploadObserver(dialogBinding)
                viewModel.uploadAttachment(request,file,Constants.NG_CUSTOMER_SIGNATURE_FILE)
            }
            btnSubmit.setOnClickListener {
                if(signature.isEnabled){
                    Toast.makeText(requireContext(), "Signature is needed", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val params = HashMap<String,String?>()
                params["application_number"] = args.appNo
                params["bp_number"] = args.bpNo
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
        deleteParams["bp_number"] = args.bpNo!!
        deleteParams["application_number"] = args.appNo
        deleteParams["session_id"] = args.sessionId!!
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
                                        binding.rvInstallation.adapter = null
                                        binding.rvMeter.adapter = null
                                        binding.rvSelfie.adapter = null
                                        binding.rvService.adapter = null
                                        binding.rvLiveGas.adapter = null
                                        serviceAdapter = null
                                        selfieAdapter = null
                                        meterAdapter = null
                                        gasAdapter = null
                                        installationAdapter = null

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

    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
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

    private fun setUploadAttchmentObserver() {
        viewModel.uploadResponse.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.LOADING->{
                        setDialog(true)
                    }
                    Status.SUCCESS->{
                        setDialog(false)
                        if (!it.data?.error!!){
                            getAttachmentList()
                        }else {
                            Toast.makeText(requireContext(), it.data!!.message, Toast.LENGTH_SHORT)
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

    private fun getAttachmentList(){
        val serviceParams = java.util.HashMap<String, String>()
        serviceParams["bp_number"] = args.bpNo!!
        serviceParams["application_number"] = args.appNo
        serviceParams["session_id"] = args.sessionId!!
        serviceParams["type"] = Constants.NG_SERVICE_CARD_FILE_TYPE

        viewModel.viewAttachments(serviceParams)

        val installationParams = java.util.HashMap<String, String>()
        installationParams["bp_number"] = args.bpNo!!
        installationParams["application_number"] = args.appNo
        installationParams["session_id"] = args.sessionId!!
        installationParams["type"] = Constants.NG_INSTALLATION_PHOTO_FILE_TYPE

        viewModel.viewAttachments(installationParams)

        val meterParams = java.util.HashMap<String, String>()
        meterParams["bp_number"] = args.bpNo!!
        meterParams["application_number"] = args.appNo
        meterParams["session_id"] = args.sessionId!!
        meterParams["type"] = Constants.NG_METER_PHOTO_FILE_TYPE

        viewModel.viewAttachments(meterParams)

        val selfieParams = java.util.HashMap<String, String>()
        selfieParams["bp_number"] = args.bpNo!!
        selfieParams["application_number"] = args.appNo
        selfieParams["session_id"] = args.sessionId!!
        selfieParams["type"] = Constants.NG_SELFIE_WITH_METER_FILE_TYPE

        viewModel.viewAttachments(selfieParams)

        val gasParams = java.util.HashMap<String, String>()
        gasParams["bp_number"] = args.bpNo!!
        gasParams["application_number"] = args.appNo
        gasParams["session_id"] = args.sessionId!!
        gasParams["type"] = Constants.NG_LIVE_GAS_READING_FILE_TYPE

        viewModel.viewAttachments(gasParams)
    }

    private fun setupAttachmentObserver() {
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
                                Constants.NG_SERVICE_CARD_FILE_TYPE->
                                {
                                    serviceAdapter = ViewAttachmentAdapter(
                                        requireContext(),
                                        it.data.attachmentList,
                                        {attachment ->  attachmentItemClicked(attachment) },
                                        {attachment ->  deleteItemClicked(attachment) }
                                    )
                                    binding.rvService.adapter = serviceAdapter
                                    binding.tvService.text = "Live Stove Gas Image (${serviceAdapter!!.itemCount})"
                                    binding.tvService.error = null
                                    serviceAdapter!!.notifyDataSetChanged()
                                    serviceCount = serviceAdapter!!.itemCount
                                }
                                Constants.NG_METER_PHOTO_FILE_TYPE->{
                                    meterAdapter = ViewAttachmentAdapter(
                                        requireContext(),
                                        it.data.attachmentList,
                                        {attachment ->  attachmentItemClicked(attachment) },
                                        {attachment ->  deleteItemClicked(attachment) }
                                    )
                                    binding.rvMeter.adapter = meterAdapter
                                    binding.tvMeter.text = "MMT Gauge Testing Photo (${meterAdapter!!.itemCount})"
                                    binding.tvMeter.error = null
                                    meterAdapter!!.notifyDataSetChanged()
                                    meterCount =  meterAdapter!!.itemCount
                                }
                                Constants.NG_INSTALLATION_PHOTO_FILE_TYPE->{
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
                                Constants.NG_SELFIE_WITH_METER_FILE_TYPE->{
                                    selfieAdapter = ViewAttachmentAdapter(
                                        requireContext(),
                                        it.data.attachmentList,
                                        {attachment ->  attachmentItemClicked(attachment) },
                                        {attachment ->  deleteItemClicked(attachment) }
                                    )
                                    binding.rvSelfie.adapter = selfieAdapter
                                    binding.tvSelfie.text = "Selfie with meter (${selfieAdapter!!.itemCount})"
                                    binding.tvSelfie.error = null
                                    selfieAdapter!!.notifyDataSetChanged()
                                    selfieCount = selfieAdapter!!.itemCount
                                }
                                Constants.NG_LIVE_GAS_READING_FILE_TYPE->{
                                    gasAdapter = ViewAttachmentAdapter(
                                        requireContext(),
                                        it.data.attachmentList,
                                        {attachment ->  attachmentItemClicked(attachment) },
                                        {attachment ->  deleteItemClicked(attachment) }
                                    )
                                    binding.rvLiveGas.adapter = gasAdapter
                                    binding.tvLiveGas.text = "Final Meter Reading Image (${gasAdapter!!.itemCount})"
                                    binding.tvLiveGas.error = null
                                    gasAdapter!!.notifyDataSetChanged()
                                    gasCount = gasAdapter!!.itemCount
                                }
                            }
                            
                            if(serviceAdapter == null){
                                binding.tvService.text = "Live Stove Gas Image (0)"
                                serviceCount = 0
                            }

                            if(gasAdapter == null){
                                binding.tvLiveGas.text = "Final Meter Reading Image (0)"
                                gasCount = 0
                            }

                            if(meterAdapter == null){
                                binding.tvMeter.text = "MMT Gauge Testing Photo (0)"
                                meterCount = 0
                            }

                            if(installationAdapter == null){
                                binding.tvInstallation.text = "Installation Documents (0)"
                                installationCount = 0
                            }

                            if (selfieAdapter == null){
                                binding.tvSelfie.text = "Selfie with meter (0)"
                                selfieCount = 0
                            }
                        } else {
                            if(serviceAdapter == null){
                                binding.tvService.text = "Live Stove Gas Image (0)"
                                serviceCount = 0
                            }

                            if(gasAdapter == null){
                                binding.tvLiveGas.text = "Final Meter Reading Image (0)"
                                gasCount = 0
                            }

                            if(meterAdapter == null){
                                binding.tvMeter.text = "MMT Gauge Testing Photo (0)"
                                meterCount = 0
                            }

                            if(installationAdapter == null){
                                binding.tvInstallation.text = "Installation Documents (0)"
                                installationCount = 0
                            }

                            if (selfieAdapter == null){
                                binding.tvSelfie.text = "Selfie with meter (0)"
                                selfieCount = 0
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

    private fun updateRfcNg(){
        val dialogBinding = CommentDialogBinding.inflate(LayoutInflater.from(requireContext()))
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        builder.setView(dialogBinding.root)
        val alert = builder.create()
        alert.show()
        dialogBinding.apply {

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
                    bpNumber = args.bpNo!!, appNo = args.appNo, sessionId = args.sessionId!!
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
                params["bp_number"] = args.bpNo
                params["tpi_id"] = args.tpiId
                params["customer_info"] = args.customerInfo
                params["ng_session_id"] = args.sessionId
//                params["ng_meter_correct_status"] = args.meterStatus!!
//                params["ng_select_status_id"] = args.statusId!!
//                params["ng_initial_reading"] = args.initialReading!!
//                params["ng_burner_details"] = args.burnerDetails!!
                params["ng_convertion_date"] = args.conversionDate
                params["ng_testing_leakage_acceptance"] = acknowledgeId
                params["rfc_status"] = args.rfcStatus
                params["mmt_testing"] = args.mmtStatus
                params["leakage_testing"] = args.leakageStatus
                params["gas_pressure"] = args.gasPressure ?: ""
                params["meter_reading"] = args.finalReading ?: ""
                params["burner_type"] = args.burnerType
                params["hose_pipe"] = args.hoseLength
                params["nozzle_65"] = args.ng65Length
                params["nozzle_90"] = args.ng90Length
                params["nozzle_110"] = args.ng110Length
                params["nozzle_125"] = args.ng125Length
                params["drs_number"] = args.drsNumber
                params["sr_number"] = args.srNumber
                params["gi_union"] = args.giUnion.toString()
                params["rfc_approval_status"] = "Nil"
                params["rfc_comments"] = ""
                params["gi_clamp"] = args.lmcGiClamp
                params["mlc_clamp"] = args.lmcMlcClamp
                params["gi_MF_elbow"] = args.lmcGiMfElbow
                params["gi_FF_elbow"] = args.lmcGiFfElbow
                params["gi_2_nipple"] = args.lmcGi2
                params["gi_3_nipple"] = args.lmcGi3
                params["gi_4_nipple"] = args.lmcGi4
                params["gi_6_nipple"] = args.lmcGi6
                params["gi_8_nipple"] = args.lmcGi8
                params["gi_tee"] = args.lmcGiTee
                params["mlc_tee"] = args.lmcMlcTee
                params["gi_socket"] = args.lmcGiSocket
                params["mlc_male_union"] = args.lmcMaleUnion
                params["mlc_female_union"] = args.lmcFemaleUnion
                params["meter_no"] = args.lmcMeterNo
//                params["meter_type"] =
                params["regulator_no"] = args.lmcRegulatorNo
                params["plate_marker"] = args.lmcPlateMarker
                params["adaptor_GI_to_reg"] = args.lmcAdaptorGI
                params["adaptor_reg_to_meter"] = args.lmcAdaptorReg
                params["adaptor_meter_to_GI_pipe"] = args.lmcAdaptorMeter
                params["female_union_meter_MLC_pipe"] = args.lmcFemaleMeter
                params["extension_modication_of_lmc"] = args.lmcExtension.toString()


                params["wo_gi_length"] = args.lmcGiLength
                params["wo_mlc_length"] = args.lmcMlcLength
                params["wo_extra_gi"] = args.lmcExtraGiLength
                params["wo_extra_mlc"] = args.lmcExtraMlcLength
                params["wo_av_no"] = args.lmcAvQty
                params["wo_iv_no"] = args.lmcIvQty

                params["wo_meter_company"] = args.lmcMeterCompany
                params["wo_initial_meter_reading"] = args.lmcInitialReading
                params["wo_meter_bracket"] = args.lmcWoMeterBracket
                params["wo_meter_sticker"] = args.lmcWoMeterSticker
                params["wo_meter_no"] = args.lmcWoMeterNumber
                params["wo_regulator_number"] = args.lmcWoRegulatorNumber
                params["wo_adaptor_GI_to_reg"] = args.lmcWoAdaptorGI
                params["wo_adaptor_reg_to_meter"] = args.lmcWoAdaptorReg
                params["wo_adaptor_meter_to_GI_pipe"] = args.lmcWoAdaptorMeter
                params["wo_female_union_meter_MLC_pipe"] = args.lmcWoFemaleMeter

                viewModel.updateRfcNg(params)
                alert.dismiss()
                setupNgObserver()

            }
        }

        dialogBinding.btnClose.setOnClickListener {
            alert.dismiss()
        }

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
                            findNavController().popBackStack(R.id.rfcHomeFragment,false)
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

    private fun setDialog(show: Boolean) {
        if (show) dialog!!.show() else dialog!!.dismiss()
    }

}