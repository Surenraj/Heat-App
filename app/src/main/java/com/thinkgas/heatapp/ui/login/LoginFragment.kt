package com.thinkgas.heatapp.ui.login

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.thinkgas.heatapp.R
import com.thinkgas.heatapp.databinding.FragmentLoginBinding
import com.thinkgas.heatapp.utils.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel by viewModels<LoginViewModel>()
    private var dialog: Dialog? = null
    private var otp: Int = 0
    private var passwordFlag = false
    private var phoneNumber: String? = null
    private var sessionId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = activity?.getSharedPreferences("TPI_PREFS", MODE_PRIVATE)
        sessionId = sharedPreferences?.getString("session_id", null)
        if (sessionId != null) {
            val directions =
                LoginFragmentDirections.actionLoginFragmentToDashboardFragment(sessionId!!)
            findNavController().navigate(directions)
        }
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(com.thinkgas.heatapp.R.layout.progress)
        dialog = builder.create()

        binding.apply {
            etMobile.transformationMethod = HideReturnsTransformationMethod.getInstance()
            etPassword.setOnTouchListener(object : View.OnTouchListener{
                override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                    if(p1?.action == MotionEvent.ACTION_UP) {
                        if(p1?.rawX!! >= etPassword.right - etPassword.compoundDrawables[2].bounds.width() - etPassword.paddingRight) {
                            // your action for drawable click event
                            passwordFlag = !passwordFlag
                            if(passwordFlag) {
//                                etPassword.inputType = InputType.TYPE_CLASS_TEXT
//                                etPassword.setText(etPassword.text)
                                etPassword.setCompoundDrawablesWithIntrinsicBounds(null,null,ContextCompat.getDrawable(requireContext(),
                                    R.drawable.ic__hide_eye),null)
                                etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                            }else{
//                                etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
//                                etPassword.setText(etPassword.text)
                                etPassword.setCompoundDrawablesWithIntrinsicBounds(null,null,ContextCompat.getDrawable(requireContext(),
                                    R.drawable.ic_baseline_remove_red_eye_24),null)
                                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()

                            }
                            etPassword.setSelection(etPassword.text.length)
                            return true;
                        }
                    }
                    return false;
                }

            })

            btnLogin.setOnClickListener {
                if (btnLogin.text.contains("login", true))
                {
                    phoneNumber = etMobile.text.toString()
                    val password = ""
                    if (validateMobileNumber(phoneNumber!!) && phoneNumber != null) {
                        val params = HashMap<String, String>()
                        params["mobile_no"] = phoneNumber!!
                        params["password"] = ""
                        loginViewModel.getOtpValue(params)
                    } else {
                        etMobile.error = "Invalid mobile number"
                        etMobile.requestFocus()
                    }
                } else {
                    if (otp != 0 && phoneNumber != null) {
                        if(etOtp.text.isBlank() || etOtp.text.length < 4){
                            etOtp.error = "Enter Valid OTP"
                            etOtp.requestFocus()
                            return@setOnClickListener
                        }
                        val params = HashMap<String, String>()
                        params["mobile"] = phoneNumber!!
                        params["otp"] = etOtp.text.toString()
                        loginViewModel.validateOtpValue(params)
                    }
                }
            }
        }

        loginViewModel.otpValue.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it.status) {
                    Status.LOADING -> {
                        setDialog(true)
                    }
                    Status.SUCCESS -> {
                        if (!it.data!!.error) {
                            val data = it.data
                            otp = data.otp
                            binding.apply {
                                etMobile.visibility = View.GONE
//                                etPassword.visibility = View.GONE
                                etOtp.visibility = View.VISIBLE
                                btnLogin.text = "Verify OTP"
                            }
                            Toast.makeText(requireContext(), data.message, Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(requireContext(), "Invalid password", Toast.LENGTH_SHORT)
                                .show()
                        }
                        setDialog(false)

                    }
                    Status.ERROR -> {
                        setDialog(false)
                        Toast.makeText(requireContext(), "Error Signing in..", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        loginViewModel.validateValue.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it.status) {
                    Status.LOADING -> {
                        setDialog(true)
                    }
                    Status.SUCCESS -> {
                        if (!it.data!!.error) {
                            Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()
                            val editor: SharedPreferences.Editor = activity?.getSharedPreferences("TPI_PREFS", MODE_PRIVATE)!!.edit()
                            editor.putString("session_id", it.data.sessionId)
                            editor.apply()
                            val directions = LoginFragmentDirections.actionLoginFragmentToDashboardFragment(it.data.sessionId)
                            findNavController().navigate(directions)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "OTP verification failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        setDialog(false)
                    }
                    Status.ERROR -> {
                        setDialog(false)
                    }
                }
            }
        }
        return binding.root
    }

    private fun validateMobileNumber(number: String): Boolean {
        return number.length >= 10
    }

    private fun setDialog(show: Boolean) {
        if (show) dialog!!.show() else dialog!!.dismiss()
    }

}