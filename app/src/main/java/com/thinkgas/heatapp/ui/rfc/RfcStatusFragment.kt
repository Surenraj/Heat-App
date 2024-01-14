package com.thinkgas.heatapp.ui.rfc

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.thinkgas.heatapp.R
import com.thinkgas.heatapp.databinding.FragmentRfcStatusBinding
import com.thinkgas.heatapp.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RfcStatusFragment : Fragment() {

    private var _binding:FragmentRfcStatusBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<RfcStatusViewModel>()
    private val args by navArgs<RfcStatusFragmentArgs>()
    private var dialog: Dialog? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRfcStatusBinding.inflate(inflater,container,false)

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.progress)
        dialog = builder.create()

        val params = HashMap<String,String>()
        params["application_number"] = args.appNo
        params["session_id"] =  args.sessionId
        viewModel.getRfcStatus(params)
        setUpObserver()
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
                    val directions = RfcStatusFragmentDirections.actionRfcStatusFragmentToLoginFragment()
                    findNavController().navigate(directions)

                }
                logoutBuilder.setNegativeButton("No") { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                val logoutAlert = logoutBuilder.create()
                logoutAlert.show()


            }

            btnSubmit.setOnClickListener {
                val directions = RfcStatusFragmentDirections.actionRfcStatusFragmentToRfcApprovalFragment(
                    appNo = args.appNo,
                    bpNo = args.bpNo,
                    sessionId = args.sessionId,
                    tpiId = args.tpiId,
                    customerInfo = args.customerInfo,
                    status = args.status
                    )
                findNavController().navigate(directions)
            }
        }
        return binding.root
    }

    private fun setUpObserver() {
        viewModel.rfcStatusResponse.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.LOADING->{
                        setDialog(true)
                    }
                    Status.SUCCESS->{
                        setDialog(false)
                        if(!it.data!!.error){

                            binding.apply {
                                val customerInfo = it.data.customerInfo
                                val installationDetails = it.data.installationDetails

                                if(!installationDetails.lmcStatus!!.contains("with meter",true)){
//                                    clSrNo.visibility = View.GONE
//                                    clDrsNo.visibility = View.GONE
                                    clMeterMake.visibility = View.GONE
                                    clMeterNo.visibility = View.GONE
                                    clMeterTyper.visibility = View.GONE
                                    clInitialReading.visibility = View.GONE
                                    clRegulatorNo.visibility = View.GONE
//                                    clTypeNr.visibility = View.GONE
                                }

                                tvBpNo.text = customerInfo.bpNumber
                                tvAplicationNo.text = customerInfo.applicationNumber
                                tvCustomerName.text = customerInfo.customerName
                                tvMobileNo.text = customerInfo.mobile
                                tvEmail.text = customerInfo.email
                                tvAddress.text = customerInfo.address
//                                tvSrNo.text = installationDetails.srNo
//                                tvDrsNo.text = installationDetails.drsNo
                                tvRfcType.text = installationDetails.lmcType
                                tvContractorName.text = installationDetails.contractorName
                                tvSupervisorName.text = installationDetails.supervisorName
                                tvMeterMake.text = installationDetails.meterMake
                                tvMeterType.text = installationDetails.meterType
                                tvMeterNo.text = installationDetails.meterNo
                                tvInitialReading.text = installationDetails.initialReading
//                                tvTypeNr.text = "-"
                                tvRegulatorNo.text = installationDetails.regulatorNo
                                tvGiInstallation.text = installationDetails.guInstallation
                                tvCuInstallation.text = installationDetails.cuInstallation
                                tvIvNo.text = installationDetails.noOfIv
                                tvAvNo.text = installationDetails.noOfAv
                                tvPvcSleeve.text = if(installationDetails.pvcSleeve == "1") "Avail" else "Not Available"
                                tvExtraPl.text = installationDetails.extraPipelength
                                tvClamping.text = if(installationDetails.clamping == "1") "Done" else "Not Done"
                                tvMeterInstallation.text = if(installationDetails.meterInstallation == "1") "Done" else "Not Done"
                                tvCoh.text = if(installationDetails.cementingOfHoles == "1") "Done" else "Not Done"
                                tvPainting.text = if(installationDetails.paintingOfGiPipe == "1") "Done" else "Not Done"
                                tvGasType.text = installationDetails.gasType
                                tvPropertyType.text = installationDetails.propertyType
                                tvTfAvail.text = if(installationDetails.tfAvail == "1") "Yes" else "No"
                                tvConnectivity.text = if(installationDetails.connectivity == "1") "Yes" else "No"
                                tvEc.text = if(installationDetails.endCap == "1") "Yes" else "No"
                                tvHd.text = if(installationDetails.holeDrilled == "1") "Yes" else "No"
                                tvMcvTesting.text = if(installationDetails.mcvTesting == "1") "Yes" else "No"
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

    private fun setDialog(show: Boolean) {
        if (show) dialog!!.show() else dialog!!.dismiss()
    }
}