package com.thinkgas.heatapp.ui.dashboard

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.thinkgas.heatapp.data.cache.AppCache
import com.thinkgas.heatapp.data.remote.model.TpiCategory
import com.thinkgas.heatapp.databinding.FragmentDashboardBinding
import com.thinkgas.heatapp.ui.dashboard.adapters.DashboardAdapter
import com.thinkgas.heatapp.ui.registration.RegistrationFragment
import com.thinkgas.heatapp.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import com.thinkgas.heatapp.R
import `in`.galaxyofandroid.spinerdialog.BuildConfig
import java.util.*


@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<DashboardFragmentArgs>()
    private val viewModel by viewModels<DashboardViewModel>()
    private var dialog: Dialog? = null
    private var locationDialog: Dialog? = null

    private lateinit var dashboardAdapter: DashboardAdapter
    private var categoryList: ArrayList<TpiCategory> = ArrayList()
    private var rolesList: ArrayList<String> = ArrayList()

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var locationSetting: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var locationPermissionSetting: ActivityResultLauncher<Intent>
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationSettingsRequest: LocationSettingsRequest
    private lateinit var requestLocationPermissionLauncher: ActivityResultLauncher<String>
    private var currentLocation: Location? = null
    private var settingsClient: SettingsClient? = null

    var latitude:Double? = 0.0
    var longitude:Double? = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        categoryList.clear()
        rolesList.clear()

        RegistrationFragment.apply {
            floorfacing = null
            gassification = null
            gaName = null
            zonalName = null
            caName = null
            colonyName = null
            districtName = null
            talukaName = null
            cityName = null
            areaName = null
            pincode = null
            stateName = null
            floorNo = null
        }
        categoryList.add(TpiCategory("Feasibility", R.drawable.ic_feasibility_icon, "reg_tpi"))
        categoryList.add(TpiCategory("LMC", R.drawable.ic_lmc_icon, "reg_lmc"))
        categoryList.add(TpiCategory("Riser", R.drawable.ic_riser, "reg_riser"))
        categoryList.add(TpiCategory("RFC/NG", R.drawable.ic_rfc_ng_icon, "reg_rfc"))
        categoryList.add(TpiCategory("GC", R.drawable.ic_gc_icon, "reg_gc"))
        categoryList.add(TpiCategory("Add GC Unregistered Customer", R.drawable.ic_registration, "reg_add_gc"))

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        settingsClient = LocationServices.getSettingsClient(requireActivity())

        // Creates a button that mimics a crash when pressed
//        val crashButton = Button(requireContext())
//        crashButton.text = "Test Crash"
//        crashButton.setOnClickListener {
//            throw RuntimeException("Test Crash") // Force a crash
//        }
//
//        requireActivity().addContentView(crashButton, ViewGroup.LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT))

        requestLocationPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted)
                {
                    getCurrentLocation()
                }
                else
                {
                    requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

        binding.apply {
            ivProfile.setOnClickListener {
                val logoutBuilder = AlertDialog.Builder(requireContext())
                logoutBuilder.setTitle("Log Out")
                logoutBuilder.setMessage("Are you sure want to log out?")
                logoutBuilder.setCancelable(false)
                logoutBuilder.setPositiveButton("Yes") { _, i ->
                    val preferences = activity?.getSharedPreferences("TPI_PREFS",
                        Context.MODE_PRIVATE
                    )
                    val editor: SharedPreferences.Editor = preferences!!.edit()
                    editor.clear()
                    editor.apply()
                    val directions = DashboardFragmentDirections.actionDashboardFragmentToLoginFragment()
                    findNavController().navigate(directions)

                }
                logoutBuilder.setNegativeButton("No") { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                val logoutAlert = logoutBuilder.create()
                logoutAlert.show()


            }
        }

        locationSetting = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            when (it.resultCode) {
                Activity.RESULT_OK -> {
                    getCurrentLocation()
                    locationDialog?.show()
                }
                Activity.RESULT_CANCELED -> {
                    requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    Toast.makeText(requireContext(), "Please turn on the location", Toast.LENGTH_SHORT).show()

                }
            }
        }

        requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        locationPermissionSetting = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    Activity.RESULT_OK -> {
                        requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                    Activity.RESULT_CANCELED -> {
                        requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }
            }

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.progress)
        dialog = builder.create()

        val locationBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        locationBuilder.setView(R.layout.location_progress)
        locationDialog = locationBuilder.create()

        if(AppCache.latitude!!.equals(0.0) && AppCache.longitude!!.equals(0.0))
        {
            locationDialog?.show()
        }
        mLocationCallback = object : LocationCallback()
        {
            override fun onLocationResult(locationResult: LocationResult)
            {
                super.onLocationResult(locationResult)
                locationDialog?.dismiss()
                locationResult.lastLocation.let { location ->
                    AppCache.latitude = location?.latitude
                    AppCache.longitude = location?.longitude
                }
            }
        }

        viewModel.getProfileResponse(args.sessionId)

        viewModel.profileResponse.observe(viewLifecycleOwner) { it ->
            if (it != null) {
                when (it.status) {
                    Status.SUCCESS -> {
                        if (!it.data!!.error) {
                            var tempList: MutableList<TpiCategory> = mutableListOf()
                            var tempList1: List<TpiCategory> = mutableListOf()
                            AppCache.isTpi = it.data.heatAppUserDetails.isTpi
                            val editor: SharedPreferences.Editor = activity?.getSharedPreferences("TPI_PREFS",
                                Context.MODE_PRIVATE
                            )!!.edit()
                            editor.putString("mobile", it.data.heatAppUserDetails.mobile)
                            editor.apply()
                            binding.apply {
                                tvName.text = it.data.heatAppUserDetails.agentName
                                tvJoinDate.text = "Member since ${it.data.heatAppUserDetails.dateOfJoining}"
                                it.data.heatAppUserDetails.rolesPermission.forEach { roles ->
                                    rolesList.add(roles.logRoles)
                                }

//                                tvJoinDate.text = it.data.heatAppUserDetails.

                                rolesList.forEach {
                                    categoryList.forEach { category ->
                                        if (category.id == it) {
                                            tempList.add(category)
                                        }
                                    }
                                }

//                                if(!AppCache.isTpi) {
//                                    tempList.add(
//                                        TpiCategory(
//                                            "Add GC Unregistered Customer",
//                                            R.drawable.ic_registration,
//                                            "reg_add_gc"
//                                        )
//                                    )
//                                }

                                dashboardAdapter = DashboardAdapter(
                                    requireContext(),
                                    tempList
                                ) { data: TpiCategory ->
                                    listItemClicked(data)
                                }
                                binding.rvCategory.adapter = dashboardAdapter
                            }

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error fetching user profile",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        setDialog(false)
                    }
                    Status.LOADING -> {
                        setDialog(true)
                    }
                    Status.ERROR -> {
                        setDialog(false)
                    }
                }
            }
        }
        return binding.root
    }

    private fun requestPermissions(permission: String)
    {
        val displayRational = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            permission)
        if (!displayRational) {
            Toast.makeText(
                requireActivity(),
                "Location Permisssion is required",
                Toast.LENGTH_LONG
            ).show()

            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts(
                "package",
                BuildConfig.APPLICATION_ID, null
            )
            intent.data = uri
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            locationPermissionSetting.launch(intent)
        }
        else
        {
            requestLocationPermissionLauncher.launch(permission)
        }
    }

    private fun getCurrentLocation()
    {
        settingsClient?.let { settings ->
            locationRequest = LocationRequest.create()
            locationRequest.interval = TimeUnit.SECONDS.toMillis(2)
            locationRequest.fastestInterval = TimeUnit.SECONDS.toMillis(2)
            locationRequest.priority = Priority.PRIORITY_HIGH_ACCURACY
            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(locationRequest)
            locationSettingsRequest = builder.build()
            settings
                .checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener {
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                        return@addOnSuccessListener
                    }
                    fusedLocationClient?.requestLocationUpdates(
                        locationRequest,
                        mLocationCallback, Looper.getMainLooper()
                    )
                }
                .addOnFailureListener {
                    when ((it as ApiException).statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            val intentSenderRequest =
                                IntentSenderRequest.Builder((it as ResolvableApiException).resolution)
                                    .build()
                            locationSetting.launch(intentSenderRequest)
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                    }
                }
        }

    }



    private fun listItemClicked(data: TpiCategory) {
        when (data.category) {
            "Feasibility" -> {
                val directions =
                    DashboardFragmentDirections.actionDashboardFragmentToFeasibilityHomeFragment(
                        args.sessionId
                    )
                findNavController().navigate(directions)
            }
            "LMC" -> {
                val directions = DashboardFragmentDirections.actionDashboardFragmentToLmcHomeFragment(args.sessionId)
                findNavController().navigate(directions)
            }
            "RFC/NG"->{
                val directions = DashboardFragmentDirections.actionDashboardFragmentToRfcHomeFragment(args.sessionId)
                findNavController().navigate(directions)
                
            }
            "GC"->{
                val directions = DashboardFragmentDirections.actionDashboardFragmentToGcHomeFragment(args.sessionId)
                findNavController().navigate(directions)
            }
            "Add GC Unregistered Customer"->{
                val directions = DashboardFragmentDirections.actionDashboardFragmentToGcUnregisteredListFragment(args.sessionId)
                findNavController().navigate(directions)
            }
        }
    }

    private fun setDialog(show: Boolean) {
        if (show) dialog!!.show() else dialog!!.dismiss()
    }


    override fun onDestroy() {
        super.onDestroy()
        dialog!!.dismiss()
        locationDialog?.dismiss()

    }

}