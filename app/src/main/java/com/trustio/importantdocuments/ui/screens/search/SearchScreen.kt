package com.trustio.importantdocuments.ui.screens.search

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import com.google.android.material.appbar.MaterialToolbar
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.databinding.SearchScreenBinding
import com.trustio.importantdocuments.utils.BaseFragment


class SearchScreen : BaseFragment<SearchScreenBinding>(SearchScreenBinding::inflate) {
    override fun onViewCreate(savedInstanceState: Bundle?) {

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(com.trustio.importantdocuments.R.menu.search_menu, menu)
        val search = menu.findItem(com.trustio.importantdocuments.R.id.action_search)
        binding.toolBar.title = ""
        val searchView: SearchView = MenuItemCompat.getActionView(search) as SearchView
        initSearch(searchView)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                binding.toolBar.title = ""
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun initSearch(searchView: SearchView) {
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.toolBar.title = ""
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                adapter.getFilter().filter(newText)
                binding.toolBar.title = ""
                return true
            }
        })
        searchView.setOnCloseListener {
            binding.toolBar.title = "Search"
            true

        }
    }

}