package com.trustio.importantdocuments.ui.screens.auth

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.registerReceiver
import androidx.fragment.app.viewModels
import com.google.android.gms.common.api.Status
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.databinding.OtpScreenBinding
import com.trustio.importantdocuments.utils.BaseFragment
import com.trustio.importantdocuments.utils.animationTransaction
import com.trustio.importantdocuments.utils.gone
import com.trustio.importantdocuments.utils.invisible
import com.trustio.importantdocuments.utils.sanitizePhoneNumber
import com.trustio.importantdocuments.utils.snackString
import com.trustio.importantdocuments.utils.visible
import com.trustio.importantdocuments.viewmodel.imp.AuthViewModelImp
import dagger.hilt.android.AndroidEntryPoint
import `in`.aabhasjindal.otptextview.OTPListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class OtpScreen : BaseFragment<OtpScreenBinding>(OtpScreenBinding::inflate) {
    private val model by viewModels<AuthViewModelImp>()
    private val countDownLiveData = MutableLiveData<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.confirmOtpFake.onEach {
            it?.onSuccess {
                binding.visibleOtpProgress.gone()
                binding.btnRegisterTxt.visible()
                findNavController().navigate(R.id.confirmRegisterScreen, Bundle().apply {putString("phone", arguments?.getString("phone")?:"") },
                    animationTransaction().build())
            }
            it?.onFailure {
                binding.visibleOtpProgress.gone()
                binding.btnRegisterTxt.visible()
                snackString("Error ${it.message}")
            }
        }.launchIn(lifecycleScope)
        countDownLiveData.observe(this, countDownObserver)

    }

    private val smsReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
                val extras = intent?.extras
                val status = extras?.get(SmsRetriever.EXTRA_STATUS) as? Status

                if (status != null) {
                    when (status.statusCode) {
                        CommonStatusCodes.SUCCESS -> {
                            // Get SMS message contents
                            val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                            extractOtpFromMessage(message)
                        }

                        CommonStatusCodes.TIMEOUT -> {
                            // Timeout happened while waiting for SMS
                            snackString("Failed to receive SMS")
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreate(savedInstanceState: Bundle?) {
//        startSmsListener()

        binding.apply {
            val phoneString =arguments?.getString("phone")?:""
            println(phoneString)
            donTHaveCodeContainer.setOnClickListener {
//                viewModel.registerUser(RegisterRequest("", phoneString.toString()))
            }
            otpDes.text = "$otpDes $phoneString"

            checkCodeOtpBtn.setOnClickListener {
                if (otpTxt.otp.length == 6) {
                    binding.visibleOtpProgress.visible()
                    binding.btnRegisterTxt.gone()
                    model.confirmOtpFake(binding.otpTxt.otp.toString())
                } else {
                    snackString("Please enter 6 digit code")
                }
            }
            otpTxt.otpListener = object : OTPListener {
                override fun onInteractionListener() {
                }

                override fun onOTPComplete(otp: String?) {
                    model.confirmOtpFake(otp.toString())

                    binding.visibleOtpProgress.visible()
                    binding.btnRegisterTxt.gone()
                }

            }

            initTimer()
            backOtp.setOnClickListener {
                println("Qayt")
                findNavController().popBackStack()
            }
        }
    }

    private fun initTimer() {
        var count = 60

        lifecycleScope.launchWhenResumed {
            while (count > 0) {
                delay(1000)
                count -= 1
                countDownLiveData.value = count
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private val countDownObserver = Observer<Int> {
        if (it != 0) {
            binding.resendTimeContainer.visible()
            binding.donTHaveCode.gone()
            binding.resendTime.text = "00:$it"
        } else {
            binding.resendTimeContainer.visibility = View.GONE
            binding.checkCodeOtpBtn.isEnabled = false

            binding.donTHaveCodeContainer.visibility = View.VISIBLE
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun startSmsListener() {
        val client = SmsRetriever.getClient(requireActivity())
        val task = client.startSmsRetriever()

        task.addOnSuccessListener {
            requireActivity().registerReceiver(
                smsReceiver,
                IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
            )
        }

        task.addOnFailureListener {
            Toast.makeText(requireActivity(), "Failed to start SMS retriever", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun extractOtpFromMessage(message: String) {
        // Assuming OTP is a 6-digit code
        val otp = Regex("\\d{6}").find(message)?.value
        if (otp != null) {
            // Automatically fill OTP view
            fillOtpView(otp)
        }
    }

    private fun fillOtpView(otp: String) {
        // Your logic to fill the OTP into the OTP input field
        binding.otpDes.setText(otp)  // Replace `otpView` with your actual OTP view reference
    }

    override fun onDestroy() {
        super.onDestroy()
//        requireActivity().unregisterReceiver(smsReceiver)
    }
}