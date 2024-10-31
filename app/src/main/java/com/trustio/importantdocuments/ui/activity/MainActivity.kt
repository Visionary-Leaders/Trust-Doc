package com.trustio.importantdocuments.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuInflater
import androidx.navigation.fragment.NavHostFragment
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.databinding.ActivityAuthBinding
import com.trustio.importantdocuments.databinding.ActivityMainBinding
import nl.joery.animatedbottombar.AnimatedBottomBar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNavigation()
    }
    private fun setupBottomNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        val navController = navHostFragment.navController

        binding.homeNavigation.setOnTabSelectListener(object :
            AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                when (newIndex) {
                    0 -> {
                        navController.navigate(R.id.button_home)
                    }
                    1 -> {
                        navController.navigate(R.id.button_search)
                    }

                    2 -> {
                        navController.navigate(R.id.button_fav)
                    }

                    else -> {
                        navController.navigate(R.id.button_settings)
                    }
                }
            }


        })
    }

}