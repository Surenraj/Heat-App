package com.thinkgas.heatapp.ui.gc

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
import com.thinkgas.heatapp.databinding.FragmentGcListBinding
import com.thinkgas.heatapp.databinding.TpiDialogBinding
import com.thinkgas.heatapp.ui.gc.adapters.GcListAdapter
import com.thinkgas.heatapp.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GcListFragment : Fragment() {

    private var _binding: FragmentGcListBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<GcViewModel>()
    private lateinit var adapter: GcListAdapter
    private val args by navArgs<GcListFragmentArgs>()
    private var job: Job? = null
    private var searchJob: Job? = null
    private var dialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGcListBinding.inflate(inflater, container, false)

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.progress)
        dialog = builder.create()

        when(args.status) {
            "unclaimed"->{
                adapter = GcListAdapter(requireContext(),true,args.status,{
                    listItemClicked(it)
                },{
                    claimItemClicked(it)
                }, {s1,s2->navigationClicked(s1,s2)},{mobile->callItemClicked(mobile)},{cancelItemClicked(it)},{supervisorClicked(it)},{infoItemClicked(it)})
            }
            else->{
                adapter = GcListAdapter(requireContext(),false,args.status,{
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
                    val directions = GcListFragmentDirections.actionGcListFragmentToLoginFragment()
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
                        tvTitle.text = "Supervisor GC Claimed"
                    }else {
                        tvTitle.text = "GC Pending"
                    }
                }
                "hold"->{
                    if(AppCache.isTpi){
                        tvTitle.text = "Supervisor GC Hold"
                    }else {
                        tvTitle.text = "GC Hold"
                    }
                }
                "done"->{
                    if(AppCache.isTpi){
                        tvTitle.text = "GC Approval Pending"
                    }else {
                        tvTitle.text = "GC Done"
                    }
                }
                "unclaimed"->{
                    if(AppCache.isTpi){
                        tvTitle.text = "Supervisor GC Unclaimed"
                    }else {
                        tvTitle.text = "GC Unclaimed"
                    }

                }
                "failed"->{
                    tvTitle.text = "GC Failed"
                }
                "approved"->{
                    tvTitle.text = "GC Approved"
                }

                "declined"->{
                    tvTitle.text = "GC Declined"
                }
            }

            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.rvList.adapter = adapter

        getGcList(args.status,args.sessionId)

        binding.swiperefresh.setOnRefreshListener {
            getGcList(args.status,args.sessionId)
        }

        binding.svList.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
//                viewModel.searchFeasibilityList(query!!, args.status)
//                setUpObserver()
                searchGcList(query!!,args.status,args.sessionId)
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                searchGcList(query!!,args.status,args.sessionId)
//                viewModel.searchFeasibilityList(newText!!, args.status)
//                setUpObserver()
                return false
            }

        })

        binding.svList.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                getGcList(args.status,args.sessionId)
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

        viewModel.cancelGc(params)
        setUpCancelObserver()
    }

    private fun setUpCancelObserver() {
        viewModel.gcCancelResponse.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.SUCCESS->{
                        setDialog(false)
                        getGcList(args.status,args.sessionId)
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
        val directions = GcListFragmentDirections.actionGcListFragmentToMapFragment2(
            latitude = lat, longitude = lon
        )
        findNavController().navigate(directions)
    }

    private fun getGcList(status:String, sessionId:String){
        listenAdapter()
        job = lifecycleScope.launch(Dispatchers.Main) {
            viewModel.gcResponse = null
            viewModel.getGcList(
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

    private fun searchGcList(query:String, status: String,sessionId: String){
        listenAdapter()
        searchJob = lifecycleScope.launch(Dispatchers.Main) {
            viewModel.searchResponse = null
            viewModel.searchGcList(
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
        viewModel.gcClaimStatus.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.SUCCESS->{
                        setDialog(false)
                        getGcList(args.status,args.sessionId)
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
        viewModel.tpiGcClaimStatus.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.SUCCESS->{
                        setDialog(false)
                        getGcList(args.status,args.sessionId)
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

        viewModel.tpiGcClaimStatus(params)
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

        viewModel.updateGcClaimStatus(params)
        setUpClaimObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvList.adapter = null
        _binding = null
        job?.cancel("cancelled")
        searchJob?.cancel("cancelled")
    }

    private fun listItemClicked(it: Agent) {
        when(args.status){
            "hold","done","failed"->{
                val directions =
                    GcListFragmentDirections.actionGcListFragmentToGcStatusFragment(
                        customerName = it.customerName,
                        mobile = it.mobileNo,
                        address = it.address,
                        sessionId = args.sessionId,
                        appNo = it.applicationNumber,
                        bpNo = it.bpnumber,
                        status = args.status,
                        gcDate = it.gcDate,
                        gcNumber = it.gcNumber,
                        description = it.description,
                        folloUpDate = it.followUpDate,
                        gcApplication = it.gcApplication,
                        lmcStatus = it.lmcStatus,
                        gcType = it.gcType,
                        lmcGcAlignment = it.lmcGcAlignment,
                        gcContractor = it.gcContractor ,
                        gcSupervisor = it.gcSupervisor,
                        statusTypeId = it.statusTypeId,
                        subStatusId = it.subStatusId,
                        potentialId = it.potential, type = "gc_list", gcModel = null,

                    )
                findNavController().navigate(directions)
            }
            "pending"->{
                if(!AppCache.isTpi) {
                    val directions =
                        GcListFragmentDirections.actionGcListFragmentToGcStatusFragment(
                            customerName = it.customerName,
                            mobile = it.mobileNo,
                            address = it.address,
                            sessionId = args.sessionId,
                            appNo = it.applicationNumber,
                            bpNo = it.bpnumber,
                            status = args.status,
                            gcDate = "",
                            gcNumber = "",
                            description = it.description,
                            folloUpDate = it.followUpDate,
                            gcApplication = "",
                            lmcStatus = "",
                            gcType = "",
                            lmcGcAlignment = "",
                            gcContractor = "",
                            gcSupervisor = "",
                            statusTypeId = 0,
                            subStatusId = 0,
                            potentialId = "",
                            type = "gc_list", gcModel = null,

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