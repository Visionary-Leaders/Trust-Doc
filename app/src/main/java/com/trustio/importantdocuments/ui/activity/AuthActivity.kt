package com.trustio.importantdocuments.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.trustio.importantdocuments.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        FirebaseAuth.getInstance().getFirebaseAuthSettings()
            .setAppVerificationDisabledForTesting(true);
        // Force reCAPTCHA flow
        FirebaseAuth.getInstance().getFirebaseAuthSettings().forceRecaptchaFlowForTesting(true)
        FirebaseAuth.getInstance().setLanguageCode("fr")
        ;
    }
}