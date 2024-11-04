package com.trustio.importantdocuments.ui.screens

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.databinding.MainScreenBinding
import com.trustio.importantdocuments.ui.screens.adapter.BottomNavAdapter
import com.trustio.importantdocuments.utils.BaseFragment
import com.trustio.importantdocuments.utils.ZoomOutPageTransformer
import com.trustio.importantdocuments.utils.animationTransactionClearStack
import nl.joery.animatedbottombar.AnimatedBottomBar

class MainScreen: BaseFragment<MainScreenBinding>(MainScreenBinding::inflate) {
    override fun onViewCreate(savedInstanceState: Bundle?) {

        setupBottomNavigation()
    }



    private fun setupBottomNavigation() {
        val mainViewPager = binding.navHost
        mainViewPager.adapter= BottomNavAdapter(parentFragmentManager, lifecycle)
        mainViewPager.isUserInputEnabled = false
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
                        mainViewPager.currentItem=0

                    }
                    1 -> {
                        mainViewPager.currentItem=1
                    }
                    2 -> {
                        mainViewPager.currentItem=2
                    }
                    3->{
                        mainViewPager.currentItem=3
                    }
                    else -> {
                        mainViewPager.currentItem=3
                    }
                }
            }
        })
    }

}