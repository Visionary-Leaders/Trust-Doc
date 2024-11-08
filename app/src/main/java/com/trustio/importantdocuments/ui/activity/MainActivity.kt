package com.trustio.importantdocuments.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Button
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.databinding.ActivityMainBinding
import com.trustio.importantdocuments.utils.animationTransactionClearStack
import com.trustio.importantdocuments.utils.hasConnection
import dagger.hilt.android.AndroidEntryPoint
import nl.joery.animatedbottombar.AnimatedBottomBar

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        val noInternetLayout = findViewById<View>(R.id.noInternetLayout)
        val retryButton = findViewById<Button>(R.id.retryButton)

        retryButton.setOnClickListener {
            checkInternetAndSetupGraph(navHostFragment, noInternetLayout)
        }

        // Initial check
        checkInternetAndSetupGraph(navHostFragment, noInternetLayout)
    }

    private fun checkInternetAndSetupGraph(navHostFragment: NavHostFragment, noInternetLayout: View) {
        if (hasConnection()) {
            noInternetLayout.visibility = View.GONE
            navHostFragment.view?.visibility = View.VISIBLE
            setupNavGraph(navHostFragment)
        } else {
            noInternetLayout.visibility = View.VISIBLE
            navHostFragment.view?.visibility = View.GONE
        }
    }

    private fun setupNavGraph(navHostFragment: NavHostFragment) {
        if (!::navController.isInitialized) {
            navController = navHostFragment.findNavController()
            navController.setGraph(R.navigation.app_graph)
        }
    }



}
