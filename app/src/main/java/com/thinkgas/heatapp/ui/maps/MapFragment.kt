package com.thinkgas.heatapp.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.thinkgas.heatapp.R
import com.thinkgas.heatapp.data.cache.AppCache
import com.thinkgas.heatapp.databinding.FragmentMapBinding
import com.thinkgas.heatapp.utils.AppUtils
import com.thinkgas.heatapp.utils.AppUtils.bitmapDescriptorFromVector
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment(),OnMapReadyCallback {
    private var _binding:FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var googleMap: GoogleMap
    private val DEFAULT_ZOOM = 18f
    private val args by navArgs<MapFragmentArgs>()

    companion object {
        var mapsFragment: SupportMapFragment? = null
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMapBinding.inflate(inflater,container,false)

        mapsFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapsFragment?.getMapAsync(this)

        binding.apply {
            if(args.type == "GC"){
                tvSave.visibility = View.VISIBLE
            }
            tvSave.setOnClickListener{
                Toast.makeText(requireContext(), "Location saved successfully", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }

        }

        val city = AppUtils.getCityName(args.latitude.toDouble(),args.longitude.toDouble(),requireContext())
        binding.txtUserCurrentLocation.text = city

        return binding.root
    }

    private fun goToMyLocation() {
        googleMap.clear()
        val currLoc = LatLng(AppCache.latitude!!, AppCache.longitude!!)
        val markerOptions = MarkerOptions()
            .position(currLoc)
            .anchor(0.5f, 0.5f)
            .icon(
                bitmapDescriptorFromVector(
                    requireActivity(),
                    R.drawable.ic_location_ripple_effect_2
                )
            )
        googleMap.addMarker(markerOptions)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLoc, DEFAULT_ZOOM))
    }

    private fun goToCustomerLocation() {
        val currLoc = LatLng(args.latitude.toDouble(), args.longitude.toDouble())
        val markerOptions = MarkerOptions()
            .position(currLoc)
            .anchor(0.5f, 0.5f)
            .icon(
                bitmapDescriptorFromVector(
                    requireActivity(),
                    R.drawable.ic_location_ripple_effect_2
                )
            )
        googleMap.addMarker(markerOptions)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLoc, DEFAULT_ZOOM))
    }

    override fun onMapReady(gMap: GoogleMap) {
        try {
            googleMap = gMap
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.apply {
                this.isMapToolbarEnabled = true
            }
            goToCustomerLocation()
//            binding.fabMyLocation.setOnClickListener {
//                goToMyLocation()
//            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

}