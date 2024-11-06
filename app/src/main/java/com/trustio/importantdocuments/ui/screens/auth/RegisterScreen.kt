package com.trustio.importantdocuments.ui.screens.auth

import android.os.Build
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.play.integrity.internal.s
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.databinding.RegisterPageBinding
import com.trustio.importantdocuments.utils.BaseFragment
import com.trustio.importantdocuments.utils.ResultApp
import com.trustio.importantdocuments.utils.animationTransaction
import com.trustio.importantdocuments.utils.createPhoneNumberPlateEditText
import com.trustio.importantdocuments.utils.gone
import com.trustio.importantdocuments.utils.isValidPhoneNumber
import com.trustio.importantdocuments.utils.sanitizePhoneNumber
import com.trustio.importantdocuments.utils.showNetworkDialog
import com.trustio.importantdocuments.utils.snackString
import com.trustio.importantdocuments.utils.visible
import com.trustio.importantdocuments.viewmodel.imp.AuthViewModelImp
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class RegisterScreen : BaseFragment<RegisterPageBinding>(RegisterPageBinding::inflate) {
    private val model by viewModels<AuthViewModelImp>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.noInternetLiveData.observe(this) {
            showNetworkDialog(requireActivity(), null)
//            enableCarInputs()

        }
        model.successResponseLiveData.observe(this) {
            when (it) {
                is ResultApp.CodeSent -> {
                    println("I need : " + it.code)
                    val authCode = it.code
                    binding.clickRegister.isEnabled = true
                    binding.registerProgress.gone()
                    binding.clickRegister.text ="Register"
                    snackString("Sms Sent")
                    val phone = sanitizePhoneNumber(binding.phoneRegister.text.toString())
                    findNavController().navigate(
                        R.id.otpScreen,
                        Bundle().apply {
                            putString(
                                "phone",
                                phone
                            )
                            putString ("authCode", authCode)
                        },
                        animationTransaction().build()
                    )

                }

                is ResultApp.Error -> {

                    Toast.makeText(requireActivity(), it.exception.message.toString(), Toast.LENGTH_SHORT).show()
//                    enableCarInputs()

                    binding.clickRegister.isEnabled = true
                    binding.registerProgress.gone()
                    binding.clickRegister.text ="Register"
                }

                is ResultApp.Success -> {
                    println("WHAT HAPPANNED")
//                    enableCarInputs()

                    binding.clickRegister.isEnabled = true
                    binding.registerProgress.gone()

                    binding.clickRegister.text ="Register"                }
            }
        }
        model.errorResponseLiveData.observe(this) {
            println("error")
            binding.clickRegister.isEnabled = true
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
            binding.clickRegister.isEnabled = true
            binding.registerProgress.gone()
            binding.clickRegister.text ="Register"

        }
    }

    override fun onViewCreate(savedInstanceState: Bundle?) {
        initClicks()
        createPhoneNumberPlateEditText(binding.phoneRegister,binding.textInputLayoutPhone)

//        val phoneNumber = "+998943017410"
//
//        val firebaseAuth = FirebaseAuth.getInstance()
//        val firebaseAuthSettings = firebaseAuth.firebaseAuthSettings
//
//
//        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
//            .setPhoneNumber(phoneNumber)
//            .setTimeout(60L, TimeUnit.SECONDS)
//            .setActivity(requireActivity())
//            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                    // Instant verification is applied and a credential is directly returned.
//                    // ...
//                }
//                override fun onCodeSent(
//                    verificationId: String,
//                    forceResendingToken: PhoneAuthProvider.ForceResendingToken,
//                ) {
//                    // Save the verification id somewhere
//                    // ...
//                    Log.d("jarayon", "onCodeSent: $verificationId")
//                    // The corresponding whitelisted code above should be used to complete sign-in.
//
//                }
//
//                override fun onVerificationFailed(p0: FirebaseException) {
//                    Log.d("jarayon", "onVerificationFailed: $p0")
//                }
//
//                // ...
//            })
//            .build()
//        PhoneAuthProvider.verifyPhoneNumber(options)
//        // Force reCAPTCHA flow
        FirebaseAuth.getInstance().firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
//



    }

    private fun initClicks() {
        binding.openLoginTxt.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.clickRegister.setOnClickListener {
            binding.registerProgress.visible()
            binding.clickRegister.text = ""
            model.sendSms(binding.phoneRegister.text.toString(), requireActivity())

        }
    }
//    private fun enableCarInputs() {
//        binding.registerPhone.isEnabled = true;
//        binding.driverPassNumber.isEnabled = true;
//        binding.registerName.isEnabled = true;
//        binding.driverPassSere.isEnabled = true;
//
//    }
//
//    private fun disableDriverInputs() {
//        binding.registerPhone.isEnabled = false;
//        binding.driverPassNumber.isEnabled = false;
//        binding.registerName.isEnabled = false;
//        binding.driverPassSere.isEnabled = false;
//    }

}