package com.thinkgas.heatapp.ui.gc

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
import com.thinkgas.heatapp.databinding.FragmentGcHomeBinding
import com.thinkgas.heatapp.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GcHomeFragment : Fragment() {

    private var _binding: FragmentGcHomeBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<GcHomeFragmentArgs>()
    private val viewModel by viewModels<GcViewModel>()
    private var dialog: Dialog? = null

    private var pendingNo:String?=null
    private var holdNo:String?=null
    private var doneNo:String?=null
    private var unclaimedNo:String?=null
    private var failedNo:String?=null
    private var approvedNo:String?=null
    private var declinedNo:String?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGcHomeBinding.inflate(inflater, container, false)
        val params = HashMap<String, String>()
        params["session_id"] = args.sessionId
        params["is_tpi"] = if(AppCache.isTpi) "1" else "0"
        viewModel.getGcInfo(params)

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.progress)
        dialog = builder.create()

        binding.apply {
            if(AppCache.isTpi){
                cvApproved.visibility = View.VISIBLE
                cvDeclined.visibility = View.VISIBLE
                tvDone.text = "GC Approval Pending"
                tvPending.text = "Supervisor GC Claimed"
                tvHold.text = "Supervisor GC Hold"
                tvClaim.text = "Supervisor GC Unclaimed"
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
                    val directions = GcHomeFragmentDirections.actionGcHomeFragmentToLoginFragment()
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
                        GcHomeFragmentDirections.actionGcHomeFragmentToGcListFragment(
                            sessionId = args.sessionId, status = "pending"
                        )
                    findNavController().navigate(directions)
                }else{
                    Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                }

            }
            cvHold.setOnClickListener {
                if(holdNo != null && !holdNo.equals("0")) {
                    val directions =
                        GcHomeFragmentDirections.actionGcHomeFragmentToGcListFragment(
                            sessionId = args.sessionId, status = "hold"
                        )
                    findNavController().navigate(directions)
                }else{
                    Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                }
            }
            cvDone.setOnClickListener {
                if(doneNo != null && !doneNo.equals("0")) {
                    val directions =
                        GcHomeFragmentDirections.actionGcHomeFragmentToGcListFragment(
                            sessionId = args.sessionId, status = "done"
                        )
                    findNavController().navigate(directions)
                }else{
                    Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                }

            }
            cvClaim.setOnClickListener {
                if(unclaimedNo != null && !unclaimedNo.equals("0")) {
                    val directions =
                        GcHomeFragmentDirections.actionGcHomeFragmentToGcListFragment(
                            sessionId = args.sessionId, status = "unclaimed"
                        )
                    findNavController().navigate(directions)
                }else{
                    Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                }

            }

            cvFailed.setOnClickListener {
                if(failedNo != null && !failedNo.equals("0")) {
                    val directions =
                        GcHomeFragmentDirections.actionGcHomeFragmentToGcListFragment(
                            sessionId = args.sessionId, status = "failed"
                        )
                    findNavController().navigate(directions)
                }else{
                    Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                }

            }

            cvApproved.setOnClickListener {
                if(approvedNo != null && !approvedNo.equals("0")) {
                    val directions =
                        GcHomeFragmentDirections.actionGcHomeFragmentToGcListFragment(
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
                        GcHomeFragmentDirections.actionGcHomeFragmentToGcListFragment(
                            sessionId = args.sessionId, status = "declined"
                        )
                    findNavController().navigate(directions)
                }else{
                    Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                }

            }

            viewModel.gcInfoResponse.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it.status) {
                        Status.LOADING -> {
                            setDialog(true)
                        }
                        Status.SUCCESS -> {
                            if (it.data!!.agentList.isNotEmpty()) {
                                val data = it.data
                                holdNo = data.agentList[0].hold.toString()
                                doneNo = data.agentList[0].done.toString()
                                pendingNo = data.agentList[0].pending.toString()
                                unclaimedNo = data.agentList[0].unClaimed.toString()
                                failedNo = data.agentList[0].failed.toString()
                                declinedNo = data.agentList[0].declined.toString()
                                approvedNo = data.agentList[0].approved.toString()

                                binding.apply {
                                    tvHoldNum.text = holdNo
                                    tvDoneNum.text = doneNo
                                    tvPendingNum.text = pendingNo
                                    tvClaimNum.text = unclaimedNo
                                    tvFaileNum.text = failedNo
                                    tvApprovedNum.text = approvedNo
                                    tvDeclinedNum.text = declinedNo
                                }
                            }
                            setDialog(false)
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

        return binding.root
    }

    private fun setDialog(show: Boolean) {
        if (show) dialog!!.show() else dialog!!.dismiss()
    }
}