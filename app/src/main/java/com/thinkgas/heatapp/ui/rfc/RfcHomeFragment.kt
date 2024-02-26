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
import com.thinkgas.heatapp.data.cache.AppCache
import com.thinkgas.heatapp.databinding.FragmentRfcHomeBinding
import com.thinkgas.heatapp.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RfcHomeFragment : Fragment() {
    private var _binding:FragmentRfcHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<RfcViewModel>()
    private val args by navArgs<RfcHomeFragmentArgs>()

    private var pendingNo:String?=null
    private var holdNo:String?=null
    private var doneNo:String?=null
    private var unclaimedNo:String?=null
    private var failedNo:String?=null
    private var approvedNo: String? = null
    private var declinedNo: String? = null
    private var dialog: Dialog? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRfcHomeBinding.inflate(inflater,container,false)
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.progress)
        dialog = builder.create()

        val params = HashMap<String,String>()
        params["session_id"]=args.sessionId
        params["is_tpi"] = if(AppCache.isTpi) "1" else "0"
        viewModel.getRfcInfoResponse(params)

        setUpObserver()

        binding.apply {
            if(AppCache.isTpi){
                cvApproved.visibility = View.VISIBLE
                cvDeclined.visibility = View.VISIBLE
                tvDone.text = "RFC Approval Pending"
                tvPending.text = "Supervisor RFC Claimed"
                tvHold.text = "Supervisor RFC Hold"
                tvClaim.text = "Supervisor RFC Unclaimed"
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
                    val directions = RfcHomeFragmentDirections.actionRfcHomeFragmentToLoginFragment()
                    findNavController().navigate(directions)

                }
                logoutBuilder.setNegativeButton("No") { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                val logoutAlert = logoutBuilder.create()
                logoutAlert.show()


            }

            cvPending.setOnClickListener {
                if(pendingNo != null && !pendingNo.equals("0")) {
                    val directions =
                        RfcHomeFragmentDirections.actionRfcHomeFragmentToRfcListFragment(args.sessionId,"pending")
                    findNavController().navigate(directions)
                }else{
                    Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                }
            }
            cvHold.setOnClickListener {
                if(holdNo != null && !holdNo.equals("0")) {
                    val directions =
                        RfcHomeFragmentDirections.actionRfcHomeFragmentToRfcListFragment(args.sessionId,"hold")
                    findNavController().navigate(directions)
                }else{
                    Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                }
            }
            cvDone.setOnClickListener {
                if(doneNo != null && !doneNo.equals("0")) {
                    val directions =
                        RfcHomeFragmentDirections.actionRfcHomeFragmentToRfcListFragment(args.sessionId,"done")
                    findNavController().navigate(directions)
                }else{
                    Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                }
            }
            cvClaim.setOnClickListener {
                if(unclaimedNo != null && !unclaimedNo.equals("0")) {
                    val directions =
                        RfcHomeFragmentDirections.actionRfcHomeFragmentToRfcListFragment(args.sessionId,"unclaimed")
                    findNavController().navigate(directions)
                }else{
                    Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                }
            }
            cvFailed.setOnClickListener {
                if(failedNo!= null && !failedNo.equals("0")) {
                    val directions =
                        RfcHomeFragmentDirections.actionRfcHomeFragmentToRfcListFragment(args.sessionId,"failed")
                    findNavController().navigate(directions)
                }else{
                    Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                }
            }
            cvApproved.setOnClickListener {
                if(approvedNo != null && !approvedNo.equals("0")) {
                    val directions =
                        RfcHomeFragmentDirections.actionRfcHomeFragmentToRfcListFragment(
                            sessionId = args.sessionId, status = "approved"
                        )
                    findNavController().navigate(directions)
                }else{
                    Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                }

            }

            cvDeclined.setOnClickListener {
                if(declinedNo != null && !declinedNo.equals("0")) {
                    val directions =
                        RfcHomeFragmentDirections.actionRfcHomeFragmentToRfcListFragment(
                            sessionId = args.sessionId, status = "declined"
                        )
                    findNavController().navigate(directions)
                }else{
                    Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                }

            }
        }
        return binding.root
    }

    private fun setUpObserver() {
        viewModel.rfcInfoResponse.observe(viewLifecycleOwner){
            if(it.data!=null){
                when(it.status){
                    Status.SUCCESS->{
                        setDialog(false)
                        if (it.data!=null) {
                            val data = it.data.agentList
                            pendingNo = data[0].pending.toString()
                            holdNo = data[0].hold.toString()
                            doneNo = data[0].done.toString()
                            unclaimedNo = data[0].unClaimed.toString()
                            failedNo = data[0].failed.toString()
                            declinedNo = data[0].declined.toString()
                            approvedNo = data[0].approved.toString()

                            binding.apply {
                                tvHoldNum.text = holdNo
                                tvDoneNum.text = doneNo
                                tvPendingNum.text = pendingNo
                                tvClaimNum.text = unclaimedNo
                                tvFaileNum.text = failedNo
                                tvApprovedNum.text = approvedNo
                                tvDeclinedNum.text = declinedNo
                            }
                        } else {
                            Toast.makeText(requireContext(), "${it.data}", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    Status.LOADING->{
                        setDialog(true)
                    }
                    Status.ERROR->{
                        setDialog(false)

                    }
                }
            }
        }
    }
    private fun setDialog(show: Boolean) {
        if (show) dialog?.show() else dialog?.dismiss()
    }
}