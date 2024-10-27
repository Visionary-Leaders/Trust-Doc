package com.trustio.importantdocuments.ui.screens.auth

import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.play.integrity.internal.s
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.databinding.RegisterPageBinding
import com.trustio.importantdocuments.utils.BaseFragment
import com.trustio.importantdocuments.utils.animationTransaction
import com.trustio.importantdocuments.utils.isValidPhoneNumber
import com.trustio.importantdocuments.utils.sanitizePhoneNumber
import com.trustio.importantdocuments.utils.snackString
import com.trustio.importantdocuments.viewmodel.imp.AuthViewModelImp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterScreen : BaseFragment<RegisterPageBinding>(RegisterPageBinding::inflate) {
    private val model by viewModels<AuthViewModelImp>()

    override fun onViewCreate(savedInstanceState: Bundle?) {
        initClicks()
        lifecycleScope.launchWhenStarted {
            model.smsState.collect { result ->
                result?.let {
                    if (it.isSuccess) {
                      snackString("Sms Sent")
                        val phone = sanitizePhoneNumber(binding.phoneRegister.text.toString())
                        findNavController().navigate(R.id.otpScreen, Bundle().apply { putString("phone", phone) }, animationTransaction().build())
                    } else {
                        snackString("Error ${it.exceptionOrNull()}")
                    }
                }
            }
        }

        binding.phoneRegister.addTextChangedListener {
            val phoneNumber = it.toString()
            binding.clickRegister.isEnabled = isValidPhoneNumber(phoneNumber)
        }
    }

    private fun initClicks() {
        binding.openLoginTxt.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.clickRegister.setOnClickListener {
            model.sendSms(binding.phoneRegister.text.toString(), requireActivity())

        }
    }
}