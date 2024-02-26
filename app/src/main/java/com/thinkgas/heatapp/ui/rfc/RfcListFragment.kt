package com.thinkgas.heatapp.ui.rfc

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.thinkgas.heatapp.R
import com.thinkgas.heatapp.data.cache.AppCache
import com.thinkgas.heatapp.data.remote.model.Agent
import com.thinkgas.heatapp.databinding.FragmentRfcListBinding
import com.thinkgas.heatapp.databinding.TpiDialogBinding
import com.thinkgas.heatapp.ui.ng.NgApprovalFragment
import com.thinkgas.heatapp.ui.rfc.adapters.RfcListAdapter
import com.thinkgas.heatapp.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RfcListFragment : Fragment() {
    private var _binding:FragmentRfcListBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<RfcViewModel>()
    private val args by navArgs<RfcListFragmentArgs>()
    private lateinit var adapter: RfcListAdapter
    private var job: Job? = null
    private var dialog: Dialog? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRfcListBinding.inflate(inflater,container,false)
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.progress)
        dialog = builder.create()

        when(args.status){
            "unclaimed"->{
                adapter = RfcListAdapter(requireContext(),true,args.status,{
                    listItemClicked(it)
                },{
                    infoItemClicked(it)
                },{
                    claimItemClicked(it)
                },{s1,s2->navigationClicked(s1,s2)},
                    {mobile->callItemClicked(mobile)},{cancelItemClicked(it)},{supervisorClicked(it)})
            }
            else->{
                adapter = RfcListAdapter(requireContext(),false,args.status,{
                    listItemClicked(it)
                },{
                    infoItemClicked(it)
                },{
                    claimItemClicked(it)
                },{s1,s2->navigationClicked(s1,s2)},
                    {mobile->callItemClicked(mobile)},{cancelItemClicked(it)},{supervisorClicked(it)})
            }
        }

        NgApprovalFragment.apply {
            ngStatus = null
            ngStatusId = null
            rfcStatus = null

            initialReading = null
            burnerDetails = null
            conversionDate = null
            acknowledgeId = null

            mmtStatus = null
            leakageStatus = null
            gasPressure = null
            finalReading = null
            burnerType = null
            hoseLength = null
            nozzle65Length = null
            nozzle90Length = null
            nozzle110Length = null
            nozzle125Length = null
            drsNumber = null
            srNumber = null
        }


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
                    val directions = RfcListFragmentDirections.actionRfcListFragmentToLoginFragment()
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

            rvList.adapter = adapter
            when(args.status){
                "pending"->{
                    if(AppCache.isTpi){
                        tvTitle.text = "Supervisor RFC Claimed"
                    }else {
                        tvTitle.text = "RFC Pending"
                    }
                }
                "hold"->{
                    if(AppCache.isTpi){
                        tvTitle.text = "Supervisor RFC Hold"
                    }else {
                        tvTitle.text = "RFC Hold"
                    }
                }
                "done"->{
                    if(AppCache.isTpi){
                        tvTitle.text = "RFC Approval Pending"
                    }else {
                        tvTitle.text = "RFC Done"
                    }
                }
                "unclaimed"->{
                    if(AppCache.isTpi){
                        tvTitle.text = "Supervisor RFC Unclaimed"
                    }else {
                        tvTitle.text = "RFC Unclaimed"
                    }

                }
                "failed"->{
                    tvTitle.text = "RFC Failed"
                }
                "approved"->{
                    tvTitle.text = "RFC Approved"
                }

                "declined"->{
                    tvTitle.text = "RFC Declined"
                }
            }
        }

        getRfcList(args.sessionId,args.status)

        binding.swiperefresh.setOnRefreshListener {
            getRfcList(args.sessionId,args.status)
        }
        binding.svList.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchRfcList(args.sessionId,query!!,args.status)
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                searchRfcList(args.sessionId,query!!,args.status)

                return false
            }

        })

        binding.svList.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
//                setUpListObserver()
                getRfcList(args.sessionId,args.status)
                return false
            }

        })

        return binding.root
    }

    private fun cancelItemClicked(it: Agent) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        builder.setTitle("Cancel RFC")
        builder.setMessage("Are you sure want to cancel the job?")
        builder.setPositiveButton("Yes",object : DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                cancelRfc(it)
            }

        })
        builder.setNegativeButton("No",null)
        val alert = builder.create()
        alert.show()
    }

    private fun cancelRfc(agent: Agent) {
        val params = HashMap<String,String>()
        params["session_id"]=args.sessionId
        params["application_number"]=agent.applicationNumber
        params["bpnumber"]=agent.bpnumber

        viewModel.cancelRfc(params)
        setUpCancelObserver()
    }

    private fun setUpCancelObserver() {
        viewModel.cancelResponse.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.SUCCESS->{
                        getRfcList(args.sessionId,args.status)
                    }
                    else->{

                    }
                }
            }
        }
    }

    private fun callItemClicked(mobile: String) {
        val i = Intent(Intent.ACTION_DIAL)
        val p = "tel:" + mobile
        i.data = Uri.parse(p)
        startActivity(i)
    }

    private fun navigationClicked(s1: String, s2: String) {
        val directions = RfcListFragmentDirections.actionRfcListFragmentToMapFragment2(
            latitude = s1, longitude = s2
        )
        findNavController().navigate(directions)
    }

    private fun listenAdapter() {
        adapter.addLoadStateListener {loadState->
            if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && adapter.itemCount < 1) {
                binding.rvList.isVisible = false
                setDialog(false)
                binding.tvError.isVisible = true
            } else if(loadState.source.refresh is LoadState.Loading){
                setDialog(true)
            }else {
                setDialog(false)
                binding.rvList.isVisible = true
                binding.tvError.isVisible = false
            }
        }
    }

    private fun getRfcList(sessionId: String, status: String) {
        listenAdapter()
        job = lifecycleScope.launch(Dispatchers.Main) {
            viewModel.rfcResponse = null
            viewModel.getRfcList(
                sessionId,
                status
            ).distinctUntilChanged().collectLatest {
                adapter.submitData(it)
                binding.swiperefresh.isRefreshing = false
            }
        }
        adapter.notifyDataSetChanged()
    }

    fun searchRfcList(sessionId:String,query:String,status: String){
        listenAdapter()
        job = lifecycleScope.launch(Dispatchers.Main) {
            viewModel.searchResponse = null
            viewModel.searchRfcList(
                sessionId,
                query,
                status
            ).distinctUntilChanged().collectLatest {
                adapter.submitData(it)
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun supervisorClicked(agent: Agent) {
        val dialogBinding = TpiDialogBinding.inflate(LayoutInflater.from(requireContext()))
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        dialogBinding.tvNameTitle.text = "Supervisor Name"
        dialogBinding.tvName.text = agent.supervisorName
        dialogBinding.tvMobile.text = agent.supervisorMobile
        builder.setCancelable(false)
        builder.setView(dialogBinding.root)
        val alert = builder.create()
        alert.show()

        dialogBinding.tvMobile.setOnClickListener {
            val i = Intent(Intent.ACTION_DIAL)
            val p = "tel:" + agent.supervisorMobile
            i.data = Uri.parse(p)
            startActivity(i)
        }

        dialogBinding.ivClose.setOnClickListener {
            alert.dismiss()
        }
    }


    private fun infoItemClicked(agent: Agent) {
        val dialogBinding = TpiDialogBinding.inflate(LayoutInflater.from(requireContext()))
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        dialogBinding.tvName.text = agent.TpiName
        dialogBinding.tvMobile.text = agent.tpiMobileNo
        builder.setCancelable(false)
        builder.setView(dialogBinding.root)
        val alert = builder.create()
        alert.show()

        dialogBinding.tvMobile.setOnClickListener {
            val i = Intent(Intent.ACTION_DIAL)
            val p = "tel:" + agent.tpiMobileNo
            i.data = Uri.parse(p)
            startActivity(i)
        }

        dialogBinding.ivClose.setOnClickListener {
            alert.dismiss()
        }

    }

    private fun claimItemClicked(agent: Agent) {
        if(AppCache.isTpi){
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setCancelable(false)
            if(agent.tpiApprovalStatus == "Claim"){
                builder.setTitle("TPI Unclaim Job")
                builder.setMessage("Are you sure want to unclaim the job?")
            }else{
                builder.setTitle("TPI Claim Job")
                builder.setMessage("Are you sure want to claim the job?")
            }

            builder.setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    tpiRfcClaim(agent)
                }

            })
            builder.setNegativeButton("No", null)
            val alert = builder.create()
            alert.show()
        }else {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setCancelable(false)
            builder.setTitle("Claim RFC")
            builder.setMessage("Are you sure want to claim RFC?")
            builder.setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    claimRfc(agent)
                }

            })
            builder.setNegativeButton("No", null)
            val alert = builder.create()
            alert.show()

        }
    }

    private fun tpiRfcClaim(agent: Agent) {
        val params = HashMap<String,String>()
        params["session_id"]=args.sessionId
        params["application_number"]=agent.applicationNumber
        params["bpnumber"]=agent.bpnumber
        params["is_tpi"]=if(AppCache.isTpi) "1" else "0"

        viewModel.tpiRfcClaimUpdate(params)
        setUpTpiClaimObserver()
    }

    private fun setUpTpiClaimObserver() {
        viewModel.tpiRfcClaimResponse.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.SUCCESS->{
                        setDialog(false)
                        getRfcList(args.sessionId,args.status)
                        Toast.makeText(requireContext(), it.data?.message, Toast.LENGTH_SHORT).show()
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



    private fun claimRfc(agent: Agent) {
        val params = HashMap<String,String>()
        params["session_id"]=args.sessionId
        params["application_number"]=agent.applicationNumber
        params["bpnumber"]=agent.bpnumber
        params["claimed_status"]="Initiated"
        params["claimed_by"]=args.sessionId
        params["tpi_id"]=agent.tpiId.toString()
        params["geo_id"]=agent.geoId.toString()
        params["zonal_id"] = agent.zonalId.toString()
        params["customer_info"] = agent.customerName

        viewModel.updateRfcClaimStatus(params)
        setUpClaimObserver()
    }

    private fun setUpClaimObserver() {
        viewModel.rfcClaimResponse.observe(viewLifecycleOwner){
            if(it.data!=null){
                when(it.status){
                    Status.LOADING->{
                        setDialog(true)
                    }
                    Status.SUCCESS->{
                        setDialog(false)
                        if(!it.data.error){
                            job?.cancel("cancelled")
                            getRfcList(args.sessionId,args.status)
                        }else{
                            Toast.makeText(requireContext(), "Unable to claim request", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Status.ERROR->{
                        setDialog(false)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel("cancelled")
        binding.rvList.adapter = null
        _binding = null
    }



    private fun listItemClicked(it: Agent) {
        if(AppCache.isTpi && args.status == "pending"){
            Toast.makeText(requireContext(), "TPI action not allowed", Toast.LENGTH_SHORT).show()
        }else {
            val directions = RfcListFragmentDirections.actionRfcListFragmentToRfcStatusFragment(
                sessionId = args.sessionId,
                appNo = it.applicationNumber,
                bpNo = it.bpnumber,
                tpiId = it.tpiId.toString(),
                customerInfo = it.customerName,
                status = args.status
            )
            findNavController().navigate(directions)
        }
    }
    private fun setDialog(show: Boolean) {
        if (show) dialog?.show() else dialog?.dismiss()
    }


}