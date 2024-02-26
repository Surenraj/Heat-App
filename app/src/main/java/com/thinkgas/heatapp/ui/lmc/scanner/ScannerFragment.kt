package com.thinkgas.heatapp.ui.lmc.scanner

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.thinkgas.heatapp.RFCExtensionViewModel
import com.thinkgas.heatapp.databinding.FragmentScannerBinding
import com.thinkgas.heatapp.ui.lmc.LmcConnectionFragment
import com.thinkgas.heatapp.ui.ng.RfcExtensionFragment
import com.thinkgas.heatapp.ui.rfc.RfcViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScannerFragment : Fragment() {
    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var codeScanner: CodeScanner
    private val rfcViewModel by activityViewModels<RFCExtensionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        codeScanner = CodeScanner(requireActivity(), binding.scannerView)
        codeScanner.startPreview()

        val fragmentName = arguments?.getString("fragmentName")

        codeScanner.decodeCallback = DecodeCallback {
            when (fragmentName) {
                "LmcConnectionFragment" -> {
                    activity?.runOnUiThread {
                        LmcConnectionFragment.qrValue = it.text
                        findNavController().navigateUp()
                    }
                }
                "RfcExtensionFragment" -> {
                    activity?.runOnUiThread {
                        RfcExtensionFragment.qrValue = it.text
                        rfcViewModel.setQrValue(it.text)
                        findNavController().navigateUp()
                    }
                }
            }
        }

        codeScanner.errorCallback = ErrorCallback {
            when (fragmentName) {
                "LmcConnectionFragment" -> {
                    activity?.runOnUiThread {
                        LmcConnectionFragment.qrError = it.message
                        findNavController().navigateUp()
                    }
                }
                "RfcExtensionFragment" -> {
                    activity?.runOnUiThread {
                        RfcExtensionFragment.qrError = it.message
                        findNavController().navigateUp()
                    }
                }
            }
        }

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        codeScanner.stopPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        codeScanner.stopPreview()
    }
}