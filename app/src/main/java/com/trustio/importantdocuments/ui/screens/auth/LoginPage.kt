package com.trustio.importantdocuments.ui.screens.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.data.remote.request.LoginRequest
import com.trustio.importantdocuments.databinding.LoginPageBinding
import com.trustio.importantdocuments.ui.activity.MainActivity
import com.trustio.importantdocuments.utils.BaseFragment
import com.trustio.importantdocuments.utils.animationTransaction
import com.trustio.importantdocuments.utils.isValidPhoneNumber
import com.trustio.importantdocuments.utils.sanitizePhoneNumber
import com.trustio.importantdocuments.utils.setSlideUp
import com.trustio.importantdocuments.utils.slideStart
import com.trustio.importantdocuments.utils.slideTop
import com.trustio.importantdocuments.utils.slideUp
import com.trustio.importantdocuments.utils.snackString
import com.trustio.importantdocuments.viewmodel.imp.AuthViewModelImp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class LoginPage : BaseFragment<LoginPageBinding>(LoginPageBinding::inflate) {
    private val model by viewModels<AuthViewModelImp>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.loginResponse.onEach {
            it?.onSuccess {
                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()

            }
            it?.onFailure {
                snackString(it.message)
            }
        }.launchIn(lifecycleScope)
    }

    override fun onViewCreate(savedInstanceState: Bundle?) {
        initClicks()
        initPhone()
        setupAnimToViews()
    }

    private fun setupAnimToViews() {
        binding.loginClick.slideUp(777, 0)
        binding.editText.slideUp(777, 0)
        binding.welcomeFrame.slideStart(777, 0)
        binding.imageView3.animation = setSlideUp()
        binding.forgotPasswordTxt.slideStart(777,0)
    }

    private fun initPhone(){
        binding.phoneLogin.addTextChangedListener {
            val phoneNumber = it.toString()
            binding.loginClick.isEnabled = isValidPhoneNumber(phoneNumber) && binding.phoneLogin.text.toString().isNotEmpty()
        }
    }
    private fun initClicks() {
        binding.regiserScreenOpen.setOnClickListener {
            findNavController().navigate(R.id.registerScreen, null, animationTransaction().build())
        }
        binding.loginClick.setOnClickListener {
            model.loginUser(LoginRequest(sanitizePhoneNumber(binding.phoneLogin.text.toString()), binding.passwordEditText.text.toString()))
        }
    }
}