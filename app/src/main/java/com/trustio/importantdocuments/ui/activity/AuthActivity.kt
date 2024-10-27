package com.trustio.importantdocuments.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.databinding.ActivityAuthBinding
import com.trustio.importantdocuments.utils.visible
import com.trustio.importantdocuments.viewmodel.imp.IntroViewModelImpl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private val model by viewModels<IntroViewModelImpl>()
    private lateinit var viewBinding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAuthBinding.inflate(layoutInflater)
        model.loginScreenLiveData.observe(this, getStartedScreenObserver)
        model.homeScreenLiveData.observe(this, openHomeScreenObserver)
        setContentView(viewBinding.root)
        FirebaseAuth.getInstance().getFirebaseAuthSettings()
            .setAppVerificationDisabledForTesting(true);
        // Force reCAPTCHA flow
        FirebaseAuth.getInstance().getFirebaseAuthSettings().forceRecaptchaFlowForTesting(true)
        FirebaseAuth.getInstance().setLanguageCode("fr")

        model.checkPage()
    }

    private val openHomeScreenObserver = Observer<Unit> {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    private val getStartedScreenObserver = Observer<Unit> {
        viewBinding.navHost.visible()
    }
}