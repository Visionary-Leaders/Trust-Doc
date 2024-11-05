package com.trustio.importantdocuments.ui.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.data.remote.request.CollectionRequest
import com.trustio.importantdocuments.databinding.MainScreenBinding
import com.trustio.importantdocuments.ui.screens.adapter.BottomNavAdapter
import com.trustio.importantdocuments.utils.BaseFragment
import com.trustio.importantdocuments.utils.showSnack
import com.trustio.importantdocuments.viewmodel.imp.HomeScreenViewModelImp
import dagger.hilt.android.AndroidEntryPoint
import nl.joery.animatedbottombar.AnimatedBottomBar

@AndroidEntryPoint
class MainScreen: BaseFragment<MainScreenBinding>(MainScreenBinding::inflate) {
   private val model by viewModels<HomeScreenViewModelImp>()
    private val adapter by lazy { BottomNavAdapter(parentFragmentManager,lifecycle) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.errorResponse.observe(this){
            showSnack(binding.root.rootView,it)
        }
        model.collectionAddedResponse.observe(this){
            showSnack(binding.root.rootView,it.message)
            setupBottomNavigation()
        }
    }
    @SuppressLint("InflateParams")
    override fun onViewCreate(savedInstanceState: Bundle?) {
        binding.fab.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(
                R.layout.folder_bottom_sheet, null
            )

            val normalMapOption = view.findViewById<MaterialCardView>(R.id.normal_map_option)
            val satelliteMapOption = view.findViewById<MaterialCardView>(R.id.satellite_map_option)
            val buttonClose = view.findViewById<FrameLayout>(R.id.button_close)
            normalMapOption.setOnClickListener {
                bottomSheetDialog.dismiss()
                showAddFolderSheet()

            }
            satelliteMapOption.setOnClickListener {

            }
            buttonClose.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.setContentView(view)
            bottomSheetDialog.show()

        }

        setupBottomNavigation()
    }


    private fun showAddFolderSheet(){
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(
            R.layout.add_folder_sheet, null
        )

        val cancelBtn = view.findViewById<AppCompatButton>(R.id.cancel_btn)
        val submitBtn = view.findViewById<AppCompatButton>(R.id.submit_btn)
        val addFolderTxt =view.findViewById<AppCompatButton>(R.id.add_folder_txt)

        cancelBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        submitBtn.setOnClickListener {
            if (addFolderTxt.text.isNotEmpty()){
                model.addCollection(CollectionRequest(addFolderTxt.text.toString()))
                bottomSheetDialog.dismiss()
            }
        }
    }

    private fun setupBottomNavigation() {
        val mainViewPager = binding.navHost
        mainViewPager.adapter= adapter
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
                        mainViewPager.currentItem = 4
                    }
                }
            }
        })
    }

}