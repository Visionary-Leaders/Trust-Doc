package com.trustio.importantdocuments.ui.screens.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.data.remote.response.section.SectionsResponseItem
import com.trustio.importantdocuments.databinding.HomeScreenBinding
import com.trustio.importantdocuments.ui.screens.home.adapter.CollectionAdapter
import com.trustio.importantdocuments.utils.BaseFragment
import com.trustio.importantdocuments.utils.gone
import com.trustio.importantdocuments.utils.showSnack
import com.trustio.importantdocuments.utils.snackString
import com.trustio.importantdocuments.utils.visible
import com.trustio.importantdocuments.viewmodel.imp.HomeScreenViewModelImp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class HomeScreen : BaseFragment<HomeScreenBinding>(HomeScreenBinding::inflate) {
    private val model by viewModels<HomeScreenViewModelImp>()
    private val adapter by lazy { CollectionAdapter() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.collectionList.observe(this) {response->
            binding.collectionProgress.gone()
            binding.collectionRv.visible()
            binding.collectionRv.adapter = adapter
            adapter.submitList(response)
            binding.searchFolder.addTextChangedListener {
                val query = it?.toString() ?: ""
                filterList(query,response)
            }
        }
        model.errorResponse.observe(this) {

            binding.collectionProgress.gone()
            binding.collectionRv.visible()
            showSnack(binding.root, it)
        }
    }

    private fun convertCyrillicToLatin(text: String): String {
        return text.replace("а", "a").replace("б", "b").replace("в", "v")
            .replace("г", "g").replace("д", "d").replace("е", "e")
            .replace("ё", "yo").replace("ж", "j").replace("з", "z")
            .replace("и", "i").replace("й", "y").replace("к", "k")
            .replace("л", "l").replace("м", "m").replace("н", "n")
            .replace("о", "o").replace("п", "p").replace("р", "r")
            .replace("с", "s").replace("т", "t").replace("у", "u")
            .replace("ф", "f").replace("х", "x").replace("ц", "ts")
            .replace("ч", "ch").replace("ш", "sh").replace("щ", "shch")
            .replace("ъ", "'").replace("ы", "y").replace("ь", "'")
            .replace("э", "e").replace("ю", "yu").replace("я", "ya")
    }



    private fun filterList(query: String,yourListData: List<SectionsResponseItem>) {
        val convertedQuery = convertCyrillicToLatin(query)

        val filteredList = yourListData.filter {
            it.name.contains(convertedQuery, ignoreCase = true)
        }
        adapter.submitList(filteredList)
    }
    override fun onViewCreate(savedInstanceState: Bundle?) {}

}