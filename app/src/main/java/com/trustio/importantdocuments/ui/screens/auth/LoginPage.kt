package com.trustio.importantdocuments.ui.screens.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.databinding.LoginPageBinding
import com.trustio.importantdocuments.utils.BaseFragment
import com.trustio.importantdocuments.utils.animationTransaction
import com.trustio.importantdocuments.utils.setSlideUp
import com.trustio.importantdocuments.utils.slideStart
import com.trustio.importantdocuments.utils.slideTop
import com.trustio.importantdocuments.utils.slideUp

class LoginPage : BaseFragment<LoginPageBinding>(LoginPageBinding::inflate) {
    override fun onViewCreate(savedInstanceState: Bundle?) {
        initClicks()
        setupAnimToViews()
    }

    private fun setupAnimToViews() {
        binding.loginClick.slideUp(777, 0)
        binding.editText.slideUp(777, 0)
        binding.welcomeFrame.slideStart(777, 0)
        binding.imageView3.animation = setSlideUp()
        binding.forgotPasswordTxt.slideStart(777,0)
    }

    private fun initClicks() {
        binding.regiserScreenOpen.setOnClickListener {
            findNavController().navigate(R.id.registerScreen, null, animationTransaction().build())
        }
    }
}