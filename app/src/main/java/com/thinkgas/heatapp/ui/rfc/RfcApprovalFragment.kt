package com.thinkgas.heatapp.ui.rfc

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.thinkgas.heatapp.R
import com.thinkgas.heatapp.data.cache.AppCache
import com.thinkgas.heatapp.data.remote.model.Attachment
import com.thinkgas.heatapp.databinding.CommentDialogBinding
import com.thinkgas.heatapp.databinding.FragmentRfcApprovalBinding
import com.thinkgas.heatapp.databinding.LayoutViewImageBinding
import com.thinkgas.heatapp.ui.rfc.adapters.RfcImageAdapter
import com.thinkgas.heatapp.utils.Constants
import com.thinkgas.heatapp.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.HashMap

@AndroidEntryPoint
class RfcApprovalFragment : Fragment() {
    private var _binding:FragmentRfcApprovalBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<RfcApprovalFragmentArgs>()
    private val viewModel by viewModels<RfcStatusViewModel>()
    private lateinit var isometricAdapter: RfcImageAdapter
    private lateinit var installationAdapter: RfcImageAdapter
    private lateinit var signatureAdapter: RfcImageAdapter

    private var imageDialog: Dialog? = null
    private var imageLayout: LayoutViewImageBinding? = null
    private var dialog: Dialog? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRfcApprovalBinding.inflate(inflater,container,false)

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.progress)
        dialog = builder.create()

        imageLayout = LayoutViewImageBinding.inflate(LayoutInflater.from(requireActivity()))
        imageDialog = Dialog(requireActivity(), R.style.list_dialog_style)
        imageDialog!!.setContentView(imageLayout!!.root)

        getAttachmentList()
        setupAttachmentObserver()

        binding.apply {
            ivBack.setOnClickListener {
                findNavController().navigateUp()
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
                    val directions = RfcApprovalFragmentDirections.actionRfcApprovalFragmentToLoginFragment()
                    findNavController().navigate(directions)

                }
                logoutBuilder.setNegativeButton("No") { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                val logoutAlert = logoutBuilder.create()
                logoutAlert.show()


            }


            if(AppCache.isTpi){
                btnNext.visibility = View.VISIBLE

            }

            btnNext.setOnClickListener {
                val directions = RfcApprovalFragmentDirections.actionRfcApprovalFragmentToNgApprovalFragment(
                    appNo = args.appNo,
                    sessionId = args.sessionId,
                    tpiId = args.tpiId,
                    customerInfo = args.customerInfo,
                    status = args.status
                )
                findNavController().navigate(directions)
            }

//           btnApprove.setOnClickListener {
////               submitRfcApproval("Approved")
//               val params = HashMap<String,String>()
//               params["application_number"] = args.appNo
//               params["bp_number"] = args.bpNo
//               params["tpi_id"] = args.tpiId
//               params["customer_info"] = args.customerInfo
//               params["rfc_approval_status"] = "Approved"
//               params["rfc_comments"] = ""
//               params["rfc_session_id"] = args.sessionId
//               viewModel.submitRfcApproval(params)
//               setupApprovalObserver("Approved")
//           }
//            btnDecline.setOnClickListener {
//                submitRfcApproval("Decline")
//            }
        }

        return binding.root
    }

    private fun submitRfcApproval(status:String){
        val dialogBinding = CommentDialogBinding.inflate(LayoutInflater.from(requireContext()))
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        builder.setView(dialogBinding.root)
        val alert = builder.create()
        alert.show()
        dialogBinding.apply {
            btnSignature.visibility = View.GONE
            signature.visibility = View.GONE
            btnClear.visibility = View.GONE
            if(status.equals("Approved")){
                etComment.visibility = View.GONE
                btnSubmit.text = "Approve"
            }else{
                btnSubmit.text = status
            }
//            btnClear.setOnClickListener {
//                signature.clear()
//            }
//            btnSignature.setOnClickListener {
//                val bitmap=signature.signatureBitmap
//                val fileName: String = SimpleDateFormat("yyyyMMDD_HHmmss").format(Date())
//                val file = File(requireActivity().externalCacheDir, "$fileName.jpg")
//                val os: OutputStream = BufferedOutputStream(FileOutputStream(file))
//                bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, os)
//                os.close()
//                val request= UploadRequestModel(
//                    bpNumber = args.bpNo, appNo = args.appNo, sessionId = args.sessionId
//                )
//                setUploadObserver(dialogBinding)
//                viewModel.uploadAttachment(request,file,Constants.NG_CUSTOMER_SIGNATURE_FILE)
//            }
            btnSubmit.setOnClickListener {
                if(signature.isEnabled){
                    Toast.makeText(requireContext(), "Signature is needed", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val params = HashMap<String,String>()
                params["application_number"] = args.appNo
                params["bp_number"] = args.bpNo
                params["tpi_id"] = args.tpiId
                params["customer_info"] = args.customerInfo
                params["rfc_approval_status"] = status
                params["rfc_comments"] = etComment.text.toString()
                params["rfc_session_id"] = args.sessionId
                viewModel.submitRfcApproval(params)
                alert.dismiss()
                setupApprovalObserver(status)

                //if ($rfc_approval_status == "Approved")
            }
        }

        dialogBinding.btnClose.setOnClickListener {
            alert.dismiss()
        }

    }

    private fun setupApprovalObserver(status:String) {
        viewModel.rfcApprovalResponse.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.LOADING->{
                        setDialog(true)
                    }
                    Status.SUCCESS->{
                        setDialog(false)
                        if(!it.data!!.error){
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                            if(status.equals("Approved")) {
                                val directions =
                                    RfcApprovalFragmentDirections.actionRfcApprovalFragmentToNgApprovalFragment(
                                        appNo = args.appNo,
                                        sessionId = args.sessionId,
                                        tpiId = args.tpiId,
                                        customerInfo = args.customerInfo,
                                        status = args.status
                                    )
                                findNavController().navigate(directions)
                            }else{
                                findNavController().popBackStack(R.id.rfcHomeFragment,false)
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


    private fun setUploadObserver(binding:CommentDialogBinding){
        viewModel.uploadResponse.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.LOADING->{

                    }
                    Status.SUCCESS->{
                        Toast.makeText(requireContext(), it.data!!.message, Toast.LENGTH_SHORT).show()
//                        binding.signature.isEnabled = false
//                        binding.btnClear.visibility =  View.GONE
//                        binding.btnSignature.visibility = View.GONE
                    }
                    Status.ERROR->{
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }
    }

    private fun getAttachmentList(){
        val isometricParams = java.util.HashMap<String, String>()
        isometricParams["bp_number"] = args.bpNo
        isometricParams["application_number"] = args.appNo
        isometricParams["session_id"] = args.sessionId
        isometricParams["type"] = "lmc_isometric_file"

        viewModel.viewAttachments(isometricParams)

        val installationParams = java.util.HashMap<String, String>()
        installationParams["bp_number"] = args.bpNo
        installationParams["application_number"] = args.appNo
        installationParams["session_id"] = args.sessionId
        installationParams["type"] = "lmc_installation_file"

        viewModel.viewAttachments(installationParams)

//        val drawingParams = java.util.HashMap<String, String>()
//        drawingParams["bp_number"] = args.bpNo
//        drawingParams["application_number"] = args.appNo
//        drawingParams["session_id"] = args.sessionId
//        drawingParams["type"] = "lmc_drawing_file"
//
//        viewModel.viewAttachments(drawingParams)

        val selfieParams = java.util.HashMap<String, String>()
        selfieParams["bp_number"] = args.bpNo
        selfieParams["application_number"] = args.appNo
        selfieParams["session_id"] = args.sessionId
        selfieParams["type"] = "lmc_customer_signature"

        viewModel.viewAttachments(selfieParams)
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
                                Constants.LMC_ISOMETRIC_FILE->{
                                    isometricAdapter = RfcImageAdapter(
                                        requireContext(),
                                        it.data.attachmentList
                                    ) { attachment -> attachmentItemClicked(attachment) }
                                    binding.rvIsometric.adapter = isometricAdapter
                                    if(isometricAdapter.itemCount>0){
                                        binding.tvIsometric.visibility = View.GONE
                                    }
                                    isometricAdapter.notifyDataSetChanged()
                                }
                                Constants.LMC_INSTALLATION_FILE->{
                                    installationAdapter = RfcImageAdapter(
                                        requireContext(),
                                        it.data.attachmentList
                                    ) { attachment -> attachmentItemClicked(attachment) }
                                    binding.rvInstallation.adapter = installationAdapter
                                    if(installationAdapter.itemCount>0){
                                        binding.tvInstallation.visibility = View.GONE
                                    }
                                    installationAdapter.notifyDataSetChanged()
                                }
                                Constants.LMC_CUSTOMER_SIGN->{
                                    signatureAdapter = RfcImageAdapter(
                                        requireContext(),
                                        it.data.attachmentList
                                    ) { attachment -> attachmentItemClicked(attachment) }
                                    binding.rvSignature.adapter = signatureAdapter
                                    if(signatureAdapter.itemCount>0){
                                        binding.tvSignature.visibility = View.GONE
                                    }
                                    signatureAdapter.notifyDataSetChanged()
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
        imageLayout?.btnDelete!!.visibility = View.GONE
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
                                        binding.rvInstallation.adapter = null
                                        binding.rvSignature.adapter = null
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

    private fun setDialog(show: Boolean) {
        if (show) dialog!!.show() else dialog!!.dismiss()
    }

}