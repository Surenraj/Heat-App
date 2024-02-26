package com.thinkgas.heatapp.ui.registration

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.thinkgas.heatapp.R
import com.thinkgas.heatapp.data.remote.model.Agent
import com.thinkgas.heatapp.data.remote.model.GcUnregisterListModel
import com.thinkgas.heatapp.databinding.FragmentGcUnregisteredListBinding
import com.thinkgas.heatapp.ui.registration.adapters.GcUnregisteredListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GcUnregisteredListFragment : Fragment() {

    private var _binding:FragmentGcUnregisteredListBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<GcUnregisteredListFragmentArgs>()
    private lateinit var adapter: GcUnregisteredListAdapter
    private var job: Job? = null
    private var searchJob: Job? = null
    private var dialog: Dialog? = null
    private val viewModel by viewModels<RegistrationViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGcUnregisteredListBinding.inflate(inflater,container,false)

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.progress)
        dialog = builder.create()

        RegistrationFragment.apply {
            gaName =null
            zonalName =null
            caName =null
            colonyName = null
            districtName = null
            talukaName = null
            cityName = null
            areaName = null
            pincode = null
            stateName = null
            gaId =null
            zonalId =null
            caId =null
            colonyId = null
            districtId = null
            talukaId = null
            cityId = null
            pincodeId = null
            stateId = null
            areaId = null
            floorfacing = null
            gassification = null
            floorNo = null
            gcStatus = null
            gcStatusCode = null
            gcDate = null
            gcPotential = null
            isFailed = false
        }

        adapter = GcUnregisteredListAdapter(requireContext(),
            {listItemClicked(it)},{s1,s2->navigationClicked(s1,s2)},{mobile->callItemClicked(mobile)})
        binding.apply {
            ivProfile.setOnClickListener {
                val directions = GcUnregisteredListFragmentDirections.actionGcUnregisteredListFragmentToRegistrationFragment(
                    sessionId = args.sessionId!!, gcUnregModel = null,
                )
                findNavController().navigate(directions)

            }

            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }

        }

        binding.rvList.adapter = adapter

        getGcUnregList(args.sessionId!!)

        binding.swiperefresh.setOnRefreshListener {
            getGcUnregList(args.sessionId!!)

        }

        binding.svList.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
//                viewModel.searchFeasibilityList(query!!, args.status)
//                setUpObserver()
                searchGcUnregList(query!!,args.sessionId!!)
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                searchGcUnregList(query!!,args.sessionId!!)
//                viewModel.searchFeasibilityList(newText!!, args.status)
//                setUpObserver()
                return false
            }

        })

        binding.svList.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                getGcUnregList(args.sessionId!!)
                return false
            }

        })

        return binding.root
    }

    private fun callItemClicked(mobile: String) {
        val i = Intent(Intent.ACTION_DIAL)
        val p = "tel:" + mobile
        i.data = Uri.parse(p)
        startActivity(i)
    }

    private fun navigationClicked(lat:String,lon:String){
        val directions = GcUnregisteredListFragmentDirections.actionGcUnregisteredListFragmentToMapFragment2(
            latitude = lat, longitude = lon
        )
        findNavController().navigate(directions)
    }

    private fun getGcUnregList(sessionId:String){
        listenAdapter()
        job = lifecycleScope.launch(Dispatchers.Main) {
            viewModel.gcResponse = null
            viewModel.getGcUnregList(
                sessionId
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

    private fun searchGcUnregList(query:String,sessionId: String){
        listenAdapter()
        searchJob = lifecycleScope.launch(Dispatchers.Main) {
            viewModel.searchResponse = null
            viewModel.searchGcUnregList(
                query,
                sessionId
            ).distinctUntilChanged().collectLatest {
                adapter.submitData(it)
            }
        }
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvList.adapter = null
        _binding = null
        job?.cancel("cancelled")
        searchJob?.cancel("cancelled")

    }

    private fun listItemClicked(it: Agent) {
        val directions = GcUnregisteredListFragmentDirections.actionGcUnregisteredListFragmentToRegistrationFragment(
            sessionId = args.sessionId!!,
            gcUnregModel = GcUnregisterListModel(
                customerName = it.customerName,
                mobileNumber = it.mobileNo,
                towerNo = it.towerNo!!,
                houseNo = it.houseNo!!,
                floorNo = it.floorNo!!,
                floorFacing = it.floorFacing!!,
                gassification = it.gassification!!,
                gaId = it.geoId.toString(),
                zonalId = it.zonalId.toString(),
                caId = it.chargeAreaId.toString(),
                colonyId = it.colonyId.toString(),
                districtId = it.districtId.toString(),
                talukaId = it.talukaId.toString(),
                cityId = it.cityId.toString(),
                areaId = it.areaId.toString(),
                pincodeId = it.pincodeId.toString(),
                gaName = it.ganame!!,
                zonalName = it.zonalName!!,
                caName = it.chargeAreaName!!,
                colonyName = it.colonyName ?: "NA",
                districtName = it.districtName!!,
                talukaName = it.talukaName!!,
                cityName = it.cityName!!,
                areaName = it.areaName!!,
                pincodeNo = it.pincode!!,
                state = it.stateName!!,
                stateId = it.stateId!!,
                landmark = it.landmark!!,
                statusType = it.statusType!!,
                subStatus = "",
                gcDate = it.gcDate!!,
                potential = it.potential!!,
                statusTypeId = it.statusTypeId,
                subStatusCode = it.subStatusId,
                lmcGcAlignment = it.lmcGcAlignment!!,
                lmcStatus = it.lmcStatus!!,
                gcStatus = it.gcType!!,
                gcContractor = it.gcContractor!!,
                gcSupervisor = it.gcSupervisorName!!,
                consentTaken = it.consentTaken!!,
                warningAvailable = it.warningAvailable!!,
            ),
        )
        findNavController().navigate(directions)
    }

    private fun setDialog(show: Boolean) {
        if (show) dialog?.show() else dialog?.dismiss()
    }



}