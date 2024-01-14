package com.thinkgas.heatapp.ui.lmc.scanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.thinkgas.heatapp.databinding.FragmentScannerBinding
import com.thinkgas.heatapp.ui.lmc.LmcConnectionFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScannerFragment : Fragment() {
    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var codeScanner: CodeScanner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        codeScanner = CodeScanner(requireActivity(), binding.scannerView)
        codeScanner.startPreview()
        codeScanner.decodeCallback = DecodeCallback {
            LmcConnectionFragment.qrValue = it.text
            findNavController().navigateUp()
        }
//        codeScanner.errorCallback = ErrorCallback {
//            LmcConnectionFragment.qrError = it.message
//            binding.progressBar.visibility = View.VISIBLE
//            Handler(Looper.getMainLooper()).postDelayed({
//                binding.progressBar.visibility = View.GONE
//                requireActivity().onBackPressed()
//            },2000)
//        }
        return binding.root
    }
}