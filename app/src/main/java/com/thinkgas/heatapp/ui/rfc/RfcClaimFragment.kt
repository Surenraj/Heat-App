package com.thinkgas.heatapp.ui.rfc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thinkgas.heatapp.R


class RfcClaimFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

//        cameraActions =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                if (it.resultCode == Activity.RESULT_OK && photoURI!=null) {
//
//                    photoURI.let { uri ->
//                        val parcelFileDescriptor =
//                            requireActivity().contentResolver.openFileDescriptor(uri!!, "r", null)
//                                ?: return@registerForActivityResult
//                        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
//                        val timeStamp: String = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Date())
//                        val fileName: String = SimpleDateFormat("yyyyMMDD_HHmmss").format(Date())
//
//
//                        val fileInfo = File(
//                            requireActivity().externalCacheDir,
//                            requireActivity().contentResolver.getFileName(uri)
//                        )
//                        var bitmap = getBitmap(fileInfo.path)
//                        val location = AppUtils.getAddress(
//                            AppCache.latitude!!,
//                            AppCache.longitude!!,requireContext())
//                        val imageText = "$timeStamp \n $location"
//                        var result = drawTextToBitmap(bitmap!!, text = imageText)
//
//                        val file = File(requireActivity().externalCacheDir,fileName+".jpg")
//                        val os: OutputStream = BufferedOutputStream(FileOutputStream(file))
//                        result?.compress(Bitmap.CompressFormat.JPEG, 50, os)
//                        os.close()
//                        val outputStream = FileOutputStream(fileInfo)
//                        if (requireActivity().contentResolver.getFileSize(uri)
//                                .toLong() / 1024 < 5120
//                        ) {
//                            val fileList = ArrayList<File>()
//                            fileList.add(fileInfo)
//
//                            val request= UploadRequestModel(
//                                bpNumber = args.bpNo!!, appNo = args.appNo, sessionId = args.sessionId!!
//                            )
//                            when(imageType){
//                                NgVerificationFragment.NgImageType.INSTALLATION->{
//                                    viewModel.uploadAttachment(request,file, Constants.NG_INSTALLATION_PHOTO_FILE)
//                                }
//                                NgVerificationFragment.NgImageType.METER->{
//                                    viewModel.uploadAttachment(request,file, Constants.NG_METER_PHOTO_FILE)
//                                }
//                                NgVerificationFragment.NgImageType.SELFIE->{
//                                    viewModel.uploadAttachment(request,file, Constants.NG_SELFIE_WITH_METER_FILE)
//                                }
//                                NgVerificationFragment.NgImageType.SERVICE->{
//                                    viewModel.uploadAttachment(request,file, Constants.NG_SERVICE_CARD_FILE)
//                                }
//                                NgVerificationFragment.NgImageType.GAS->{
//                                    viewModel.uploadAttachment(request,file, Constants.NG_LIVE_GAS_READING_FILE)
//                                }
//                                else -> {
//
//                                }
//                            }
//                            setUploadAttchmentObserver()
//                            Toast.makeText(
//                                requireActivity(),
//                                "${fileInfo.name} success",
//                                Toast.LENGTH_SHORT
//                            ).show()
//
//                        } else {
//                            Toast.makeText(
//                                requireActivity(),
//                                "${fileInfo.name} exceeds maximum size of 5Mb",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
////                    try {
////                        val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, photoURI)
//////                        val image = drawTextToBitmap(bitmap, text = "Venkatesh")
////                        binding.ivProfile.setImageBitmap(bitmap)
////                        val file = File(photoURI!!.path)
////
////
////                    } catch (e: FileNotFoundException) {
////                        e.printStackTrace()
////                    } catch (e: IOException) {
////                        e.printStackTrace()
////                    }
//
//                }
//            }
        return inflater.inflate(R.layout.fragment_rfc_claim, container, false)
    }


}