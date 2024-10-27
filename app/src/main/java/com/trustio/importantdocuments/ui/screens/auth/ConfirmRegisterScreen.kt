package com.trustio.importantdocuments.ui.screens.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.data.remote.request.RegisterRequest
import com.trustio.importantdocuments.databinding.ConfirmRegisterScreenBinding
import com.trustio.importantdocuments.ui.activity.AuthActivity
import com.trustio.importantdocuments.ui.activity.MainActivity
import com.trustio.importantdocuments.utils.BaseFragment
import com.trustio.importantdocuments.utils.snackString
import com.trustio.importantdocuments.viewmodel.imp.AuthViewModelImp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class ConfirmRegisterScreen :
    BaseFragment<ConfirmRegisterScreenBinding>(ConfirmRegisterScreenBinding::inflate) {
    private val model by viewModels<AuthViewModelImp>()
    override fun onViewCreate(savedInstanceState: Bundle?) {
        binding.loginClick.isEnabled = false
        binding.loginClick.alpha = 0.5f
        binding.loginClick.setOnClickListener {
            val phone = arguments?.getString("phone") ?: ""
            model.registerUser(
                RegisterRequest(
                    first_name = binding.firstNameTxt.text.toString(),
                    last_name = binding.lastNameEdittext.text.toString(),
                    email = binding.emailTxt.text.toString(),
                    phone_number = phone,
                    password = binding.passwordTxt.text.toString()
                )
            )
        }
        model.registerResponse.onEach {
            it?.onSuccess {
                snackString("User Successfully Registered")
                val intent = Intent(requireActivity(), MainActivity::class.java)
                requireActivity().apply {
                    startActivity(intent)
                    finish()
                }

            }
            it?.onFailure {
                snackString("Something Wrong : ${it.message}")
            }
        }.launchIn(lifecycleScope)
        setupTextWatchers()
    }

    private fun setupTextWatchers() {
        val fields = listOf(
            binding.firstNameTxt,
            binding.lastNameEdittext,
            binding.emailTxt,
            binding.passwordTxt
        )

        fields.forEach { editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    checkFieldsForValidation()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    private fun checkFieldsForValidation() {
        val isFirstNameValid = binding.firstNameTxt.text.toString().isNotBlank()
        val isLastNameValid = binding.lastNameEdittext.text.toString().isNotBlank()
        val isEmailValid = binding.emailTxt.text.toString().contains("@")
        val isPasswordValid = binding.passwordTxt.text.toString().length >= 6

        val isAllValid = isFirstNameValid && isLastNameValid && isEmailValid && isPasswordValid
        setButtonState(isAllValid)
    }

    private fun setButtonState(isEnabled: Boolean) {
        if (isEnabled && !binding.loginClick.isEnabled) {
            binding.loginClick.apply {
                this.isEnabled = true
                this.alpha = 1.0f
                startAnimation(createFadeInAnimation())
            }
        } else if (!isEnabled && binding.loginClick.isEnabled) {
            binding.loginClick.apply {
                this.isEnabled = false
                this.alpha = 0.5f
                startAnimation(createFadeOutAnimation())
            }
        }
    }

    private fun createFadeInAnimation(): AlphaAnimation {
        return AlphaAnimation(0.5f, 1.0f).apply {
            duration = 500
            fillAfter = true
        }
    }

    private fun createFadeOutAnimation(): AlphaAnimation {
        return AlphaAnimation(1.0f, 0.5f).apply {
            duration = 500
            fillAfter = true
        }
    }
}