package com.trustio.importantdocuments.ui.screens.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.Zbekz.tashkentmetro.utils.enums.CurrentScreenEnum
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.data.local.shp.AppReference
import com.trustio.importantdocuments.databinding.SettingsScreenBinding
import com.trustio.importantdocuments.ui.activity.AuthActivity
import com.trustio.importantdocuments.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.log

@AndroidEntryPoint
class SettingsScreen : BaseFragment<SettingsScreenBinding>(SettingsScreenBinding::inflate) {
    @Inject
    lateinit var appReference: AppReference
    @SuppressLint("SetTextI18n")
    override fun onViewCreate(savedInstanceState: Bundle?) {
        binding.apply {
            userPhone.text= "Phone : +998"+ appReference.phone
            logoutButton.setOnClickListener {
                appReference.currentScreenEnum =CurrentScreenEnum.INTRO
                appReference.token =""
                val intent =Intent(requireActivity(),AuthActivity::class.java)
                requireActivity().startActivity(intent)
                requireActivity().finish()
            }
        }
    }

}