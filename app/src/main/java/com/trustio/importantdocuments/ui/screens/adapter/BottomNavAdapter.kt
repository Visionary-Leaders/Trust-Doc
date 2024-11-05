package com.trustio.importantdocuments.ui.screens.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.trustio.importantdocuments.ui.screens.favorite.FavoriteScreen
import com.trustio.importantdocuments.ui.screens.home.HomeScreen
import com.trustio.importantdocuments.ui.screens.search.SearchScreen
import com.trustio.importantdocuments.ui.screens.settings.SettingsScreen

class BottomNavAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeScreen()
            1 -> SearchScreen()
            2 -> FavoriteScreen()
            3 -> SettingsScreen()
            else -> SettingsScreen()
        }
    }
}