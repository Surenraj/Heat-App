package com.thinkgas.heatapp.ui.feasibility

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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.thinkgas.heatapp.R
import com.thinkgas.heatapp.data.cache.AppCache
import com.thinkgas.heatapp.data.remote.model.Agent
import com.thinkgas.heatapp.databinding.FragmentFeasibilityListBinding
import com.thinkgas.heatapp.databinding.TpiDialogBinding
import com.thinkgas.heatapp.ui.feasibility.adapters.FeasibilityListAdapter
import com.thinkgas.heatapp.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeasibilityListFragment : Fragment() {
    private var _binding: FragmentFeasibilityListBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<FeasibilityViewModel>()
    private lateinit var adapter: FeasibilityListAdapter
    private val args by navArgs<FeasibilityListFragmentArgs>()
    private var job: Job? = null
    private var searchJob: Job? = null
    private var dialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFeasibilityListBinding.inflate(inflater, container, false)

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.progress)
        dialog = builder.create()

        when(args.status){
            "unclaimed"->{
                adapter = FeasibilityListAdapter(requireContext(),true,args.status,{
                    listItemClicked(it)
                },{
                    claimItemClicked(it)
                }, {s1,s2->navigationClicked(s1,s2)},{mobile->callItemClicked(mobile)},{cancelItemClicked(it)},{supervisorClicked(it)},{infoItemClicked(it)})
            }
            else->{
                adapter = FeasibilityListAdapter(requireContext(),false,args.status,{
                    listItemClicked(it)
                },{
                    claimItemClicked(it)
                },{s1,s2->navigationClicked(s1,s2)},{mobile->callItemClicked(mobile)},{cancelItemClicked(it)},{supervisorClicked(it)},{infoItemClicked(it)})
            }
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
                    val directions = FeasibilityListFragmentDirections.actionFeasibilityListFragmentToLoginFragment()
                    findNavController().navigate(directions)

                }
                logoutBuilder.setNegativeButton("No") { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                val logoutAlert = logoutBuilder.create()
                logoutAlert.show()


            }

            when(args.status){
                "pending"->{
                    if(AppCache.isTpi){
                        tvTitle.text = "Supervisor Feasibility Claimed"
                    }else {
                        tvTitle.text = "Feasibility Pending"
                    }
                }
                "hold"->{
                    if(AppCache.isTpi){
                        tvTitle.text = "Supervisor Feasibility Hold"
                    }else {
                        tvTitle.text = "Feasibility Hold"
                    }
                }
                "done"->{
                    if(AppCache.isTpi){
                        tvTitle.text = "Feasibility Approval Pending"
                    }else {
                        tvTitle.text = "Feasibility Done"
                    }
                }
                "unclaimed"->{
                    if(AppCache.isTpi){
                        tvTitle.text = "Supervisor Feasibility Unclaimed"
                    }else {
                        tvTitle.text = "Feasibility Unclaimed"
                    }

                }
                "failed"->{
                    tvTitle.text = "Feasibility Failed"
                }
                "approved"->{
                    tvTitle.text = "Feasibility Approved"
                }

                "declined"->{
                    tvTitle.text = "Feasibility Declined"
                }
            }

            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.rvList.adapter = adapter

        getFsList(args.status,args.sessionId)

        binding.swiperefresh.setOnRefreshListener {
            getFsList(args.status,args.sessionId)
        }

//        viewModel.getFeasibilityList(args.status,args.sessionId)
//        setUpListObserver()
        binding.svList.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
//                viewModel.searchFeasibilityList(query!!, args.status)
//                setUpObserver()
                searchFsList(query!!,args.status,args.sessionId)
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                searchFsList(query!!,args.status,args.sessionId)
//                viewModel.searchFeasibilityList(newText!!, args.status)
//                setUpObserver()
                return false
            }

        })

        binding.svList.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                getFsList(args.status,args.sessionId)
                return false
            }

        })

        return binding.root
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

    private fun cancelItemClicked(it: Agent) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        builder.setTitle("Cancel Feasibility")
        builder.setMessage("Are you sure want to cancel the job?")
        builder.setPositiveButton("Yes",object : DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                cancelFeasibility(it)
            }

        })
        builder.setNegativeButton("No",null)
        val alert = builder.create()
        alert.show()
    }

    private fun cancelFeasibility(agent: Agent) {
        val params = HashMap<String,String>()
        params["session_id"]=args.sessionId
        params["application_number"]=agent.applicationNumber
        params["bpnumber"]=agent.bpnumber

        viewModel.cancelFeasibility(params)
        setUpCancelObserver()
    }

    private fun setUpCancelObserver() {
        viewModel.cancelResponse.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.SUCCESS->{
                        setDialog(false)
                        getFsList(args.status,args.sessionId)
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

    private fun callItemClicked(mobile: String) {
        val i = Intent(Intent.ACTION_DIAL)
        val p = "tel:" + mobile
        i.data = Uri.parse(p)
        startActivity(i)
    }

    private fun navigationClicked(lat:String,lon:String){
        val directions = FeasibilityListFragmentDirections.actionFeasibilityListFragmentToMapFragment2(
            latitude = lat, longitude = lon
        )
        findNavController().navigate(directions)
    }

    private fun getFsList(status:String, sessionId:String){
        listenAdapter()
        job = lifecycleScope.launch(Dispatchers.Main) {
            viewModel.fsResponse = null
            viewModel.getFeasibilityList(
                status
            ,sessionId
            ).distinctUntilChanged().collectLatest {
                adapter.submitData(it)
                binding.swiperefresh.isRefreshing = false
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun listenAdapter() {
        adapter.addLoadStateListener {loadState->

            if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && adapter.itemCount < 1) {
                binding.rvList.isVisible = false
                binding.tvError.isVisible = true
                setDialog(false)
            }else if(loadState.source.refresh is LoadState.Loading){
                setDialog(true)
            } else {
                setDialog(false)
                binding.rvList.isVisible = true
                binding.tvError.isVisible = false
            }
        }
    }

    private fun searchFsList(query:String, status: String,sessionId: String){
        listenAdapter()
        searchJob = lifecycleScope.launch(Dispatchers.Main) {
            viewModel.searchResponse = null
            viewModel.searchFeasibilityList(
                query,
                status,
                sessionId
            ).distinctUntilChanged().collectLatest {
                adapter.submitData(it)
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun setUpClaimObserver() {
        viewModel.claimResponse.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.SUCCESS->{
                        setDialog(false)
                        getFsList(args.status,args.sessionId)
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

    private fun setUpTpiClaimObserver() {
        viewModel.tpiTfsClaimResponse.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.SUCCESS->{
                        setDialog(false)
                        getFsList(args.status,args.sessionId)
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
                    tpiTfsClaim(agent)
                }

            })
            builder.setNegativeButton("No", null)
            val alert = builder.create()
            alert.show()
        }else {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setCancelable(false)
            builder.setTitle("Feasibility Job")
            builder.setMessage("Are you sure want to start the job?")
            builder.setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    claimFeasibility(agent)
                }
            })
            builder.setNegativeButton("No", null)
            val alert = builder.create()
            alert.show()
        }




    }

    private fun tpiTfsClaim(agent: Agent) {
        val params = HashMap<String,String>()
        params["session_id"]=args.sessionId
        params["application_number"]=agent.applicationNumber
        params["bpnumber"]=agent.bpnumber
        params["is_tpi"]=if(AppCache.isTpi) "1" else "0"

        viewModel.tpiTfsClaimResponse(params)
        setUpTpiClaimObserver()
    }

    private fun claimFeasibility(agent: Agent) {
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

        viewModel.updateClaimStatus(params)
        setUpClaimObserver()
    }

    private fun setUpListObserver() {
        viewModel.feasibilityResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.submitData(lifecycle, it)
//                if(adapter.itemCount==0){
//                    binding.tvError.visibility = View.VISIBLE
//                }else{
//                    binding.tvError.visibility = View.GONE
//                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvList.adapter = null
        _binding = null
        viewModel.stopAllJobs()
        job?.cancel("cancelled")
        searchJob?.cancel("cancelled")

    }

    private fun listItemClicked(it: Agent) {
        when(args.status){
            "hold","done","failed"->{
                val directions =
                    FeasibilityListFragmentDirections.actionFeasibilityListFragmentToFeasibilityStatusFragment(
                        customerName = it.customerName,
                        mobile = it.mobileNo,
                        address = it.address,
                        sessionId = args.sessionId,
                        appNo = it.applicationNumber,
                        bpNo = it.bpnumber,
                        status = args.status,
                        description = it.description,
                        statusTypeId = it.statusTypeId,
                        subStatusId = it.subStatusId,
                        pipeLineId = it.pipelineId,
                        riserStatus = it.riserStatus,
                        riserLength = it.riserLength,
                        pipeLength = it.giPipelength,
                        gcStatus = it.gcStatus,
                        srNo = it.srNo,
                        drsNo = it.drsNo,
                        mlcLength = it.mlcPipelength
                    )
                findNavController().navigate(directions)
            }
            "pending"->{
                if(!AppCache.isTpi) {
                    val directions =
                        FeasibilityListFragmentDirections.actionFeasibilityListFragmentToFeasibilityStatusFragment(
                            customerName = it.customerName,
                            mobile = it.mobileNo,
                            address = it.address,
                            sessionId = args.sessionId,
                            appNo = it.applicationNumber,
                            bpNo = it.bpnumber,
                            status = args.status,
                            description = null,
                        )
                    findNavController().navigate(directions)
                }else{
                    Toast.makeText(requireContext(), "TPI action not allowed", Toast.LENGTH_SHORT).show()
                }
            }
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