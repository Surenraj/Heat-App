package com.thinkgas.heatapp.network

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thinkgas.heatapp.databinding.FragmentNoNetworkBinding
import com.thinkgas.heatapp.ui.dashboard.DashboardFragmentDirections

class NoNetworkFragment : Fragment() {

    private var _binding: FragmentNoNetworkBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNoNetworkBinding.inflate(inflater, container, false)

        binding.apply {
            btnRetry.setOnClickListener {
//                val directions = DashboardFragmentDirections.actionDashboardFragmentToRfcHomeFragment()
//                findNavController().navigate(directions)
            }
        }

        return binding.root
    }

}