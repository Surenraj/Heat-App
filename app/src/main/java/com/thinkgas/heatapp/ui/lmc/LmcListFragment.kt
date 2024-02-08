package com.thinkgas.heatapp.ui.lmc

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
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
import com.thinkgas.heatapp.data.remote.model.LmcConnectionModel
import com.thinkgas.heatapp.databinding.FragmentLmcListBinding
import com.thinkgas.heatapp.databinding.TpiDialogBinding
import com.thinkgas.heatapp.ui.lmc.adapters.LmcListAdapter
import com.thinkgas.heatapp.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LmcListFragment : Fragment() {
    private var _binding: FragmentLmcListBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<LmcViewModel>()
    private lateinit var adapter: LmcListAdapter
    private var dialog: Dialog? = null
    private val args by navArgs<LmcListFragmentArgs>()
    private var job: Job? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLmcListBinding.inflate(inflater, container, false)

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.progress)
        dialog = builder.create()

        LmcStatusFragment.apply {
            fsSubStatusCode = null
            fsSubStatus = null
            fsStatus = null
            fsStatusCode = null
            dateTime = null
            description = null
        }

        LmcConnectionFragment.apply {
            qrValue = null
            qrError = null
            lmcType = null
            lmcMeterDetail = null
            lmcMeterNumber = null
            lmcMeterId = null
            lmcMeterType = null
            lmcInitialReading = null
            lmcRegulator = null
            lmcGI = null
            lmcCU = null
            lmcNoOfAV = null
            lmcNoOfIV = null
            lmcPipeLength = null
            lmcProperty = null
            lmcGas = null
            lmcExtraMlclength = null
            lmcExtraGiLength = null
            lmcGiClamp = null
            lmcMlcClamp = null
            lmcGiMfElbow = null
            lmcGiFfElbow = null
            lmcGi2 = null
            lmcGi3 = null
            lmcGi4 = null
            lmcGi6 = null
            lmcGi8 = null
            lmcGiTee = null
            lmcMlcTee = null
            lmcGiSocket = null
            lmcMaleUnion = null
            lmcFemaleUnion = null
            lmcMeterBracket = null
            lmcMeterSticker = null
            lmcPlateMarker = null
            lmcAdaptorGI = null
            lmcAdaptorReg = null
            lmcAdaptorMeter = null
            lmcFemaleMeter = null
        }



        when(args.status){
            "unclaimed"->{
                adapter = LmcListAdapter(requireContext(),true,args.status,{
                    listItemClicked(it)
                },{
                  infoItemClicked(it)
                },{
                    claimItemClicked(it)
                },{s1,s2->navigationClicked(s1,s2)},{cancelItemClicked(it)},{supervisorClicked(it)})
            }
            else->{
                adapter = LmcListAdapter(requireContext(),false,args.status,{
                    listItemClicked(it)
                },{
                    infoItemClicked(it)
                },{
                    claimItemClicked(it)
                },{s1,s2->navigationClicked(s1,s2)},{cancelItemClicked(it)},{supervisorClicked(it)})
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
                    val directions = LmcListFragmentDirections.actionLmcListFragmentToLoginFragment()
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

            when(args.status){
                "pending"->{
                    if(AppCache.isTpi){
                        tvTitle.text = "Supervisor LMC Claimed"
                    }else {
                        tvTitle.text = "LMC Pending"
                    }
                }
                "hold"->{
                    if(AppCache.isTpi){
                        tvTitle.text = "Supervisor LMC Hold"
                    }else {
                        tvTitle.text = "LMC Hold"
                    }
                }
                "done"->{
                    if(AppCache.isTpi){
                        tvTitle.text = "LMC Approval Pending"
                    }else {
                        tvTitle.text = "LMC Done"
                    }
                }
                "unclaimed"->{
                    if(AppCache.isTpi){
                        tvTitle.text = "Supervisor LMC Unclaimed"
                    }else {
                        tvTitle.text = "LMC Unclaimed"
                    }

                }
                "failed"->{
                    tvTitle.text = "LMC Failed"
                }
                "approved"->{
                    tvTitle.text = "LMC Approved"
                }

                "declined"->{
                    tvTitle.text = "LMC Declined"
                }
            }

        }

        binding.rvList.adapter = adapter

        val params = HashMap<String, String>()
        params["session_id"] = args.sessionId
        params["limit_per_page"] = "100"
        params["next_page_offset"] = "1"

//        viewModel.getLmcList(params)
//        setUpListObserver()
        getLmcList(args.sessionId,args.status)

        binding.swiperefresh.setOnRefreshListener {
            getLmcList(args.sessionId,args.status)
        }
        binding.svList.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchLmcList(args.sessionId,query!!,args.status)
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                searchLmcList(args.sessionId,query!!,args.status)

                return false
            }

        })

        binding.svList.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
//                setUpListObserver()
                getLmcList(args.sessionId,args.status)
                return false
            }

        })

        return binding.root
    }

    private fun cancelItemClicked(it: Agent) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        builder.setTitle("Cancel LMC")
        builder.setMessage("Are you sure want to cancel the job?")
        builder.setPositiveButton("Yes",object : DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                cancelLmc(it)
            }

        })
        builder.setNegativeButton("No",null)
        val alert = builder.create()
        alert.show()
    }

    private fun cancelLmc(agent: Agent) {
        val params = HashMap<String,String>()
        params["session_id"]=args.sessionId
        params["application_number"]=agent.applicationNumber
        params["bpnumber"]=agent.bpnumber

        viewModel.cancelLmc(params)
        setUpCancelObserver()
    }

    private fun setUpCancelObserver() {
        viewModel.cancelResponse.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.SUCCESS->{
                        setDialog(false)
                        getLmcList(args.sessionId,args.status)
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

    private fun navigationClicked(s1: String, s2: String) {
        val directions = LmcListFragmentDirections.actionLmcListFragmentToMapFragment2(
            latitude = s1, longitude = s2
        )
        findNavController().navigate(directions)

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

    private fun getLmcList(sessionId: String, status: String){
        listenAdapter()
        job = lifecycleScope.launch(Dispatchers.Main) {
            viewModel.lmcResponse = null
            viewModel.getLmcList(
                sessionId,
                status
            ).distinctUntilChanged().collectLatest {
                adapter.submitData(it)
                binding.swiperefresh.isRefreshing = false
            }
        }
        adapter.notifyDataSetChanged()
    }

    fun searchLmcList(sessionId:String,query:String,status: String){
        listenAdapter()
        job = lifecycleScope.launch(Dispatchers.Main) {
            viewModel.searchResponse = null
            viewModel.searchLmcList(
                sessionId,
                query,
                status
            ).distinctUntilChanged().collectLatest {
                adapter.submitData(it)
            }
        }
        adapter.notifyDataSetChanged()
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
                    tpiLmcClaim(agent)
                }

            })
            builder.setNegativeButton("No", null)
            val alert = builder.create()
            alert.show()
        }else {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setCancelable(false)
            builder.setTitle("Start LMC Job")
            builder.setMessage("Are you sure want to start the job?")
            builder.setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    claimLmc(agent)
                }

            })
            builder.setNegativeButton("No", null)
            val alert = builder.create()
            alert.show()
        }
    }

    private fun tpiLmcClaim(agent: Agent) {
        val params = HashMap<String,String>()
        params["session_id"]=args.sessionId
        params["application_number"]=agent.applicationNumber
        params["bpnumber"]=agent.bpnumber
        params["is_tpi"]=if(AppCache.isTpi) "1" else "0"

        viewModel.tpiLmcClaimUpdate(params)
        setUpTpiClaimObserver()
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


    private fun setUpTpiClaimObserver() {
        viewModel.tpiLmcClaimResponse.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.SUCCESS->{
                        setDialog(false)
                        getLmcList(args.sessionId,args.status)
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

    private fun claimLmc(agent: Agent) {
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

        viewModel.updateLmcStatus(params)
        setUpClaimObserver()
    }

    private fun setUpClaimObserver() {
        viewModel.claimStatus.observe(viewLifecycleOwner){
            if(it.data!=null){
                when(it.status){
                    Status.LOADING->{
                        setDialog(true)
                    }
                    Status.SUCCESS->{
                        setDialog(false)
                        if(!it.data.error){
                            job?.cancel("cancelled")
                            getLmcList(args.sessionId,args.status)
                        }else{
                            Toast.makeText(requireContext(), "Unable to claim request", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Status.ERROR->{
                        setDialog(false)
                        Toast.makeText(requireContext(), "Failed to claim", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun listItemClicked(it: Agent) {
        when(args.status){
            "pending"->{
                if(!AppCache.isTpi) {
                    if (it.claimStatus.contains("follow", true)) {
                        val directions =
                            LmcListFragmentDirections.actionLmcListFragmentToLmcConnectionFragment(
                                lmcStatusType = null,
                                lmcSubStatus = null,
                                appNo = it.applicationNumber,
                                bpNo = it.bpnumber,
                                customerInfo = it.customerName,
                                tpiId = it.tpiId.toString(),
                                followUpDate = null,
                                description = null,
                                sessionId = args.sessionId,
                                mobile = it.mobileNo,
                                firstName = it.firstName,
                                middleName = it.middleName,
                                lastName = it.lastName,
                                email = it.email,
                                status = args.status,
                                lmcExecution = it.lmcExecution,
                                lmcConnectionModel = null
                            )
                        findNavController().navigate(directions)
                    } else {
                        val directions =
                            LmcListFragmentDirections.actionLmcListFragmentToLmcStatusFragment(
                                customerName = it.customerName,
                                mobile = it.mobileNo,
                                address = it.address,
                                sessionId = args.sessionId,
                                appNo = it.applicationNumber,
                                bpNo = it.bpnumber,
                                firstName = it.firstName,
                                middleName = it.middleName,
                                lastName = it.lastName,
                                email = it.email,
                                tpiId = it.tpiId.toString(),
                                status = args.status,
                                lmcExecution = it.lmcExecution,
                                lmcConnectionModel = null
                            )
                        findNavController().navigate(directions)
                    }
                }else{
                    Toast.makeText(requireContext(), "TPI action not allowed", Toast.LENGTH_SHORT).show()
                }
            }
            "hold","done","failed"->{
                val directions =
                    LmcListFragmentDirections.actionLmcListFragmentToLmcStatusFragment(
                        customerName = it.customerName,
                        mobile = it.mobileNo,
                        address = it.address,
                        sessionId = args.sessionId,
                        appNo = it.applicationNumber,
                        bpNo = it.bpnumber,
                        firstName = it.firstName,
                        middleName = it.middleName,
                        lastName = it.lastName,
                        email = it.email,
                        tpiId = it.tpiId.toString(),
                        statusId = it.statusTypeId.toString(),
                        subStatusId = it.subStatusId.toString(),
                        followUpDate = it.followUpDate,
                        lmcType = it.lmcType,
                        meterDetails = it.meterDetails,
                        meterNo = it.meterNo,
                        meterType = it.meterType,
                        initialReading = it.initialReading,
                        regNo = it.regNo,
                        giNo = it.giMeter,
                        cuNo = it.cuMeter,
                        avNo = it.avNo,
                        ivNo = it.ivNo,
                        pipeLength = it.pipeLength,
                        propertyType = it.propertyType,
                        gasType = it.gasType,
                        pvcSleeve = it.pvcSleeve,
                        meterInstallation = it.meterInstallation,
                        gmTesting = it.gasTesting,
                        coh = it.coh,
                        clamping = it.clamping,
                        painting = it.paintingPipe,
                        tfAvail = it.tfAvail,
                        connectivity = it.connectivity,
                        endCap = it.endCap,
                        areagassified = it.areaGassified,
                        holeDrilled = it.holeDrilled,
                        mcvTesting = it.mcvTesting,
                        custStatus = it.custReadyStatus,
                        convStatus = it.ngConvDate,
                        status = args.status, statusType = it.statusType, substatus = it.subStatus,
                        meterSerialNo = it.meterSerialNumber,
                        confirmStatus = it.ackStatus,
                        extraGiLength = it.extraGiLength, extxraMlLength = it.extraMlcLength,
                        acTape = it.acTape,
                        description = it.description,
                        lmcExecution = it.lmcExecution,
                        lmcConnectionModel = LmcConnectionModel(
                            lmcGiClamp = it.giClamp,
                            lmcMlcClamp = it.mlcClamp,
                            lmcGiMfElbow = it.giMfElbow,
                            lmcGiFfElbow = it.giFfElbow,
                            lmcGi2 = it.gi2Nipple,
                            lmcGi3 = it.gi3Nipple,
                            lmcGi4 = it.gi4Nipple,
                            lmcGi6 = it.gi6Nipple,
                            lmcGi8 = it.gi8Nipple,
                            lmcGiTee = it.giTee,
                            lmcMlcTee = it.mlcTee,
                            lmcGiSocket = it.giSocket,
                            lmcMaleUnion = it.mlcMaleUnion,
                            lmcFemaleUnion = it.mlcFemaleUnion,
                            lmcMeterBracket = it.meterBracket,
                            lmcMeterSticker = it.meterSticker,
                            lmcPlateMarker = it.plateMarker,
                            lmcAdaptorGI = it.adaptorGi,
                            lmcAdaptorReg = it.adaptorReg,
                            lmcAdaptorMeter = it.adaptorMeter,
                            lmcFemaleMeter = it.femaleUnion

                        ),

                    )
                findNavController().navigate(directions)
            }
        }

    }

//    fun setUpObserver() {
//        viewModel.lmcResponse.observeOnce(viewLifecycleOwner) {
//            if (it != null) {
//                adapter.submitData(lifecycle, it)
////                if(adapter.itemCount==0){
////                    binding.tvError.visibility = View.VISIBLE
////                }else{
////                    binding.tvError.visibility = View.GONE
////                }
//            }
//        }
//    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvList.adapter = null
        _binding = null
        viewModel.stopAllJobs()
        job?.cancel("cancelled")
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
        if (show) dialog?.show() else dialog?.dismiss()
    }

}